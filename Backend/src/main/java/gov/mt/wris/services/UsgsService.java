package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.UsgsPageDto;

public interface UsgsService {

    public UsgsPageDto getUsgsQuadMapsList(Integer pageNumber, Integer pageSize, SortDirection sortDirection, String usgsQuadMapName);

}
