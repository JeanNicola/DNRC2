package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import gov.mt.wris.dtos.RepresentativeDto;
import gov.mt.wris.dtos.RepresentativeSortColumn;
import gov.mt.wris.dtos.RepresentativesPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.Objector;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.Representative;
import gov.mt.wris.repositories.CustomerRepository;
import gov.mt.wris.repositories.ObjectorsRepository;
import gov.mt.wris.repositories.OwnerRepository;
import gov.mt.wris.repositories.RepresentativesRepository;
import gov.mt.wris.services.RepresentativeService;

/**
 * @author Cesar.Zamorano
 *
 */
@Service
public class RepresentativeServiceImpl implements RepresentativeService {

	private static Logger LOGGER = LoggerFactory.getLogger(RepresentativeServiceImpl.class);

	@Autowired
	private RepresentativesRepository repository;

	@Autowired
	private OwnerRepository ownerRepo;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private ObjectorsRepository objectorRepo;

	/**
	 * @param applicationId
	 * @param ownerId
	 * @param pageNumber
	 * @param pageSize
	 * @param sortColumn
	 * @param sortDirection
	 * @return
	 */
	public RepresentativesPageDto getRepresentatives(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId, Integer pageNumber,
			Integer pageSize, RepresentativeSortColumn sortColumn, SortDirection sortDirection) {
		LOGGER.info("Getting a Page of Representatives");

        Optional<Owner> optOwner = ownerRepo.findOwnersByOwnerIdAndCustomerId(ownerId, customerId);
		if (!optOwner.isPresent()) {
			throw new NotFoundException("The Owner with id " + ownerId + " doesn't exist.");
		}
		Owner owner = optOwner.get();

		// Grab owner against application
		if (!(applicationId.equals(owner.getApplication().getId()))) {
			throw new NotFoundException(
					"The Applicant with ownerId " + ownerId + " doesn't belong to applicationId " + applicationId);
		}

		Sort requestedSort = getEntitySortColumn(sortColumn, sortDirection);
		Sort baseSort = Sort.by(Sort.Direction.ASC, "customerId");

		// pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, requestedSort.and(baseSort));
		Page<Representative> resultsPage = repository.getRepresentatives(request, ownerId);

		RepresentativesPageDto page = new RepresentativesPageDto();

		page.setResults(resultsPage.getContent().stream().map(sbcd -> {
			return getRepresentativeDto(sbcd);
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());

		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());

		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	};

    /**
     *
     * @param applicationId
     * @param ownerId
     * @param customerId
     * @param representativeId
     * @param representativeDto
     * @return
     */
    public Optional<RepresentativeDto> changeRepresentative(BigDecimal applicationId,
                                                            BigDecimal ownerId,
                                                            BigDecimal customerId,
                                                            BigDecimal representativeId,
                                                            RepresentativeDto representativeDto) {
		LOGGER.info("Updating a Representative");

		// Checking Representative
		Optional<Representative> optModel = repository.findById(representativeId);
		if (!optModel.isPresent()) {
			throw new NotFoundException("The Representative with id " + representativeId + " doesn't exist.");
		}

		Representative oldRep = optModel.get();

		Optional<Owner> optOwner = ownerRepo.getOwnerById(ownerId);
		if (!optOwner.isPresent()) {
			throw new NotFoundException("The Owner with id " + ownerId + " doesn't exist.");
		}
		Owner owner = optOwner.get();

		if (!owner.getOwnerId().equals(ownerId)) {
			throw new NotFoundException("The Representative with id " + representativeId
					+ " doesn't belong to Applicant with ownerId" + ownerId);
		}

		// Grab owner against application
		if (!(owner.getApplication().getId().equals(applicationId))) {
			throw new NotFoundException(
					"The Applicant with ownerId " + ownerId + " doesn't belong to applicationId " + applicationId);
		}

		if (!oldRep.getBeginDate().equals(representativeDto.getBeginDate())) {
			throw new ValidationException("Cannot edit the Begin Date on a representative");
		}

		if (oldRep.getEndDate() != null && !oldRep.getEndDate().equals(representativeDto.getEndDate())) {
			throw new ValidationException("Cannot edit the End Date on a representative once set");
		}

		// Convert input dto to model to nullable fields is properly handled.
		Representative repModel = replaceRepresentative(oldRep, representativeDto, ownerId, customerId);

		// Representative validations
		if (repModel.getBeginDate().isAfter(LocalDate.now())
				|| repModel.getBeginDate().isBefore(owner.getBeginDate())) {
			throw new ValidationException("Begin Date must be before today, but not before Applicant's Begin Date.");
		}

		if (owner.getEndDate() != null && repModel.getEndDate() == null) {
			throw new ValidationException("An End Date is required when the Applicant has an End Date");
		}
		if (owner.getEndDate() != null && owner.getEndDate().isBefore(repModel.getEndDate())) {
			throw new ValidationException("End Date must be on or before the Applicant's End Date");
		}

		if (repModel.getEndDate() != null && repModel.getEndDate().isBefore(owner.getBeginDate())) {
			throw new ValidationException("End Date must be after Applicant Begin Date");
		}

		if (repModel.getEndDate() != null && repModel.getEndDate().isAfter(LocalDate.now())) {
			throw new ValidationException("End Date must be before today.");
		}
		if (repModel.getRoleTypeCode() == null) {
			throw new ValidationException("Role Type Code cannot be empty.");
		}

		if (!customerRepo.findById(new BigDecimal(representativeDto.getContactId())).isPresent()) {
			throw new ValidationException("Invalid Contact ID: " + representativeDto.getContactId() + ".");
		}

		Representative newOne = repository.save(repModel);
		return Optional.ofNullable(getRepresentativeDto(newOne));
	};

	private Representative replaceRepresentative(Representative oldModel, RepresentativeDto dto, BigDecimal ownerId, BigDecimal customerId) {
		if (dto.getBeginDate() != null)
			oldModel.setBeginDate(dto.getBeginDate());
		if (dto.getContactId() != null)
			oldModel.setCustomerId(new BigDecimal(dto.getContactId()));
		oldModel.setEndDate(dto.getEndDate());
		if (dto.getRepresentativeId() != null)
			oldModel.setRepresentativeId(new BigDecimal(dto.getRepresentativeId()));
		if (dto.getRoleTypeCode() != null)
			oldModel.setRoleTypeCode(dto.getRoleTypeCode());
		oldModel.setOwnerId(ownerId);
        oldModel.setSecondaryCustomerId(customerId);
		return oldModel;
	}

	/**
	 *
	 * @param applicationId
	 * @param ownerId
	 * @param representativeId
	 */
	public void deleteRepresentative(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId, BigDecimal representativeId) {
		LOGGER.info("Deleting a Representative");

		// Checking Representative
		Optional<Representative> optModel = repository.findById(representativeId);
		if (!optModel.isPresent()) {
			throw new NotFoundException("The Representative with id " + representativeId + " doesn't exist.");
		}

		Optional<Owner> optOwner = ownerRepo.getOwnerById(ownerId);
		if (!optOwner.isPresent()) {
			throw new NotFoundException("The Owner with id " + ownerId + " doesn't exist.");
		}
		Owner owner = optOwner.get();

		if (!owner.getOwnerId().equals(ownerId)) {
			throw new NotFoundException("The Representative with id " + representativeId
					+ " doesn't belong to Applicant with ownerId" + ownerId);
		}

		// Grab owner against application
		if (!(owner.getApplication().getId().equals(applicationId))) {
			throw new NotFoundException(
					"The Applicant with ownerId " + ownerId + " doesn't belong to applicationId " + applicationId);
		}
		Application application = owner.getApplication();

		if (!application.getTypeCode().equals("600P") && !application.getTypeCode().equals("606P")) {
			throw new ValidationException("Only Applications with Type 600P or 606P can delete Representatives.");
		}

		repository.deleteById(representativeId);
	};

	/**
	 *
	 * @param applicationId
	 * @param ownerId
	 * @param representativeDto
	 * @return
	 */
	public RepresentativeDto createRepresentative(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId,
			RepresentativeDto representativeDto) {
		LOGGER.info("Creating an Representative");

		Optional<Owner> result = ownerRepo.findOwnersByOwnerIdAndCustomerId(ownerId, customerId);
		Representative newOne = null;
		if (!result.isPresent()) {
			throw new NotFoundException("The Application with Id " + applicationId + " was not found");
		}
		Owner owner = result.get();
		if (!customerRepo.findById(new BigDecimal(representativeDto.getContactId())).isPresent()) {
			throw new NotFoundException("Invalid Contact ID: " + representativeDto.getContactId() + ".");
		}
		if (representativeDto.getRepresentativeId() != null) {
			throw new ValidationException("The new Representative must not have an id.");
		}
		if (representativeDto.getBeginDate().isBefore(owner.getBeginDate())) {
			throw new ValidationException("Begin Date cannot be before the Applicant's Begin Date");
		}
		if (representativeDto.getBeginDate().isAfter(LocalDate.now())) {
			throw new ValidationException("Begin Date must be before today");
		}
		if (owner.getEndDate() != null && representativeDto.getBeginDate().isAfter(owner.getEndDate())) {
			throw new ValidationException("Begin Date cannot be after the Applicant's End Date");
		}

		if (representativeDto.getEndDate() != null) {
			if (representativeDto.getEndDate().isAfter(LocalDate.now())) {
				throw new ValidationException("End Date must be before today");
			}
			if (representativeDto.getEndDate().isBefore(owner.getBeginDate())) {
				throw new ValidationException("End Date cannot be before Applicant's Begin Date");
			}

			if (owner.getEndDate() != null && representativeDto.getEndDate().isAfter(owner.getEndDate())) {
				throw new ValidationException("End Date cannot be after Applicant's End Date");
			}
		}

		if(repository.existsByCustomerIdAndOwnerIdAndEndDateIsNull(BigDecimal.valueOf(representativeDto.getContactId()), ownerId)) {
			throw new DataConflictException("Cannot create a duplicate representative");
		}
		Representative newRep = getRepresentative(representativeDto, ownerId, customerId);
		if(owner.getEndDate() != null && representativeDto.getEndDate() == null) {
			newRep.setEndDate(owner.getEndDate());
		}
		newOne = repository.save(newRep);

		return getRepresentativeDto(newOne);
	}

	/**
	 * @param dto
	 * @return
	 */
	public RepresentativeDto toUpperCase(RepresentativeDto dto) {
		RepresentativeDto newDto = new RepresentativeDto();
		newDto.setBeginDate(dto.getBeginDate());
		newDto.setContactId((dto.getContactId() != null) ? dto.getContactId().longValue() : null);
		newDto.setEndDate(dto.getEndDate());
		newDto.setFirstName((dto.getFirstName() != null) ? dto.getFirstName().toUpperCase() : null);
		newDto.setLastName((dto.getLastName() != null) ? dto.getLastName().toUpperCase() : null);
		newDto.setMiddleInitial((dto.getMiddleInitial() != null) ? dto.getMiddleInitial().toUpperCase() : null);
		newDto.setRepresentativeId((dto.getRepresentativeId() != null) ? dto.getRepresentativeId().longValue() : null);
		newDto.setSuffix(dto.getSuffix());
		newDto.setRoleTypeCode(dto.getRoleTypeCode());
		newDto.setRoleTypeDescription(dto.getRoleTypeDescription());
		return newDto;
	}

	private RepresentativeDto getRepresentativeDto(Representative model) {
		RepresentativeDto newDto = new RepresentativeDto();
		newDto.setBeginDate(model.getBeginDate());
		newDto.setEndDate(model.getEndDate());
		if (model.getCustomer() != null) {
			newDto.setContactId(model.getCustomer().getCustomerId().longValue());
			newDto.setFirstName(model.getCustomer().getFirstName());
			newDto.setLastName(model.getCustomer().getLastName());
			newDto.setMiddleInitial(model.getCustomer().getMiddleInitial());
			newDto.setSuffix(model.getCustomer().getSuffix());
      newDto.setApplicantContactId(model.getSecondaryCustomerId() != null ? model.getSecondaryCustomerId().longValue() : null);
		} else {
			newDto.setContactId(model.getCustomerId().longValue());
		}
		newDto.setRepresentativeId(model.getRepresentativeId().longValue());
		if(model.getRoleType() != null) {
			newDto.setRoleTypeCode(model.getRoleType().getCode());
			newDto.setRoleTypeDescription(model.getRoleType().getDescription());
		}else {
			newDto.setRoleTypeCode(model.getRoleTypeCode());
		}
		return newDto;
	}

	private Representative getRepresentative(RepresentativeDto dto, BigDecimal ownerId, BigDecimal customerId) {
		Representative newModel = new Representative();
		newModel.setBeginDate(dto.getBeginDate());
		newModel.setCustomerId(new BigDecimal(dto.getContactId()));
		newModel.setSecondaryCustomerId(customerId);
		newModel.setEndDate(dto.getEndDate());
		newModel.setRepresentativeId(
				(dto.getRepresentativeId() != null) ? new BigDecimal(dto.getRepresentativeId()) : null);
		newModel.setRoleTypeCode(dto.getRoleTypeCode());
		newModel.setOwnerId(ownerId);
		return newModel;
	}

	private Sort getEntitySortColumn(RepresentativeSortColumn sortColumn, SortDirection sortDirection) {
		Sort.Direction sortDtoDirection = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;		
		Sort requestedSort;

		switch (sortColumn) {
			case BEGINDATE:
				requestedSort = Sort.by(sortDtoDirection, "beginDate");
				break;
			case ENDDATE:
				requestedSort = Sort.by(sortDtoDirection, "endDate");
				break;
			case ROLETYPEDESCRIPTION:
				requestedSort = Sort.by(sortDtoDirection, "t.description");
				break;
			case FULLNAME:
				requestedSort = Sort.by(sortDtoDirection, "c.lastName");
				requestedSort = requestedSort.and(Sort.by(sortDtoDirection, "c.firstName"));
				requestedSort = requestedSort.and(Sort.by(sortDtoDirection, "c.middleInitial"));
				requestedSort = requestedSort.and(Sort.by(sortDtoDirection, "c.suffix"));
				break;
			default:
				requestedSort = Sort.by(sortDtoDirection, "customerId");
		}
		
		return requestedSort;
	}
	
	// objectors representatives
	public RepresentativesPageDto getObjectorRepresentatives(Long applicationId, Long objectionId, Long customerId, Integer pageNumber,
			Integer pageSize, RepresentativeSortColumn sortColumn, SortDirection sortDirection) {
		LOGGER.info("Getting a Page of Representatives for an objector");

		BigDecimal objId = BigDecimal.valueOf(objectionId);
		BigDecimal custId = BigDecimal.valueOf(customerId);
        Optional<Objector> optObjector = objectorRepo.findObjectorsByObjectionIdAndCustomerId(objId, custId);
		if (!optObjector.isPresent()) {
			throw new NotFoundException("This Objector doesn't exist.");
		}

		Sort requestedSort = getEntitySortColumn(sortColumn, sortDirection);
		Sort baseSort = Sort.by(Sort.Direction.ASC, "customerId");

		// pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, requestedSort.and(baseSort));
		Page<Representative> resultsPage = repository.findAllByObjectionIdAndThirdCustomerId(request, objId, custId);

		RepresentativesPageDto page = new RepresentativesPageDto();

		page.setResults(resultsPage.getContent().stream().map(sbcd -> {
			return getRepresentativeDto(sbcd);
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());

		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());

		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	};
}
