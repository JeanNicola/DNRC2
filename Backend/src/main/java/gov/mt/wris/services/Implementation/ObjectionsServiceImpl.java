package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.ApplicationCaseSummary;
import gov.mt.wris.models.ElementObjection;
import gov.mt.wris.models.Objection;
import gov.mt.wris.models.Objector;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.ElementObjectionRepository;
import gov.mt.wris.repositories.EventRepository;
import gov.mt.wris.repositories.ObjectionsRepository;
import gov.mt.wris.repositories.ObjectorsRepository;
import gov.mt.wris.services.ObjectionsService;
import gov.mt.wris.utils.Helpers;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Cesar.Zamorano
 *
 */
@Service
public class ObjectionsServiceImpl implements ObjectionsService {
	private static Logger LOGGER = LoggerFactory.getLogger(ObjectionsServiceImpl.class);

	@Autowired
	private ObjectionsRepository objectionsRepository;

	@Autowired
	private ObjectorsRepository objectorsRepository;

	@Autowired
	private ElementObjectionRepository elementObjectionRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Override
	public WaterRightVersionObjectionsPageDto getWaterRightVersionObjections(Integer pageNumber, Integer pageSize, WaterRightVersionObjectionsSortColumn sortColumn,
																			 SortDirection sortDirection, BigDecimal waterRightId, BigDecimal versionId) {

		LOGGER.info("Get water right version objections");

		Sort sortDtoColumn = getWaterRightVersionObjectionsSortColumn(sortColumn, sortDirection);
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
		Page<Objection> results = objectionsRepository.getWaterRightVersionObjections(request, waterRightId, versionId);

		WaterRightVersionObjectionsPageDto page = new WaterRightVersionObjectionsPageDto();
		page.setResults(results.getContent().stream().map(obj -> {
			return getWaterRightVersionObjectionsDto(obj);
		}).collect(Collectors.toList()));

		page.setCurrentPage(results.getNumber() + 1);
		page.setPageSize(results.getSize());
		page.setTotalPages(results.getTotalPages());
		page.setTotalElements(results.getTotalElements());
		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	}

	private Sort getWaterRightVersionObjectionsSortColumn(WaterRightVersionObjectionsSortColumn sortColumn, SortDirection sortDirection) {

		Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		switch (sortColumn) {
			case ID:
				orders.add(new Sort.Order(direction, "id"));
				break;
			case DATERECEIVED:
				orders.add(new Sort.Order(direction, "dateReceived"));
				break;
			case LATE:
				orders.add(new Sort.Order(direction, "l.meaning"));
				break;
			case OBJECTIONTYPE:
				orders.add(new Sort.Order(direction, "t.value"));
				break;
			case OBJECTIONTYPEDESCRIPTION:
				orders.add(new Sort.Order(direction, "t.meaning"));
				break;
			case STATUS:
				orders.add(new Sort.Order(direction, "isOpen"));
				orders.add(new Sort.Order(direction, "isValid"));
				orders.add(new Sort.Order(direction, "isInvalid"));
				orders.add(new Sort.Order(direction, "s.value"));
				break;
			case OBJECTIONSTATUSDESCRIPTION:
				orders.add(new Sort.Order(direction, "s.meaning"));
				break;
		}
		// Secondary - STATUS
		orders.add(new Sort.Order(Sort.Direction.ASC, "isOpen"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "isValid"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "isInvalid"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "s.value"));
		Sort fullSort = Sort.by(orders);
		return fullSort;
	}

	private WaterRightVersionObjectionsDto getWaterRightVersionObjectionsDto(Objection model) {

		WaterRightVersionObjectionsDto dto = new WaterRightVersionObjectionsDto();
		dto.setId(model.getId().longValue());
		dto.setObjectionType(model.getType());
		if (model.getTypeReference()!=null)
		   dto.setObjectionTypeDescription(model.getTypeReference().getMeaning());
		dto.setDateReceived(model.getDateReceived());
		dto.setLate((model.getLateReference()!=null)?model.getLateReference().getMeaning():null);
		dto.setStatus(model.getStatus());
		if (model.getStatusReference()!=null)
			dto.setObjectionStatusDescription(model.getStatusReference().getMeaning());
		return dto;

	}

	@Override
	public ObjectionsPageDto getObjections(Integer pageNumber, Integer pageSize, ObjectionSortColumn sortColumn,
			SortDirection sortDirection, BigDecimal applicationId) {

		LOGGER.info("Getting objections");

		// keep the conversion from dto column to entity column in the service layer
		Sort sortDtoColumn = getEntitySortColumn(sortColumn, sortDirection);
		// pagination by default uses 0 to start, shift it so we can use number
		// displayed to users
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
		Page<Objection> resultsPage = objectionsRepository.getObjections(request, applicationId);

		ObjectionsPageDto page = new ObjectionsPageDto();

		ObjectionsPageDtoResults objectionPage = new ObjectionsPageDtoResults();

		objectionPage.setDetails(resultsPage.getContent().stream().map(sbcd -> {
			return getObjectionsDto(sbcd);
		}).collect(Collectors.toList()));

		Optional<ApplicationCaseSummary> firstCase = objectionsRepository.getFirstCase(applicationId);

		ApplicationCaseSummaryDto caseDto = new ApplicationCaseSummaryDto();
		if(firstCase.isPresent()) {
			caseDto.setCaseId(firstCase.get().getCaseId().longValue());
			caseDto.setStatusDescription(firstCase.get().getCaseStatusDescription());
			caseDto.setTypeDescription(firstCase.get().getCaseTypeDescription());
		}
		objectionPage.setSummary(caseDto);

		page.setResults(objectionPage);

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());

		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());

		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	}

	private ObjectionDto getObjectionsDto(Objection model) {
		ObjectionDto dto = new ObjectionDto();
		dto.setDateReceived(model.getDateReceived());
		dto.setId(model.getId().longValue());
		dto.setLate((model.getLateReference()!=null)?model.getLateReference().getMeaning():null);
		dto.setObjectionType(model.getTypeReference().getMeaning());
		dto.setStatus((model.getStatusReference()!=null)?model.getStatusReference().getMeaning():null);

		return dto;
	}

	private Sort getEntitySortColumn(ObjectionSortColumn sortColumn, SortDirection sortDirection) {
		Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		if(ObjectionSortColumn.ID == sortColumn)
			orders.add(new Sort.Order(direction, "id"));
		if(ObjectionSortColumn.DATERECEIVED == sortColumn)
			orders.add(new Sort.Order(direction, "dateReceived"));
		if(ObjectionSortColumn.LATE == sortColumn)
			orders.add(new Sort.Order(direction, "l.meaning"));
		if(ObjectionSortColumn.OBJECTIONTYPE == sortColumn)
			orders.add(new Sort.Order(direction, "t.meaning"));
		if(ObjectionSortColumn.STATUS == sortColumn) {
			orders.add(new Sort.Order(direction, "isOpen"));
			orders.add(new Sort.Order(direction, "isValid"));
			orders.add(new Sort.Order(direction, "isInvalid"));
			orders.add(new Sort.Order(direction, "s.meaning"));
		}
		orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
		Sort fullSort = Sort.by(orders);
		return fullSort;
	}


	public ObjectionsSearchResultPageDto searchObjections(Integer pageNumber, Integer pageSize, SearchObjectionsSortColumn sortColumn, SortDirection sortDirection,
														  String objectionId, String objectionType, LocalDate filedDate, String objectionLate, String objectionStatus, String basin) {

		LOGGER.info("Search Objections and Counter Objections");

		Sort sortDtoColumn = getSearchObjectionsSortColumn(sortColumn, sortDirection);
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
		Page<Objection> results = objectionsRepository.searchObjections(request, objectionId, objectionType, filedDate, objectionLate, objectionStatus, basin);

		ObjectionsSearchResultPageDto page = new ObjectionsSearchResultPageDto();
		page.setResults(results.getContent().stream().map(obj -> {
			return getObjectionsSearchResultDto(obj);
		}).collect(Collectors.toList()));

		page.setCurrentPage(results.getNumber() + 1);
		page.setPageSize(results.getSize());
		page.setTotalPages(results.getTotalPages());
		page.setTotalElements(results.getTotalElements());
		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);
		return page;

	}

	private Sort getSearchObjectionsSortColumn(SearchObjectionsSortColumn sortColumn, SortDirection sortDirection) {

		Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		switch (sortColumn) {
			case OBJECTIONID:
				orders.add(new Sort.Order(direction, "id"));
				break;
			case OBJECTIONTYPEDESCRIPTION:
				orders.add(new Sort.Order(direction, "tr.meaning"));
				break;
			case FILEDDATE:
				orders.add(new Sort.Order(direction, "dateReceived"));
				break;
			case OBJECTIONSTATUSDESCRIPTION:
				orders.add(new Sort.Order(direction, "sr.meaning"));
				break;
			case COMPLETEBASIN:
				orders.add(new Sort.Order(direction, "d.basin"));
				orders.add(new Sort.Order(direction, "dt.description"));
				orders.add(new Sort.Order(direction, "d.issuedDate"));
				break;
			case COMPLETEWATERRIGHTNUMBER:
				orders.add(new Sort.Order(direction, "wr.basin"));
				orders.add(new Sort.Order(direction, "wr.waterRightNumber"));
				orders.add(new Sort.Order(direction, "wr.ext"));
				break;
		}
		// Secondary - OBJECTIONID
		orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
		Sort fullSort = Sort.by(orders);
		return fullSort;
	}

	private ObjectionsSearchResultDto getObjectionsSearchResultDto(Objection model) {

		ObjectionsSearchResultDto dto = new ObjectionsSearchResultDto();
		dto.setObjectionId(model.getId().longValue());
		dto.setObjectionType(model.getType());
		if (model.getTypeReference()!=null)
			dto.setObjectionTypeDescription(model.getTypeReference().getMeaning());
		dto.setFiledDate(model.getDateReceived());
		dto.setObjectionStatus(model.getStatus());
		if (model.getStatusReference()!=null)
			dto.setObjectionStatusDescription(model.getStatusReference().getMeaning());
		if (model.getDecree()!=null) {
			dto.setBasin(model.getDecree().getBasin());
			dto.setCompleteBasin(
			    String.format(
			        "%s %s %s",
			        model.getDecree().getBasin(),
			        model.getDecree().getDecreeType().getDescription(),
			        model.getDecree().getIssuedDate())
			);
		}
		if (model.getWaterRightVersion()!=null)
		    dto.setCompleteWaterRightNumber(
				Helpers.buildCompleteWaterRightNumber(
					model.getWaterRightVersion().getWaterRight().getBasin(),
					model.getWaterRightVersion().getWaterRight().getWaterRightNumber().toString(),
					model.getWaterRightVersion().getWaterRight().getExt())
		    );
		return dto;

	}

	@Transactional
	public ObjectionsSearchResultDto createObjection(ObjectionCreationDto createDto) {

			try {
				return createObjectionTransaction(createDto);
			} catch (DataIntegrityViolationException e){
				if(e.getCause() instanceof ConstraintViolationException &&
						e.getCause().getCause() instanceof BatchUpdateException) {
					ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
					BatchUpdateException sc = (BatchUpdateException) cn.getCause();
					String constraintMessage = sc.getMessage();
					if (constraintMessage.contains("WRD.OITA_APPL_FK")) {
						throw new DataIntegrityViolationException("The application id " + createDto.getApplicationId() + " does not exist");
					} else if (constraintMessage.contains("WRD.OITA_DECR_FK")) {
						throw new DataIntegrityViolationException("The decree id " + createDto.getDecreeId() + " does not exist");
					} else if (constraintMessage.contains("WRD.OITA_OBJN_TYPE")) {
						throw new DataIntegrityViolationException("The objection type " + createDto.getObjectionType() + " does not exist");
					} else if (constraintMessage.contains("WRD.OBJT_CUST_FK")) {
						throw new DataIntegrityViolationException(
						    String.format("The objector contact id(s) %s does not exist",
							createDto.getObjectors().stream().map(c->String.valueOf(c.getContactId())).collect(Collectors.joining(", ")))
						);
					} else if (constraintMessage.contains("WRD.EOBJ_ETYP_FK")) {
						throw new DataIntegrityViolationException(
								String.format("The objection element type(s) %s does not exist",
										createDto.getElements().stream().map(oe->String.valueOf(oe.getElementType())).collect(Collectors.joining(", ")))
						);
					} else {
						throw e;
					}
				} else {
					throw e;
				}
			}

		}

		@Transactional
		private ObjectionsSearchResultDto createObjectionTransaction(ObjectionCreationDto createDto) {

			LOGGER.info("Create Objection or Counter Objection");

			if (!createDto.getObjectionType().equals(Constants.OBJECTIONS_TYPE_ON_MOTION) && (createDto.getObjectors()==null || createDto.getObjectors().size()<1))
				throw new DataIntegrityViolationException("You cannot save this record until an objector has been assigned to this objection.");
			if (createDto.getFiledDate()==null)
				throw new DataIntegrityViolationException("You cannot save this record until a date filed has been entered.");

			Objection model = new Objection();
			ObjectionsSearchResultDto resultDto;
			model.setType(createDto.getObjectionType()!=null?createDto.getObjectionType():Constants.OBJECTIONS_TYPE_OBJECTION_TO_RIGHT);
			model.setObjectionLate(createDto.getObjectionLate()!=null?createDto.getObjectionLate():Constants.OBJECTIONS_LATE_DEFAULT);
			model.setStatus(createDto.getObjectionStatus()!=null?createDto.getObjectionStatus():Constants.OBJECTIONS_STATUS_DEFAULT);
			model.setDateReceived(createDto.getFiledDate());

			switch(createDto.getObjectionType()) {

				case Constants.OBJECTIONS_TYPE_COUNTER_OBJECTION:
				case Constants.OBJECTIONS_TYPE_INTENT_TO_APPEAR:
				case Constants.OBJECTIONS_TYPE_OBJECTION_TO_RIGHT: {

					if (createDto.getWaterRightId() != null) {
						model.setWaterRightId(new BigDecimal(createDto.getWaterRightId()));
						model.setVersionId(new BigDecimal(createDto.getVersionId()));
					}
					if (createDto.getDecreeId() != null)
					    model.setDecreeId(new BigDecimal(createDto.getDecreeId()));
					resultDto = getObjectionsSearchResultDto(objectionsRepository.saveAndFlush(model));

					List<Objector> objectors = new ArrayList<>();
					for (ObjectorDto dto : createDto.getObjectors()) {
						Objector obj = new Objector();
						obj.setObjectionId(new BigDecimal(resultDto.getObjectionId()));
						obj.setCustomerId(dto.getContactId() != null ? new BigDecimal(dto.getContactId()) : null);
						obj.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : null);
						obj.setRepresentativeCount(dto.getRepresentativeCount() != null ? dto.getRepresentativeCount() : null);
						objectors.add(obj);
					}
					objectorsRepository.saveAllAndFlush(objectors);
					break;

				}
				case Constants.OBJECTIONS_TYPE_ON_MOTION: {

					if (createDto.getWaterRightId() != null) {
						model.setWaterRightId(new BigDecimal(createDto.getWaterRightId()));
						model.setVersionId(new BigDecimal(createDto.getVersionId()));
					}
					if (createDto.getDecreeId() != null)
						model.setDecreeId(new BigDecimal(createDto.getDecreeId()));
					resultDto = getObjectionsSearchResultDto(objectionsRepository.saveAndFlush(model));

					if (createDto.getElements().size() == 0)
						throw new DataIntegrityViolationException("You cannot save this record until an element has been entered.");
					List<ElementObjection> elements = new ArrayList<>();
					for (ElementDto dto : createDto.getElements()) {
						ElementObjection eo = new ElementObjection();
						eo.setObjectionId(new BigDecimal(resultDto.getObjectionId()));
						eo.setType(dto.getElementType());
						eo.setComment(dto.getElementObjectionRemark() != null ? dto.getElementObjectionRemark() : null);
						elements.add(eo);
					}
					elementObjectionRepository.saveAllAndFlush(elements);
					break;

				}
				case Constants.OBJECTIONS_TYPE_OBJECTION_TO_DECREE: {

	                model.setDecreeId(new BigDecimal(createDto.getDecreeId()));
					resultDto = getObjectionsSearchResultDto(objectionsRepository.saveAndFlush(model));

					if (createDto.getElements().size() == 0)
						throw new DataIntegrityViolationException("You cannot save this record until an element has been entered.");
					List<ElementObjection> elements = new ArrayList<>();
					for (ElementDto dto : createDto.getElements()) {
						ElementObjection eo = new ElementObjection();
						eo.setObjectionId(new BigDecimal(resultDto.getObjectionId()));
						eo.setType(dto.getElementType());
						eo.setComment(dto.getElementObjectionRemark() != null ? dto.getElementObjectionRemark() : null);
						elements.add(eo);
					}
					elementObjectionRepository.saveAllAndFlush(elements);

					List<Objector> objectors = new ArrayList<>();
					for (ObjectorDto dto : createDto.getObjectors()) {
						Objector obj = new Objector();
						obj.setObjectionId(new BigDecimal(resultDto.getObjectionId()));
						obj.setCustomerId(dto.getContactId() != null ? new BigDecimal(dto.getContactId()) : null);
						obj.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : null);
						obj.setRepresentativeCount(dto.getRepresentativeCount() != null ? dto.getRepresentativeCount() : null);
						objectors.add(obj);
					}
					objectorsRepository.saveAllAndFlush(objectors);
					break;

				}
				case Constants.OBJECTIONS_TYPE_OBJECTION_TO_APPLICATION: {

					if (createDto.getApplicationId()==null)
						throw new DataIntegrityViolationException("You cannot save this record until an application has been entered.");
					model.setApplicationId(new BigDecimal(createDto.getApplicationId()));
					resultDto = getObjectionsSearchResultDto(objectionsRepository.saveAndFlush(model));

					List<Objector> objectors = new ArrayList<>();
					for (ObjectorDto dto : createDto.getObjectors()) {
						Objector obj = new Objector();
						obj.setObjectionId(new BigDecimal(resultDto.getObjectionId()));
						obj.setCustomerId(dto.getContactId() != null ? new BigDecimal(dto.getContactId()) : null);
						obj.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : null);
						obj.setRepresentativeCount(dto.getRepresentativeCount() != null ? dto.getRepresentativeCount() : null);
						objectors.add(obj);
					}
					objectorsRepository.saveAllAndFlush(objectors);
					eventRepository.createApplicationObjectionEvent(new BigDecimal(createDto.getApplicationId()), createDto.getFiledDate());
					break;

				}
				default:
					throw new DataIntegrityViolationException(
					    String.format("%s objection type is no longer supported creating an objection",
					    createDto.getObjectionType())
					);

			}

	        return resultDto;

		}

		public int closeCaseObjections(BigDecimal caseId) {

			try {
				return closeCaseObjectionsTransaction(caseId);
			} catch (DataIntegrityViolationException e){
				if(e.getCause() instanceof ConstraintViolationException &&
						e.getCause().getCause() instanceof BatchUpdateException) {
					ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
					BatchUpdateException sc = (BatchUpdateException) cn.getCause();
					String constraintMessage = sc.getMessage();
					if (constraintMessage.contains("WRD.CVXR_CASE_FK")) {
						throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
					} else {
						throw e;
					}
				} else {
					throw e;
				}
			}

		}

		@Transactional
		private int closeCaseObjectionsTransaction(BigDecimal caseId) {

			LOGGER.info("Close outstanding Court Case Objections");
			return objectionsRepository.closeCaseObjections(caseId);

		}

	public EligibleApplicationsSearchPageDto getEligibleApplications(Integer pagenumber, Integer pagesize, EligibleApplicationsSortColumn sortColumn, SortDirection sortDirection, String applicationId) {

		LOGGER.info("Get eligible Applications for Objections or Counter Objections");

		Sort sortDtoColumn = getEligibleApplicationsSortColumn(sortColumn, sortDirection);
		Pageable pageable = PageRequest.of(pagenumber -1, pagesize, sortDtoColumn);

		Page<Application> resultsPage = applicationRepository.getObjectionsAllowedApplications(pageable, applicationId);
		EligibleApplicationsSearchPageDto page = new EligibleApplicationsSearchPageDto();
		page.setResults(resultsPage.getContent().stream().map(row -> {
			EligibleApplicationsDto dto = new EligibleApplicationsDto();
			dto.setApplicationId(row.getId().longValue());
			if (row.getType()!=null) {
				dto.setApplicationTypeDescription(row.getType().getDescription());
				dto.setObjectionsAllowed(row.getType().getObjectionsAllowed());
				if (row.getType().getObjectionsAllowedReference()!=null)
					dto.setObjectionAllowedDescription(row.getType().getObjectionsAllowedReference().getMeaning());
			}
			return dto;
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());
		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());
		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);
		Map<String, String> filters = new HashMap<String, String>();
		if (applicationId != null) filters.put("applicationId", applicationId);
		page.setFilters(filters);

		return page;

	}

	private Sort getEligibleApplicationsSortColumn(EligibleApplicationsSortColumn sortColumn, SortDirection sortDirection) {

		Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		switch (sortColumn) {
			case APPLICATIONID:
				orders.add(new Sort.Order(direction, "id"));
				break;
			case APPLICATIONTYPEDESCRIPTION:
				orders.add(new Sort.Order(direction, "oar.meaning"));
				break;
		}
		orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
		Sort fullSort = Sort.by(orders);
		return fullSort;

	}

	public ObjectionDto updateObjection(BigDecimal objectionId, ObjectionUpdateDto objectionUpdateDto) {

		LOGGER.info("Updating Objection");

		Optional<Objection> fndObj = objectionsRepository.findById(objectionId);
		if (!fndObj.isPresent())
			throw new NotFoundException(String.format("Objection %s not found",objectionId));
		Objection objection = fndObj.get();

		objection.setStatus(objectionUpdateDto.getStatus());

		return getObjectionsDto(objectionsRepository.saveAndFlush(objection));
	}

}
