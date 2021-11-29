package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.ApplicantDto;
import gov.mt.wris.dtos.ApplicantSortColumn;
import gov.mt.wris.dtos.ApplicantsPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.RepresentativeCount;
import gov.mt.wris.models.IdClasses.OwnerId;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.CustomerRepository;
import gov.mt.wris.repositories.OwnerRepository;
import gov.mt.wris.services.ApplicantService;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicantServiceImpl.class);

    @Autowired
    private OwnerRepository repo;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public ApplicantsPageDto getApplicants(Long applicationId, Integer pageNumber, Integer pageSize,
            ApplicantSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting a Page of Applicants");

        Sort.Direction direction =
            sortDirection == SortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ApplicantDto> results = repo
            .findApplicantsByApplicationId(
                pageable,
                BigDecimal.valueOf(applicationId),
                sortColumn,
                direction
            );

        return new ApplicantsPageDto()
            .results(results.toList())
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalPages(results.getTotalPages())
            .totalElements(results.getTotalElements())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
    }

    @Transactional
    public Optional<ApplicantDto> changeApplicant(Long applicationId, Long ownerId, ApplicantDto applicantDto) {
        LOGGER.info("Updating an Applicant");

        // Grab old version with id
        BigDecimal ownId = BigDecimal.valueOf(ownerId);
        BigDecimal custId = BigDecimal.valueOf(applicantDto.getContactId());
        Optional<Owner> result = repo.findById(new OwnerId(ownId, custId));

        if (!result.isPresent()) {
            throw new NotFoundException("Invalid Contact ID: " + applicantDto.getContactId() + ".");
        }

        Owner oldOne = result.get();
        if (!(oldOne.getApplication().getId().longValue() == applicationId)) {
            throw new NotFoundException(
                    "The Applicant with Contact ID " +  applicantDto.getContactId() + " doesn't belong to Application ID " + applicationId);
        }

        if (!oldOne.getBeginDate().equals(applicantDto.getBeginDate())) {
            throw new DataConflictException("Cannot edit the Begin Date on an applicant");
        }

        if (oldOne.getEndDate() != null && !oldOne.getEndDate().equals(applicantDto.getEndDate())) {
            throw new DataConflictException("Cannot edit the End Date on an applicant once set");
        } else if (oldOne.getEndDate() == null && applicantDto.getEndDate() != null) {
            if (applicantDto.getEndDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Applicant End Date cannot be after today");
            }

            if (applicantDto.getEndDate().isBefore(oldOne.getBeginDate())) {
                throw new ValidationException("Applicant End Date cannot be before Begin Date");
            }

            Optional<Application> application = appRepo.findById(BigDecimal.valueOf(applicationId));
            if (application.isPresent() && applicantDto.getEndDate().isBefore(application.get().getDateTimeReceivedEvent().getEventDate().toLocalDate())) {
                throw new ValidationException("Applicant End Date cannot be before the Date/Time Received on Application");
            }

            if (repo.countByApplicationIdAndEndDateIsNull(BigDecimal.valueOf(applicationId)) < 2) {
                throw new DataConflictException("At least one Applicant needs to be active at all times");
            }

            Optional<LocalDate> optLatestRepDate = repo.getLatestRepresentativeEndDate(custId, ownId);
            if (
                    optLatestRepDate.isPresent() &&
                    applicantDto.getEndDate().isBefore(optLatestRepDate.get())
               ) {
                throw new DataConflictException("The Applicant End Date cannot be before any of the representative end dates");
               }
        }

        Owner newOne = repo.save(updateApplicantEntity(applicantDto, oldOne));
        if(applicantDto.getEndDate() != null){
            repo.endDateRepresentatives(newOne.getCustomerId(), newOne.getOwnerId(), newOne.getEndDate());
        }
        return Optional.ofNullable(getApplicantDto(newOne));
    }

    private Owner updateApplicantEntity(ApplicantDto dto, Owner oldOne) {
        // We only accept changes in endDate
        oldOne.setEndDate(dto.getEndDate());
        return oldOne;
    }

    private Owner getApplicantEntity(ApplicantDto dto, Application app) {
        Owner model = new Owner();
        model.setApplication(app);
        model.setBeginDate(dto.getBeginDate());
        model.setEndDate(dto.getEndDate());
        model.setCustomerId(new BigDecimal(dto.getContactId()));
        model.setOwnerId((dto.getOwnerId() != null) ? new BigDecimal(dto.getOwnerId()) : null);
        return model;
    }

    private static ApplicantDto getApplicantDto(Owner model) {
        ApplicantDto newDto = new ApplicantDto();
        newDto.setBeginDate(model.getBeginDate());
        newDto.setContactId(model.getCustomerId().longValue());
        newDto.setEndDate(model.getEndDate());
        newDto.setFirstName((model.getCustomer() != null) ? model.getCustomer().getFirstName() : null);
        newDto.setLastName((model.getCustomer() != null) ? model.getCustomer().getLastName() : null);
        newDto.setMiddleInitial((model.getCustomer() != null) ? model.getCustomer().getMiddleInitial() : null);
        newDto.setSuffix((model.getCustomer() != null) ? model.getCustomer().getSuffix() : null);
        newDto.setOwnerId(model.getOwnerId().longValue());
        return newDto;
    }

    public void deleteApplicant(Long applicationId, Long ownerId) {
        LOGGER.info("Deleting an Applicant");

        Optional<Application> app = appRepo.findById(new BigDecimal(applicationId));
        if (!app.isPresent()) {
            throw new NotFoundException("The Application with Id " + applicationId + " was not found");
        }
        Application application = app.get();

        if (!application.getTypeCode().equals("600P") && !application.getTypeCode().equals("606P")) {
            throw new ValidationException("Only Applications with Type 600P or 606P can delete Applicants");
        }
        if (application.getApplicants().size() == 1) {
            throw new ValidationException(
                    "There is only one Applicant for this Application, you must add a new one before delete this one.");
        }

        Optional<Owner> result = repo.getOwnerById(new BigDecimal(ownerId));
        if (result.isPresent()) {
            repo.deleteById(result.get().getOwnerId());
        } else {
            // Otherwise, return error
            throw new NotFoundException("The Applicant with ownerId " + ownerId + " was not found");
        }
    }

    public ApplicantDto createApplicant(Long applicationId, ApplicantDto dto) {
        LOGGER.info("Creating an Applicant");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> app = appRepo.findById(appId);
        Owner newOne = null;
        if (!app.isPresent()) {
            throw new NotFoundException("The Application with Id " + applicationId + " was not found");
        }

        BigDecimal customerId = BigDecimal.valueOf(dto.getContactId());
        if (!customerRepo.findById(customerId).isPresent()) {
            throw new NotFoundException("Invalid Contact ID: " + dto.getContactId() + ".");
        }

        if (dto.getEndDate() != null) {
            if (dto.getEndDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Applicant End Date cannot be after today");
            }

            if (dto.getEndDate().isBefore(dto.getBeginDate())) {
                throw new ValidationException("Applicant End Date cannot be before Begin Date");
            }

            if (dto.getEndDate().isBefore(app.get().getDateTimeReceivedEvent().getEventDate().toLocalDate())) {
                throw new ValidationException("Applicant End Date cannot be before the Date/Time Received on Application");
            }
        }

        if(repo.existsByCustomerIdAndApplicationIdAndEndDateIsNull(customerId, appId)) {
            throw new DataConflictException("Cannot create a duplicate applicant");
        }
        newOne = repo.save(getApplicantEntity(dto, app.get()));

        return getApplicantDto(newOne);
    }

    public ApplicantDto toUpperCase(ApplicantDto dto) {
        ApplicantDto newDto = new ApplicantDto();
        newDto.setBeginDate(dto.getBeginDate());
        newDto.setContactId(dto.getContactId());
        newDto.setEndDate(dto.getEndDate());
        newDto.setFirstName((dto.getFirstName() != null) ? dto.getFirstName().toUpperCase() : null);
        newDto.setLastName((dto.getLastName() != null) ? dto.getLastName().toUpperCase() : null);
        newDto.setMiddleInitial((dto.getMiddleInitial() != null) ? dto.getMiddleInitial().toUpperCase() : null);
        newDto.setSuffix((dto.getSuffix() != null) ? dto.getSuffix().toUpperCase() : null);
        newDto.setOwnerId(dto.getOwnerId());
        return newDto;
    }
}
