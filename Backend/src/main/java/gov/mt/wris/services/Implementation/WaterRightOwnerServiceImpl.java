package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightOwnerDto;
import gov.mt.wris.dtos.WaterRightOwnerPageDto;
import gov.mt.wris.dtos.WaterRightOwnerSortColumn;
import gov.mt.wris.dtos.WaterRightOwnerUpdateDto;
import gov.mt.wris.dtos.WaterRightRepresentativeDto;
import gov.mt.wris.dtos.WaterRightRepresentativePageDto;
import gov.mt.wris.dtos.WaterRightRepresentativeSortColumn;
import gov.mt.wris.dtos.WaterRightRepresentativeUpdateDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.Representative;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.OwnerRepository;
import gov.mt.wris.repositories.RepresentativesRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.services.WaterRightOwnerService;
import gov.mt.wris.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WaterRightOwnerServiceImpl implements WaterRightOwnerService {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightOwnerService.class);

    @Autowired
    WaterRightRepository waterRepo;

    @Autowired
    MasterStaffIndexesRepository staffRepo;

    @Autowired
    OwnerRepository ownerRepo;

    @Autowired
    RepresentativesRepository repRepo;

    public WaterRightOwnerPageDto getWaterRightOwners(int pagenumber,
        int pagesize,
        WaterRightOwnerSortColumn sortColumn,
        DescSortDirection sortDirection,
        Long waterRightId
    ) {
        LOGGER.info("Getting a page of Water Right Owners");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        Sort ownerSort = getOwnerSort(sortColumn, sortDirection);

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, ownerSort);

        Page<Owner> resultsPage = ownerRepo.findAllByWaterRightId(pageable, id);

        WaterRightOwnerPageDto page = new WaterRightOwnerPageDto();

        page.setResults(resultsPage.getContent().stream().map(owner -> {
            return getWaterRightOwnerDto(owner, (long) owner.getRepresentatives().size());
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private WaterRightOwnerDto getWaterRightOwnerDto(Owner owner, Long repCount) {
        Customer customer = owner.getCustomer();
        WaterRightOwnerDto dto = new WaterRightOwnerDto();

        dto.setBeginDate(owner.getBeginDate());
        dto.setEndDate(owner.getEndDate());
        dto.setContactId(owner.getCustomerId().longValue());
        dto.setOwnerId(owner.getOwnerId().longValue());

        String name = Helpers.buildName(customer.getLastName(), customer.getFirstName(), customer.getMiddleInitial(), customer.getSuffix());
        dto.setName(name);

        dto.setOriginalOwner("Y".equals(owner.getOriginalOwner()));
        dto.setContractForDeedValue(owner.getContractForDeed());
        if(owner.getContractReference() != null) dto.setContractForDeedDescription(owner.getContractReference().getMeaning());

        if (owner.getOriginReference() != null) {
            dto.setOriginDescription(owner.getOriginReference().getMeaning());
            dto.setOriginValue(owner.getOriginReference().getValue());
        }
        dto.setReceivedMail("Y".equals(owner.getReceivedMail()));

        dto.setRepCount(repCount);
        
        return dto;
    }

    private Sort getOwnerSort(WaterRightOwnerSortColumn sortColumn, DescSortDirection sortDirection) {
        Sort.Direction direction = sortDirection == DescSortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;

        if (sortColumn == WaterRightOwnerSortColumn.BEGINDATE) {
            sort = Sort.by(direction, "beginDate");
        } else if (sortColumn == WaterRightOwnerSortColumn.ENDDATE) {
            sort = Sort.by(direction, "endDate");
        } else if (sortColumn == WaterRightOwnerSortColumn.NAME) {
            sort = Sort.by(direction, "c.lastName")
                    .and(Sort.by(direction, "c.firstName"))
                    .and(Sort.by(direction, "c.middleInitial"))
                    .and(Sort.by(direction, "c.suffix"));
        } else if (sortColumn == WaterRightOwnerSortColumn.CONTRACTFORDEED) {
            sort = Sort.by(direction, "contractForDeed");
        } else if (sortColumn == WaterRightOwnerSortColumn.ORIGINALOWNER) {
            sort = Sort.by(direction, "originalOwner");
        } else if (sortColumn == WaterRightOwnerSortColumn.ORIGINDESCRIPTION) {
            sort = Sort.by(direction, "oo.meaning");
        } else if (sortColumn == WaterRightOwnerSortColumn.RECEIVEDMAIL) {
            sort = Sort.by(direction, "receivedMail");
        } else {
            sort = Sort.by(direction, "customerId");
        }
        sort = sort.and(Sort.by(Sort.Direction.DESC, "customerId"));
        return sort;
    }

    public WaterRightOwnerDto updateWaterRightOwner(Long waterRightId, Long ownerId, Long contactId, WaterRightOwnerUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right Owner");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal ownId = BigDecimal.valueOf(ownerId);
        BigDecimal custId = BigDecimal.valueOf(contactId);
        Optional<Owner> foundOwner = ownerRepo.findOwnersByOwnerIdAndCustomerId(ownId, custId);
        if(!foundOwner.isPresent()) {
            throw new NotFoundException("This Owner (Contact Id #" + contactId + ") doesn't exist on this Water Right");
        }
        Owner owner = foundOwner.get();

        if( (owner.getOrigin() == null && updateDto.getOriginValue() != null) ||
            !owner.getOrigin().equals(updateDto.getOriginValue())
        ) {
            Boolean isDecreed = waterRepo.needsDecreePermission(waterId);
            Boolean canModifyDecreed = staffRepo.hasRoles(Arrays.asList(Constants.DECREE_MODIFY_ROLE)) > 0;
            if(isDecreed && !canModifyDecreed) {
                throw new ValidationException("Cannot add an Owner origin to this decreed Water Right");
            }
        }

        owner.setOrigin(updateDto.getOriginValue());

        try {
            owner = ownerRepo.save(owner);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("OWNR_ELEM_ORGN_CK")) {
                    throw new NotFoundException("Pick a correct Owner Origin");
                }
            }
            throw e;
        }

        return getWaterRightOwnerDto(owner, 0L);
    }

    public WaterRightRepresentativePageDto getWaterRightRepresentatives(int pagenumber,
        int pagesize,
        WaterRightRepresentativeSortColumn sortColumn,
        DescSortDirection sortDirection,
        Long waterRightId,
        Long ownerId,
        Long contactId
    ) {
        LOGGER.info("Getting a page of Water Right Representatives");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        BigDecimal ownId = BigDecimal.valueOf(ownerId);
        BigDecimal custId = BigDecimal.valueOf(contactId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();
        Optional<Owner> foundOwner = ownerRepo.findOwnersByOwnerIdAndCustomerId(ownId, custId);
        if(!foundOwner.isPresent()) {
            throw new NotFoundException("This Owner (Contact Id #" + contactId + ") doesn't exist on this Water Right");
        }
        Owner owner = foundOwner.get();

        Sort repSort = getRepresentativeSort(sortColumn, sortDirection);

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, repSort);

        Page<Representative> resultsPage = repRepo.findByOwnerIdAndSecondaryCustomerId(pageable, ownId, custId);

        WaterRightRepresentativePageDto page = new WaterRightRepresentativePageDto();

        page.setResults(resultsPage.getContent().stream().map(rep -> {
            return getWaterRightRepresentativeDto(rep);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private WaterRightRepresentativeDto getWaterRightRepresentativeDto(Representative rep) {
        Customer customer = rep.getCustomer();
        WaterRightRepresentativeDto dto = new WaterRightRepresentativeDto();

        dto.setRepresentativeId(rep.getRepresentativeId().longValue());
        dto.setBeginDate(rep.getBeginDate());
        dto.setEndDate(rep.getEndDate());
        dto.setContactId(rep.getCustomerId().longValue());
        dto.setRoleCode(rep.getRoleTypeCode());
        if(rep.getRoleType() != null) dto.setRoleDescription(rep.getRoleType().getDescription());
        if(customer != null) {
            String name = Helpers.buildName(customer.getLastName(), customer.getFirstName(), customer.getMiddleInitial(), customer.getSuffix());
            dto.setName(name);
        }
        
        return dto;
    }

    private Sort getRepresentativeSort(WaterRightRepresentativeSortColumn sortColumn, DescSortDirection sortDirection) {
        Sort.Direction direction = sortDirection == DescSortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;

        if (sortColumn == WaterRightRepresentativeSortColumn.BEGINDATE) {
            sort = Sort.by(direction, "beginDate");
        } else if (sortColumn == WaterRightRepresentativeSortColumn.ENDDATE) {
            sort = Sort.by(direction, "endDate");
        } else if (sortColumn == WaterRightRepresentativeSortColumn.NAME) {
            sort = Sort.by(direction, "c.lastName")
                    .and(Sort.by(direction, "c.firstName"))
                    .and(Sort.by(direction, "c.middleInitial"))
                    .and(Sort.by(direction, "c.suffix"));
        } else if (sortColumn == WaterRightRepresentativeSortColumn.ROLEDESCRIPTION) {
            sort = Sort.by(direction, "rt.description");
        } else {
            sort = Sort.by(direction, "customerId");
        }
        sort = sort.and(Sort.by(Sort.Direction.DESC, "customerId"));
        return sort;
    }

    public WaterRightRepresentativeDto addWaterRightRepresentative(Long waterRightId, Long ownerId, Long contactId, WaterRightRepresentativeDto createDto) {
        LOGGER.info("Adding a Representative to a Water Right Owner");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        BigDecimal ownId = BigDecimal.valueOf(ownerId);
        BigDecimal custId = BigDecimal.valueOf(contactId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        Representative rep = new Representative();
        rep.setBeginDate(createDto.getBeginDate());
        rep.setEndDate(createDto.getEndDate());
        rep.setRoleTypeCode(createDto.getRoleCode());
        rep.setCustomerId(BigDecimal.valueOf(createDto.getContactId()));
        rep.setSecondaryCustomerId(custId);
        rep.setOwnerId(ownId);

        try {
            rep = repRepo.save(rep);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("REPT_CUST_FK")) {
                    throw new NotFoundException("This Contact (Id#" + createDto.getContactId() + ") does not exist");
                } else if(constraintMessage.contains("REPT_RLTP_FK")) {
                    throw new NotFoundException("Invalid Role Type: " + createDto.getRoleCode());
                } else if(constraintMessage.contains("REPT_OWNR")) {
                    throw new NotFoundException("The Owner with Contact Id " + contactId + " does not exist");
                }
            }
            throw e;
        }

        return getWaterRightRepresentativeDto(rep);
    }

    public WaterRightRepresentativeDto editWaterRightRepresentative(Long waterRightId, Long ownerId, Long contactId, Long representativeId, WaterRightRepresentativeUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right Owner Representative");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        BigDecimal repId = BigDecimal.valueOf(representativeId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();
        Optional<Representative> foundRepresentative = repRepo.findById(repId);
        if(!foundRepresentative.isPresent()) {
            throw new NotFoundException("This Representative does not exist");
        }
        Representative representative = foundRepresentative.get();

        representative.setBeginDate(updateDto.getBeginDate());
        representative.setEndDate(updateDto.getEndDate());
        representative.setRoleTypeCode(updateDto.getRoleCode());

        try {
            representative = repRepo.save(representative);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("REPT_RLTP_FK")) {
                    throw new NotFoundException("Invalid Role Type: " + updateDto.getRoleCode());
                }
            }
            throw e;
        }

        return getWaterRightRepresentativeDto(representative);
    }
}
