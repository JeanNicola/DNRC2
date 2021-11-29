package gov.mt.wris.services;

import gov.mt.wris.dtos.PurposeUpdateDto;
import gov.mt.wris.dtos.WaterRightVersionPurposeCreationDto;
import gov.mt.wris.models.PeriodOfUse;
import gov.mt.wris.models.PurposeVolumeCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PurposeVolumeCalculationService {

    public PurposeVolumeCalculation calculateVolume(BigDecimal purposeId, String purposeType, int household, String clarCode, BigDecimal animalUnits, List<PeriodOfUse> periods);

}
