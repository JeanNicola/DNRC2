package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

import java.math.BigDecimal;

public interface DataSourceService {

    public DataSourceDto getDataSourceDetails(BigDecimal pexmId);

    public DataSourceDto updateDataSource(BigDecimal pexmId, DataSourceCreationDto dataSourceCreationDto);

    public UsgsPageDto getUsgsQuads(BigDecimal pexmId, Integer pageNumber, Integer pageSize, UsgsSortColumn sortColumn, SortDirection sortDirection);

    public UsgsDto createUsgsQuadMap(BigDecimal pexmId, UsgsCreationDto usgsCreationDto);

    public void deleteUsgsQuadMap(BigDecimal pexmId, BigDecimal utmpId);

    public AerialPhotoPageDto getAerialPhotos(BigDecimal pexmId, Integer pageNumber, Integer pageSize, AerialPhotoSortColumn sortColumn, SortDirection sortDirection);

    public AerialPhotoDto createAerialPhoto(BigDecimal pexmId, AerialPhotoCreationDto aerialPhotoCreationDto);

    public AerialPhotoDto updateAerialPhoto(BigDecimal pexmId, BigDecimal aerialId, AerialPhotoCreationDto aerialPhotoCreationDto);

    public void deleteAerialPhoto(BigDecimal pexmId, BigDecimal aerialId);

    public WaterResourceSurveyPageDto getWaterSurveys(BigDecimal pexmId, Integer pageNumber, Integer pageSize, WaterResourceSurveySortColumn sortColumn, SortDirection sortDirection);

    public WaterResourceSurveyDto createWaterSourceSurvey(BigDecimal pexmId, WaterResourceSurveyCreationDto waterResourceSurveyCreationDto);

    public void deleteWaterResourceSurvey(BigDecimal pexmId, BigDecimal surveyId);

    public void populateParcels(BigDecimal pexmId);

    public ParcelPageDto getParcels(BigDecimal pexmId, Integer pageNumber, Integer pageSize, ParcelSortColumn sortColumn, SortDirection sortDirection);

    public ParcelDto updateParcel(BigDecimal pexmId, BigDecimal placeId, ParcelUpdateDto parcelUpdateDto);

    public void deleteParcel(BigDecimal pexmId, BigDecimal placeId);

    public AllReferencesDto getExamInfoValues(BigDecimal pexmId);

}
