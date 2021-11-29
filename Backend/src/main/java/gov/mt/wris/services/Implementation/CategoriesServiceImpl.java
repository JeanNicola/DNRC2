package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.CategoriesPageDto;
import gov.mt.wris.dtos.CategoryDto;
import gov.mt.wris.dtos.CategorySortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CorrectComplete;
import gov.mt.wris.models.Objection;
import gov.mt.wris.repositories.CorrectCompletesRepository;
import gov.mt.wris.repositories.ObjectionsRepository;
import gov.mt.wris.services.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Cesar.Zamorano
 *
 */
@Service
public class CategoriesServiceImpl implements CategoriesService {

	@Autowired
	private CorrectCompletesRepository repository;

	@Autowired
	private ObjectionsRepository objectionRepo;

	@Override
	public CategoriesPageDto getCategories(BigDecimal applicationId, BigDecimal objectionId, Integer pageNumber,
			Integer pageSize, CategorySortColumn sortColumn, SortDirection sortDirection) {

		Optional<Objection> obj = objectionRepo.findById(objectionId);
		if (!obj.isPresent()) {
			throw new NotFoundException("Objection with id " + objectionId + " not found.");
		}
		if (!obj.get().getApplicationId().equals(applicationId)) {
			throw new DataConflictException(
					"Application ID " + applicationId + " doesn't have an objection with id " + objectionId);
		}
		// keep the conversion from dto column to entity column in the service layer
		String sortDtoColumn = getEntitySortColumn(sortColumn);
		// pagination by default uses 0 to start, shift it so we can use number
		// displayed to users
		Pageable request = null;
		if (sortDirection == SortDirection.ASC) {
			request = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.ASC, sortDtoColumn).and(Sort.by(Sort.Direction.ASC, "id")));
		} else {
			request = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, sortDtoColumn).and(Sort.by(Sort.Direction.ASC, "id")));
		}

		Page<CorrectComplete> resultsPage = repository.getCorrectCompletes(request, objectionId);

		CategoriesPageDto page = new CategoriesPageDto();

		page.setResults(resultsPage.getContent().stream().map(sbcd -> {
			return getCategoryDto(sbcd);
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());

		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());

		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;
	}

	private CategoryDto getCategoryDto(CorrectComplete model) {
		CategoryDto dto = new CategoryDto();
		dto.setDeterminationDate(model.getCorrectCompleteDate());
		dto.setCategoryTypeCode(model.getCorrectCompleteType().getCode());
		dto.setCategoryTypeDescription(model.getCorrectCompleteType().getDescription());
		dto.setId(model.getId().longValue());
		return dto;
	}

	private String getEntitySortColumn(CategorySortColumn sortColumn) {
		if (CategorySortColumn.ID == sortColumn)
			return "id";
		if (CategorySortColumn.CATEGORYTYPECODE == sortColumn)
			return "t.code";
		if (CategorySortColumn.CATEGORYTYPEDESCRIPTION == sortColumn)
			return "t.description";
		if (CategorySortColumn.DETERMINATIONDATE == sortColumn)
			return "correctCompleteDate";
		return "id";
	}

}
