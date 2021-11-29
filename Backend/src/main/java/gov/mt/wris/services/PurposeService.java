package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

import java.math.BigDecimal;

public interface PurposeService {

    public PurposesSearchPageDto searchPurposes(Integer pageNumber,
                                                Integer pageSize,
                                                PurposesSortColumn sortColumn,
                                                SortDirection sortDirection,
                                                PurposeSearchType purposeSearchType,
                                                String basin,
                                                String waterRightNumber,
                                                String waterRightType,
                                                String ext,
                                                String versionType,
                                                String versionNumber);

    public PurposeDetailDto getPurpose(BigDecimal purposeId);

    public PurposeDetailDto createPurpose(BigDecimal waterRightId, BigDecimal versionId, WaterRightVersionPurposeCreationDto dto);

    public PurposeDetailDto updatePurpose(BigDecimal purposeId, PurposeUpdateDto dto);

    public void deletePurpose(BigDecimal purposeId);

}
