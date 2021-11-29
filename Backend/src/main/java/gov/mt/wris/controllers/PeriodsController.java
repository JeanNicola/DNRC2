package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.PeriodsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.PeriodOfUseDto;
import gov.mt.wris.dtos.PeriodOfUseUpdateDto;
import gov.mt.wris.services.PeriodOfUseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class PeriodsController implements PeriodsApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(PeriodsController.class);

    @Autowired
    private PeriodOfUseService periodOfUseService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<PeriodOfUseDto> getPeriodOfUse(Long periodId) {

        LOGGER.info("Get Period Of Use");
        PeriodOfUseDto dto = periodOfUseService.getPeriodOfUse(new BigDecimal(periodId));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<PeriodOfUseDto> updatePeriodOfUse(Long periodId, PeriodOfUseUpdateDto updateDto) {

        LOGGER.info("Update Period Of Use");
        PeriodOfUseDto dto = periodOfUseService.updatePeriodOfUse(new BigDecimal(periodId), updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<Void> deletePeriodOfUse(Long periodId) {

        LOGGER.info("Delete Period Of Use");
        periodOfUseService.deletePeriodOfUse(new BigDecimal(periodId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);

    }

}
