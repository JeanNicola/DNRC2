package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.DescSortDirection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import gov.mt.wris.dtos.ObjectionCreationDto;
import gov.mt.wris.dtos.ObjectionsSearchResultDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionObjectorsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectorsSortColumn;
import gov.mt.wris.services.WaterRightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import gov.mt.wris.dtos.ObjectorDto;
import gov.mt.wris.dtos.ObjectorSortColumn;
import gov.mt.wris.dtos.ObjectorsPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionObjectorsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectorsSortColumn;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.Objection;
import gov.mt.wris.models.Objector;
import gov.mt.wris.repositories.ObjectionsRepository;
import gov.mt.wris.repositories.ObjectorsRepository;
import gov.mt.wris.services.ObjectorsService;
import gov.mt.wris.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Cesar.Zamorano
 *
 */
@Service
public class ObjectorsServiceImpl implements ObjectorsService {

	private static Logger LOGGER = LoggerFactory.getLogger(ObjectorsServiceImpl.class);

	@Autowired
	private ObjectorsRepository repository;

	@Autowired
	private ObjectionsRepository objectionRepo;

	public WaterRightVersionObjectorsPageDto getWaterRightVersionObjectors(BigDecimal waterRightId, BigDecimal versionId, BigDecimal objectionId, Integer pageNumber,
																		   Integer pageSize, WaterRightVersionObjectorsSortColumn sortColumn, SortDirection sortDirection) {

		LOGGER.info("Get page of Water Right Version Objectors");
		Optional<Objection> obj = objectionRepo.findById(objectionId);
		if (!obj.isPresent()) {
			throw new NotFoundException("Objection with id " + objectionId + " not found.");
		}
		if (!(obj.get().getWaterRightId().equals(waterRightId) && obj.get().getVersionId().equals(versionId))) {
			throw new DataConflictException(
			String.format("Water right %s, version %s does not have objection id %s",
					waterRightId, versionId, objectionId)
			);
		}

		Sort sort = getWaterRightVersionObjectorsSortColumn(sortColumn, sortDirection);
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, sort);
		Page<Objector> results = repository.getObjectors(request, objectionId);
		WaterRightVersionObjectorsPageDto page = new WaterRightVersionObjectorsPageDto();

		page.setResults(results.getContent().stream().map(sbcd -> {
			return getObjectorDto(sbcd);
		}).collect(Collectors.toList()));

		page.setCurrentPage(results.getNumber() + 1);
		page.setPageSize(results.getSize());
		page.setTotalPages(results.getTotalPages());
		page.setTotalElements(results.getTotalElements());
		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	}

	private Sort getWaterRightVersionObjectorsSortColumn(WaterRightVersionObjectorsSortColumn sortColumn, SortDirection sortDirection) {

		Sort.Direction sort = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		switch (sortColumn) {
			case CONTACTID:
				orders.add(new Sort.Order(sort, "customerId"));
				break;
			case NAME:
				orders.add(new Sort.Order(sort, "c.lastName"));
				orders.add(new Sort.Order(sort, "c.firstName"));
				orders.add(new Sort.Order(sort, "c.middleInitial"));
				orders.add(new Sort.Order(sort, "c.suffix"));
				break;
		}
		// Secondary WaterRightVersionObjectorsSortColumn.CONTACTNAME
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.lastName"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.firstName"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.middleInitial"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.suffix"));
		Sort fullSort = Sort.by(orders);
		return fullSort;

	}

	public ObjectorsPageDto getObjectors(BigDecimal applicationId, BigDecimal objectionId, Integer pageNumber,
			Integer pageSize, ObjectorSortColumn sortColumn, DescSortDirection sortDirection) {

		Optional<Objection> obj = objectionRepo.findById(objectionId);
		if (!obj.isPresent()) {
			throw new NotFoundException("Objection with id " + objectionId + " not found.");
		}
		if (!obj.get().getApplicationId().equals(applicationId)) {
			throw new DataConflictException(
					"Application ID " + applicationId + " doesn't have an objection with id " + objectionId);
		}
		// keep the conversion from dto column to entity column in the service layer
		Sort sort = getEntitySortColumn(sortColumn, sortDirection);
		// pagination by default uses 0 to start, shift it so we can use number
		// displayed to users
		Pageable request = PageRequest.of(pageNumber - 1, pageSize, sort);

		Page<Objector> resultsPage = repository.getObjectors(request, objectionId);

		ObjectorsPageDto page = new ObjectorsPageDto();

		page.setResults(resultsPage.getContent().stream().map(sbcd -> {
			return getObjectorDto(sbcd);
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());

		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());

		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	}

	private ObjectorDto getObjectorDto(Objector model) {
		ObjectorDto dto = new ObjectorDto();
		Customer customer = model.getCustomer();
		String name = Helpers.buildName(customer.getLastName(), customer.getFirstName(), customer.getMiddleInitial(), customer.getSuffix());
		dto.setName(name);
		dto.setContactId(model.getCustomer().getCustomerId().longValue());
		dto.setEndDate(model.getEndDate());
		dto.setRepresentativeCount(model.getRepresentativeCount());
		return dto;
	}

	private Sort getEntitySortColumn(ObjectorSortColumn sortColumn, DescSortDirection sortDirection) {
		Sort.Direction sort = sortDirection == DescSortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		if (ObjectorSortColumn.CONTACTID == sortColumn)
			orders.add(new Sort.Order(sort, "customerId"));
		if (ObjectorSortColumn.ENDDATE == sortColumn)
			orders.add(new Sort.Order(sort, "endDate"));
		if (ObjectorSortColumn.NAME == sortColumn) {
			orders.add(new Sort.Order(sort, "c.lastName"));
			orders.add(new Sort.Order(sort, "c.firstName"));
			orders.add(new Sort.Order(sort, "c.middleInitial"));
			orders.add(new Sort.Order(sort, "c.suffix"));
		}
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.lastName"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.firstName"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.middleInitial"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "c.suffix"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "customerId"));
		Sort fullSort = Sort.by(orders);
		return fullSort;
	}

}
