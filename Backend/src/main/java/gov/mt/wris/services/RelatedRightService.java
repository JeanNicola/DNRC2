package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

import java.math.BigDecimal;


public interface RelatedRightService {

    public RelatedRightElementsPageDto getRelatedRightElements(Integer pageNumber,
                                                               Integer pageSize,
                                                               RelatedRightElementsSortColumn sortColumn,
                                                               SortDirection sortDirection,
                                                               Long relatedRightId);

    public void deleteRelatedRightElement(Long relatedRightId, String elementType);

    public RelatedRightElementsSearchResultDto createRelatedRightElement(Long relatedRightId, RelatedRightElementCreationDto newElement);

    public WaterRightReferenceToRelatedRightSearchResultDto createWaterRightReferenceToRelatedRight(Long relatedRightId, WaterRightReferenceToRelatedRightCreationDto dto);

   public WaterRightsVersionsPageDto searchWaterRightsVersions(Long relatedRightId,
                                                               Integer pageNumber,
                                                               Integer pageSize,
                                                               WaterRightVersionsForRelatedRightSortColumn sortColumn,
                                                               SortDirection sortDirection,
                                                               String basin,
                                                               String waterNumber,
                                                               String ext);

   public WaterRightsVersionsPageDto searchWaterRightsVersionsAll(Integer pageNumber,
                                                               Integer pageSize,
                                                               WaterRightVersionsForRelatedRightSortColumn sortColumn,
                                                               SortDirection sortDirection,
                                                               String basin,
                                                               String waterNumber,
                                                               String ext);

    public void deleteWaterRightReferenceToRelatedRight(Long relatedRightId, Long waterRightId, Long versionId);

    public RelatedRightsPageDto searchRelatedRights(int pageNumber, int pageSize, RelatedRightSortColumn sortColumn, SortDirection sortDirection, String relatedRightId, String relationshipType, String waterRightNumber, String basin, String ext);

    public RelatedRightDto getRelatedRightDetails(Long relatedRightId);

   public RelatedRightWaterRightPageDto getRelatedRightWaterRights(Integer pageNumber, Integer pageSize, RelatedRightWaterRightSortColumn sortColumn, SortDirection sortDirection, Long relatedRightId, String returnVersions);

    public RelatedRightDto changeRelatedRight(BigDecimal relatedRightId, UpdateRelatedRightDto updateRelatedRightDto);

   public RelatedRightCreationResultDto createRelatedRight(RelatedRightCreationDto newRelatedRight);

    public void deleteRelatedRight(BigDecimal relatedRightId);

}

