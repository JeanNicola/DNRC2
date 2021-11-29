package gov.mt.wris.services;

import gov.mt.wris.dtos.CopyDiversionToPeriodResultsDto;
import gov.mt.wris.dtos.PeriodOfUseCreationDto;
import gov.mt.wris.dtos.PeriodOfUseDto;
import gov.mt.wris.dtos.PeriodOfUseUpdateDto;
import gov.mt.wris.dtos.PeriodsOfUsePageDto;
import gov.mt.wris.dtos.PeriodsOfUseSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;

public interface PeriodOfUseService {

    public CopyDiversionToPeriodResultsDto copyFirstPeriodOfDiversionToPeriodOfUse(BigDecimal purposeId);

    public PeriodsOfUsePageDto getPeriodsOfUse(int pageNumber, int pageSize, PeriodsOfUseSortColumn sortColumn, SortDirection sortDirection, Long purposeId);

    public PeriodOfUseDto getPeriodOfUse(BigDecimal periodId);

    public PeriodOfUseDto updatePeriodOfUse(BigDecimal periodId, PeriodOfUseUpdateDto updateDto);

    public void deletePeriodOfUse(BigDecimal periodId);

    public PeriodOfUseDto createPeriodOfUse(BigDecimal purposeId, PeriodOfUseCreationDto createDto);

}
