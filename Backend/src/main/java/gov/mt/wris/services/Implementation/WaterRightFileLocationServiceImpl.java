package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OfficeCreationDto;
import gov.mt.wris.dtos.OfficeDto;
import gov.mt.wris.dtos.OfficePageDto;
import gov.mt.wris.dtos.OfficeSortColumn;
import gov.mt.wris.dtos.ResponsibleOfficeDto;
import gov.mt.wris.dtos.StaffCreationDto;
import gov.mt.wris.dtos.StaffDto;
import gov.mt.wris.dtos.StaffPageDto;
import gov.mt.wris.dtos.StaffSortColumn;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.models.Office;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightOffice;
import gov.mt.wris.models.WaterRightStaff;
import gov.mt.wris.repositories.WaterRightOfficeRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.repositories.WaterRightStaffRepository;
import gov.mt.wris.services.WaterRightFileLocationService;

@Service
public class WaterRightFileLocationServiceImpl implements WaterRightFileLocationService {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightFileLocationService.class);

    @Autowired
    WaterRightRepository waterRepo;

    @Autowired
    WaterRightOfficeRepository officeRepo;

    @Autowired
    WaterRightStaffRepository staffRepo;
    
    public ResponsibleOfficeDto getResponsibleOffice(Long waterRightId) {
        LOGGER.info("Getting the Responsible Office for the water right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWater.get();

        ResponsibleOfficeDto dto = getResponsibleOfficeDto(waterRight.getResponsibleOffice());
        
        return dto;
    }

    private ResponsibleOfficeDto getResponsibleOfficeDto(Office office) {
        ResponsibleOfficeDto dto = new ResponsibleOfficeDto();
        if(office != null) dto.setOffice(office.getDescription());
        if(office != null) dto.setOfficeId(office.getId().longValue());
        return dto;
    }

    public ResponsibleOfficeDto editResponsibleOffice(Long waterRightId, ResponsibleOfficeDto dto) {
        LOGGER.info("Changing the responsible Office");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWater.get();

        waterRight.setOfficeId(BigDecimal.valueOf(dto.getOfficeId()));

        waterRight = waterRepo.save(waterRight);

        ResponsibleOfficeDto returnDto = getResponsibleOfficeDto(waterRight.getResponsibleOffice());

        return returnDto;
    }

    public OfficePageDto getWaterRightsOffices(Long waterRightId,
        int pageNumber,
        int pageSize,
        OfficeSortColumn sortDTOColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the offices for a water right");

        String sortColumn = getOfficeSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Page<WaterRightOffice> resultsPage = officeRepo.findWaterRightsOffices(pageable, waterId);

        LocalDateTime earliestCreatedByDate = officeRepo.minCreatedDate(waterId);

        OfficePageDto page = new OfficePageDto();

        page.setResults(resultsPage.getContent().stream().map(xref -> {
            return getOfficeDto(xref, earliestCreatedByDate);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortDTOColumn);
        page.setSortDirection(sortDirection);

        LocalDate latestSentDate = officeRepo.getLatestSentDate(waterId);
        page.setLatestSentDate(latestSentDate);

        int activeOffices = officeRepo.countActiveOffices(waterId);
        page.setCanInsert(activeOffices == 0);

        return page;
    }

    private String getOfficeSortColumn(OfficeSortColumn sortColumn) {
        if (OfficeSortColumn.OFFICEDESCRIPTION == sortColumn)
            return "o.description";
        if (OfficeSortColumn.RECEIVEDDATE == sortColumn)
            return "receivedDate";
        return "sentDate";
    }

    private OfficeDto getOfficeDto(WaterRightOffice xref, LocalDateTime earliestCreatedByDate) {
        OfficeDto dto = new OfficeDto();
        if(xref.getOffice() != null) {
            dto.setOfficeDescription(xref.getOffice().getDescription());
            dto.setNotes(xref.getOffice().getNotes());
        }
        dto.setId(xref.getId().longValue());
        dto.setOfficeId(xref.getOfficeId().longValue());
        dto.setReceivedDate(xref.getReceivedDate());
        dto.setSentDate(xref.getSentDate());
        if(earliestCreatedByDate != null) {
            dto.setIsSystemGenerated(!earliestCreatedByDate.isBefore(xref.getCreatedDate()));
        }
        return dto;
    }

    public OfficeDto addWaterRightOffice(Long waterRightId, OfficeCreationDto dto) {
        LOGGER.info("Adding a new office to a Water Right");

        if(dto.getReceivedDate() != null && dto.getReceivedDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter a date after today");
        }

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("This Water Right doesn't exist");
        }
        WaterRight waterRight = foundWater.get();

        if (dto.getReceivedDate() != null) {
            if (dto.getReceivedDate().isBefore(waterRight.getCreatedDate())) {
                throw new ValidationException("The Received Date cannot be before the Created Date on the water right");
            }

            LocalDate earliestCreatedDate = officeRepo.minReceivedDate(waterId);
            if (earliestCreatedDate != null && dto.getReceivedDate().isBefore(earliestCreatedDate)) {
                throw new ValidationException("The Received Date cannot be before the System Generated Office received date");
            }

            LocalDate latestSentDate = officeRepo.getLatestSentDate(waterId);
            if (latestSentDate != null && dto.getReceivedDate().isBefore(latestSentDate)) {
                throw new ValidationException("The Received Date cannot be before the latest Office Sent Date on the water right");
            }
        }

        if(officeRepo.countActiveOffices(waterId) > 0) {
            throw new ValidationException("Add a sent date to all offices before adding one");
        }

        WaterRightOffice model = getOfficeForCreation(dto, waterId);
        model = officeRepo.save(model);
        return getOfficeDto(model, null);
    }

    private WaterRightOffice getOfficeForCreation(OfficeCreationDto office, BigDecimal waterRightId) {
        WaterRightOffice model = new WaterRightOffice();
        model.setReceivedDate(office.getReceivedDate());
        model.setOfficeId(BigDecimal.valueOf(office.getOfficeId()));
        model.setWaterRightId(waterRightId);
        return model;
    }

    public OfficeDto editWaterRightOffice(Long waterRightId, Long officeXrefId, OfficeDto dto) {
        LOGGER.info("Editing an office attached to a Water Right");

        if (dto.getReceivedDate() != null && dto.getReceivedDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Office Received Date cannot be after today");
        }

        if (dto.getSentDate() != null && dto.getSentDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Office Sent Date cannot be after today");
        }

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("This Water Right doesn't exist");
        }
        WaterRight waterRight = foundWater.get();

        BigDecimal xrefId = BigDecimal.valueOf(officeXrefId);
        Optional<WaterRightOffice> foundOffice = officeRepo.findById(xrefId);
        if(!foundOffice.isPresent()) {
            throw new NotFoundException("This office isn't attached to this water right");
        }
        WaterRightOffice office = foundOffice.get();

        if (office.getReceivedDate() != null && !office.getReceivedDate().equals(dto.getReceivedDate())) {
            throw new DataConflictException("Cannot edit Office Received Date once set");
        }

        if (office.getSentDate() != null && !office.getSentDate().equals(dto.getSentDate())) {
            throw new DataConflictException("Cannot edit Office Sent Date once set");
        }

        if (dto.getReceivedDate() == null && dto.getSentDate() != null) {
            throw new ValidationException("Office Sent Date cannot be set before the Office Received Date");
        }

        if (dto.getSentDate() != null && dto.getSentDate().isBefore(waterRight.getCreatedDate())) {
            throw new ValidationException("Office Sent Date cannot be before the Created Date of the water right");
        }

        if (office.getReceivedDate() == null && dto.getReceivedDate() != null) {
            if (dto.getReceivedDate().isBefore(officeRepo.minReceivedDate(waterId))) {
                throw new ValidationException("The Received Date cannot be before the system generated office received date");
            }

            LocalDate latestSentDate = officeRepo.getLatestSentDate(waterId);
            if (latestSentDate != null && dto.getReceivedDate().isBefore(latestSentDate)) {
                throw new ValidationException("The Received Date cannot be before the latest Office Sent Date on the water right");
            }
        }

        LocalDate earliestCreatedDate = officeRepo.minReceivedDate(waterId);
        if(earliestCreatedDate != null &&
            (dto.getReceivedDate() != null && dto.getReceivedDate().isBefore(earliestCreatedDate) || 
            (dto.getSentDate() != null && dto.getReceivedDate().isBefore(earliestCreatedDate)))
        ) {
            throw new ValidationException("The Received Date cannot be before the System Generated Office received date");
        }

        if(
            // removing an end date when there's already one without an end date is not allowed
            officeRepo.countActiveOffices(waterId) > 0 && office.getSentDate() != null && dto.getSentDate() == null
        ) {
            throw new ValidationException("Add a sent date to all offices before removing an office");
        }

        office.setSentDate(dto.getSentDate());
        office.setReceivedDate(dto.getReceivedDate());
        office = officeRepo.save(office);
        return getOfficeDto(office, null);
    }

    public void deleteWaterRightOffice(Long waterRightId, Long officeXrefId) {
        LOGGER.info("Removing an attached office from a Water RIght");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("This Water Right doesn't exist");
        }
        WaterRight waterRight = foundWater.get();

        BigDecimal xrefId = BigDecimal.valueOf(officeXrefId);
        Optional<WaterRightOffice> foundOffice = officeRepo.findById(xrefId);
        if(!foundOffice.isPresent()) {
            throw new NotFoundException("This office isn't attached to this water right");
        }
        WaterRightOffice office = foundOffice.get();

        LocalDateTime earliestCreatedDate = officeRepo.minCreatedDate(waterId);
        if(earliestCreatedDate != null && !earliestCreatedDate.isBefore(office.getCreatedDate())) {
            throw new ValidationException("The System Generated office can not be deleted");
        }

        officeRepo.deleteById(xrefId);

        return;
    }

    public StaffPageDto getWaterRightStaff(Long waterRightId,
        int pageNumber,
        int pageSize,
        StaffSortColumn sortDTOColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the staff for a Water Right");

        String sortColumn = getStaffSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Page<WaterRightStaff> resultsPage = staffRepo.findWaterRightStaff(pageable, waterId);

        LocalDateTime earliestCreatedByDate = staffRepo.minCreatedDate(waterId);

        StaffPageDto page = new StaffPageDto();

        page.setResults(resultsPage.getContent().stream().map(xref -> {
            return getStaffDto(xref, earliestCreatedByDate);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortDTOColumn);
        page.setSortDirection(sortDirection);

        LocalDate latestEndDate = staffRepo.getLatestEndDate(waterId);
        page.setLatestEndDate(latestEndDate);

        int activeStaff = staffRepo.countActiveStaff(waterId);
        page.setCanInsert(activeStaff == 0);

        return page;
    }

    private String getStaffSortColumn(StaffSortColumn sortDTOColumn) {
        if(StaffSortColumn.STAFFDESCRIPTION == sortDTOColumn)
            return "s.description";
        if(StaffSortColumn.BEGINDATE == sortDTOColumn)
            return "beginDate";
        return "endDate";
    }

    private StaffDto getStaffDto(WaterRightStaff xref, LocalDateTime earliestCreatedByDate) {
        StaffDto dto = new StaffDto();
        dto.setId(xref.getId().longValue());
        dto.setBeginDate(xref.getBeginDate());
        dto.setEndDate(xref.getEndDate());
        dto.setStaffId(xref.getStaffId().longValue());
        MasterStaffIndexes staff = xref.getStaff();
        if(staff != null) {
            String firstName = staff.getFirstName() != null ? staff.getFirstName() + " ":"";
            String name = firstName + staff.getLastName();
            dto.setName(name);
        }
        if(earliestCreatedByDate != null) {
            dto.setIsSystemGenerated(!earliestCreatedByDate.isBefore(xref.getCreatedDate()));
        }
        return dto;
    }

    public StaffDto addWaterRightStaff(Long waterRightId, StaffCreationDto dto) {
        LOGGER.info("Adding a new staff");

        if(dto.getBeginDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter a date after today");
        }

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new ValidationException("This water right doesn't exist");
        }
        WaterRight waterRight = foundWater.get();

        if (dto.getBeginDate().isBefore(waterRight.getCreatedDate())) {
            throw new ValidationException("The Begin Date cannot be before the Created Date of the water right");
        }

        LocalDate earliestCreatedDate = officeRepo.minReceivedDate(waterId);
        if(earliestCreatedDate != null && dto.getBeginDate().isBefore(earliestCreatedDate)) {
            throw new ValidationException("The Received Date cannot be before the System Generated Office received date");
        }

        LocalDate latestEndDate = staffRepo.getLatestEndDate(waterId);
        if (latestEndDate != null && dto.getBeginDate().isBefore(latestEndDate)) {
            throw new ValidationException("The Begin Date cannot be before the latest Staff End Date on the water right");
        }

        if(staffRepo.countActiveStaff(waterId) > 0) {
            throw new ValidationException("Add an end date to all staff before adding one");
        }

        WaterRightStaff model = getStaffForCreation(dto, waterId);
        model = staffRepo.save(model);
        return getStaffDto(model, null);
    }

    private WaterRightStaff getStaffForCreation(StaffCreationDto staff, BigDecimal waterRightId) {
        WaterRightStaff model = new WaterRightStaff();
        model.setBeginDate(staff.getBeginDate());
        model.setStaffId(BigDecimal.valueOf(staff.getStaffId()));
        model.setWaterRightId(waterRightId);
        return model;
    }

    public StaffDto editWaterRightStaff(Long waterRightId, Long staffXrefId, StaffDto dto) {
        LOGGER.info("Editing an Water Right's staff member");

        if(dto.getEndDate() != null && dto.getEndDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter an End Date after today");
        }

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new ValidationException("This water right doesn't exist");
        }
        WaterRight waterRight = foundWater.get();

        BigDecimal xrefId = BigDecimal.valueOf(staffXrefId);
        Optional<WaterRightStaff> foundStaff = staffRepo.findById(xrefId);
        if(!foundStaff.isPresent()) {
            throw new ValidationException("This staff is not attached to this water right");
        }
        WaterRightStaff staff = foundStaff.get();

        if (!staff.getBeginDate().equals(dto.getBeginDate())) {
            throw new DataConflictException("Cannot edit Staff Begin Date");
        }

        if (staff.getEndDate() != null && !staff.getEndDate().equals(dto.getEndDate())) {
            throw new DataConflictException("Cannot edit Staff End Date once set");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(waterRight.getCreatedDate())) {
            throw new ValidationException("The End Date cannot be before the Created Date of the water right");
        }

        LocalDate earliestCreatedDate = officeRepo.minReceivedDate(waterId);
        if(earliestCreatedDate != null && dto.getBeginDate().isBefore(earliestCreatedDate)) {
            throw new ValidationException("The Begin Date cannot be before the System Generated Staff Begin Date");
        }

        if(earliestCreatedDate != null && dto.getEndDate().isBefore(earliestCreatedDate)) {
            throw new ValidationException("The End Date cannot be before the System Generated Staff Begin Date");
        }

        if(
            // removing an end date when there's already one without an end date is not allowed
            staffRepo.countActiveStaff(waterId) > 0 && staff.getEndDate() != null && dto.getEndDate() == null
        ) {
            throw new ValidationException("Add a end date to all staff before removing one");
        }

        staff.setEndDate(dto.getEndDate());
        staff.setBeginDate(dto.getBeginDate());
        staff = staffRepo.save(staff);
        return getStaffDto(staff, null);
    }

    public void deleteWaterRightStaff(Long waterRightId, Long staffXrefId) {
        LOGGER.info("Removing an attached staff from a water right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWater = waterRepo.findById(waterId);
        if(!foundWater.isPresent()) {
            throw new ValidationException("This water right doesn't exist");
        }
        WaterRight waterRight = foundWater.get();

        BigDecimal xrefId = BigDecimal.valueOf(staffXrefId);
        Optional<WaterRightStaff> foundStaff = staffRepo.findById(xrefId);
        if(!foundStaff.isPresent()) {
            throw new ValidationException("This staff is not attached to this water right");
        }
        WaterRightStaff staff = foundStaff.get();

        LocalDateTime earliestCreatedDate = staffRepo.minCreatedDate(waterId);
        if(earliestCreatedDate != null && !earliestCreatedDate.isBefore(staff.getCreatedDate())) {
            throw new ValidationException("The System Added Staff Member can not be deleted");
        }

        staffRepo.deleteById(xrefId);
    }
}
