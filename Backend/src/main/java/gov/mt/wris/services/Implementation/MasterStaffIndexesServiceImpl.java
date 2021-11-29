/**
 * 
 */
package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllStaffDto;
import gov.mt.wris.dtos.SearchStaffPageDto;
import gov.mt.wris.dtos.SearchStaffResultDto;
import gov.mt.wris.dtos.SearchStaffSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.StaffDropdownDto;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.security.UserData;
import gov.mt.wris.services.MasterStaffIndexesService;
import gov.mt.wris.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of MasterStaffIndexesService
 * 
 * @author Cesar.Zamorano
 */
@Service
public class MasterStaffIndexesServiceImpl implements MasterStaffIndexesService {

	private static Logger LOGGER = LoggerFactory.getLogger(MasterStaffIndexesServiceImpl.class);

	@Autowired
	MasterStaffIndexesRepository repository;

	@Override
	public UserData getUserDataByDirectoryUserName(String directoryUserName) {
		LOGGER.info("Getting user data from MasterStaffIndexes, by directoryUser: " + directoryUserName);

		UserData resp = new UserData();
		MasterStaffIndexes master;

		Optional<MasterStaffIndexes> data = repository.findByDirectoryUserAndEndDateNull(directoryUserName);
		if (data.isPresent()) {
			// Get the database environment
			String dbEnv = repository.getDatabaseEnvironment();
			// Parse off first part of database name - assume format of <env>.ISD.DOA.STATE.MT.US
			dbEnv = dbEnv.substring( 0, dbEnv.indexOf("."));
			
			master = data.get();
			resp.setFirstName(master.getFirstName());
			resp.setMidInitial(master.getMidInitial());
			resp.setLastName(master.getLastName());
			resp.setOfficeId(master.getOfficeId());
			resp.setDatabaseEnv(dbEnv);
			return resp;
		} else {
			return null;
		}
	}

	@Override
	public MasterStaffIndexes getLocationStaffInfo(String directoryUserName) {
		LOGGER.info("Getting office Id from MasterStaffIndexes, by directoryUser: " + directoryUserName);

		Optional<MasterStaffIndexes> data = repository.findByDirectoryUserAndEndDateNull(directoryUserName);
		if (data.isPresent()) {
			return data.get();
		} else {
			return null;
		}
	}

	public AllStaffDto getAllStaff() {
		LOGGER.info("Getting a list of all the staff");

		List<MasterStaffIndexes> allStaff = repository.findAllByEndDateIsNullOrderByLastNameAscFirstNameAsc();

		AllStaffDto dto = new AllStaffDto();
		dto.setResults(allStaff.stream().map(staff -> {
			return getStaffDto(staff);
		}).collect(Collectors.toList()));

		return dto;
	}

	private StaffDropdownDto getStaffDto(MasterStaffIndexes staff) {
		StaffDropdownDto dto = new StaffDropdownDto();
		String firstName = staff.getFirstName() != null ? staff.getFirstName() + " ":"";
		String name = firstName + staff.getLastName();
		dto.setName(name);
		dto.setStaffId(staff.getId().longValue());
		return dto;
	}

	public SearchStaffPageDto searchStaffByName(Integer pagenumber, Integer pagesize, SearchStaffSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName) {

		LOGGER.info("Search for Staff");

		Sort sortDtoColumn = getSearchStaffSortColumn(sortColumn, sortDirection);
		Pageable pageable = PageRequest.of(pagenumber -1, pagesize, sortDtoColumn);

		Page<MasterStaffIndexes> resultsPage = repository.searchAllByName(pageable, lastName, firstName);
		SearchStaffPageDto page = new SearchStaffPageDto();
		page.setResults(resultsPage.getContent().stream().map(row -> {
			SearchStaffResultDto dto = new SearchStaffResultDto();
            dto.setCompleteName(Helpers.buildName(row.getLastName(), row.getFirstName(), row.getMidInitial()));
            dto.setDnrcId(row.getId().toString());
			return dto;
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());
		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());
		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		return page;

	}


	private Sort getSearchStaffSortColumn(SearchStaffSortColumn sortColumn, SortDirection sortDirection) {

		Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
		List<Sort.Order> orders = new ArrayList<>();
		if (sortColumn==SearchStaffSortColumn.NAME) {
			orders.add(new Sort.Order(direction, "lastName"));
			orders.add(new Sort.Order(direction, "firstName"));
			orders.add(new Sort.Order(direction, "midInitial"));
		} else if (sortColumn==SearchStaffSortColumn.STAFFID) {
			orders.add(new Sort.Order(direction, "id"));
		}
		orders.add(new Sort.Order(Sort.Direction.ASC, "lastName"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "firstName"));
		orders.add(new Sort.Order(Sort.Direction.ASC, "midInitial"));
		Sort fullSort = Sort.by(orders);
		return fullSort;

	}

	public AllStaffDto getDistrictCourtStaff(Integer districtCourt) {

		LOGGER.info("Getting a list of all the staff for district court");

		List<MasterStaffIndexes> allStaff =
		    repository.findMasterStaffIndexesByDistrictCourtAndPositionCodeOrderByLastNameAscFirstNameAsc(
		        districtCourt,
		        Constants.DISTRICT_COURT_STAFF
			);
		AllStaffDto allDto = new AllStaffDto();
		allDto.setResults(allStaff.stream().map(staff -> {
			StaffDropdownDto dto = new StaffDropdownDto();
			dto.setStaffId(staff.getId().longValue());
			dto.setName(
			    Helpers.buildName(staff.getLastName(), staff.getFirstName(), staff.getMidInitial())
			);
			return dto;
		}).collect(Collectors.toList()));
		return allDto;

	}

}
