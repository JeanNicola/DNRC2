package gov.mt.wris.services;

import java.math.BigDecimal;

import gov.mt.wris.dtos.AllStaffDto;
import gov.mt.wris.dtos.SearchStaffPageDto;
import gov.mt.wris.dtos.SearchStaffSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.security.UserData;

/**
 * The service which contains any type of operation against MasterStaffIndexes
 * table.
 * 
 * @author Cesar.Zamorano
 *
 */
public interface MasterStaffIndexesService {

	/**
	 * Returns Full name of a User by his Active Directory Username
	 *
	 * @param directoryUserName
	 * @return
	 */
	public UserData getUserDataByDirectoryUserName(String directoryUserName);
	
	
	/**
	 * Returns OfficeId of the current User.
	 * 
	 * @return officeId
	 */
	public MasterStaffIndexes getLocationStaffInfo(String directoryUserName);

	public AllStaffDto getAllStaff();

    SearchStaffPageDto searchStaffByName(Integer pageNumber, Integer pageSize, SearchStaffSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName);

	public AllStaffDto getDistrictCourtStaff(Integer districtCourt);

}
