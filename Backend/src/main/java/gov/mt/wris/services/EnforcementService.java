package gov.mt.wris.services;

import gov.mt.wris.dtos.AllEnforcementsDto;
import gov.mt.wris.dtos.EnforcementDto;
import gov.mt.wris.dtos.EnforcementPodPageDto;
import gov.mt.wris.dtos.EnforcementPodsSortColumn;
import gov.mt.wris.dtos.EnforcementsSearchPageDto;
import gov.mt.wris.dtos.EnforcementsSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface EnforcementService {

    public AllEnforcementsDto findAll();

    public EnforcementDto createEnforcement(EnforcementDto creationDto);

    public EnforcementsSearchPageDto searchEnforcements(int pagenumber, int pagesize, EnforcementsSortColumn sortColumn, SortDirection sortDirection, String area, String name, String enforcementNumber, String basin, String waterNumber);

    public EnforcementPodPageDto getEnforcementPods(int pageNumber, int pageSize, EnforcementPodsSortColumn sortColumn, SortDirection sortDirection, String area);

    public EnforcementDto getEnforcement(String area);

    public EnforcementDto updateEnforcementArea(String area, EnforcementDto updateDto);

}
