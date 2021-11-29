package gov.mt.wris.services;

import gov.mt.wris.dtos.DataSourceCreationDto;
import gov.mt.wris.dtos.DataSourceDto;
import gov.mt.wris.dtos.DataSourcePageDto;
import gov.mt.wris.dtos.DataSourceSortColumn;
import gov.mt.wris.dtos.ExaminationCreationDto;
import gov.mt.wris.dtos.ExaminationDetailDto;
import gov.mt.wris.dtos.ExaminationsSearchPageDto;
import gov.mt.wris.dtos.ExaminationsSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;

public interface ExaminationService {

    public ExaminationDetailDto getExamination(Long examinationId);

    public ExaminationDetailDto updateExamination(BigDecimal examinationId, ExaminationCreationDto examinationCreationDtoDto);

    public ExaminationDetailDto createExamination(BigDecimal purposeId, ExaminationCreationDto examinationCreationDto);

    public ExaminationsSearchPageDto searchExaminations(Integer pageNumber,
                                                    Integer pageSize,
                                                    ExaminationsSortColumn sortColumn,
                                                    SortDirection sortDirection,
                                                    String basin,
                                                    String waterRightNumber,
                                                    String waterRightType,
                                                    String versionType,
                                                    String versionNumber);

    public DataSourcePageDto getExaminationDataSources(BigDecimal examinationId, Integer pageNumber, Integer pageSize, DataSourceSortColumn sortColumn, SortDirection sortDirection);

    public DataSourceDto createDataSourceForExamination(BigDecimal examinationId, DataSourceCreationDto dataSourceCreationDto);

    public void deleteDataSource(BigDecimal examinationId, BigDecimal pexmId);

}
