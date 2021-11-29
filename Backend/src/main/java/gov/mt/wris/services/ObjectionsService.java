package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Cesar.Zamorano
 *
 */
public interface ObjectionsService {

	public ObjectionsPageDto getObjections(Integer pageNumber, Integer pageSize, ObjectionSortColumn sortColumn,
			SortDirection sortDirection, BigDecimal applicationId);

	public WaterRightVersionObjectionsPageDto getWaterRightVersionObjections(Integer pageNumber, Integer pageSize, WaterRightVersionObjectionsSortColumn sortColumn,
																			 SortDirection sortDirection, BigDecimal waterRightId, BigDecimal versionId);
	
    public ObjectionsSearchResultPageDto searchObjections(Integer pageNumber, Integer pageSize, SearchObjectionsSortColumn sortColumn, SortDirection sortDirection, String objectionId,
														  String objectionType, LocalDate filedDate, String objectionLate, String objectionStatus, String basin);

	public int closeCaseObjections(BigDecimal caseId);

	public ObjectionDto updateObjection(BigDecimal objectionId, ObjectionUpdateDto objectionUpdateDto);

        public ObjectionsSearchResultDto createObjection(ObjectionCreationDto createDto);


	public EligibleApplicationsSearchPageDto getEligibleApplications(Integer pagenumber, Integer pagesize, EligibleApplicationsSortColumn sortColumn, SortDirection sortDirection, String applicationId);

}
