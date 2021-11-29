package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.RemarksApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.RemarkCodeSearchPageDto;
import gov.mt.wris.dtos.RemarkCodeSortColumn;
import gov.mt.wris.dtos.RemarkDto;
import gov.mt.wris.dtos.RemarkTextDto;
import gov.mt.wris.dtos.RemarkUpdateDto;
import gov.mt.wris.dtos.RemarkVariableDto;
import gov.mt.wris.dtos.RemarkVariableUpdateDto;
import gov.mt.wris.dtos.RemarkVariablesPageDto;
import gov.mt.wris.dtos.RemarkVariablesSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.RemarkCodeService;
import gov.mt.wris.services.RemarkService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class RemarksController implements RemarksApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(RemarksController.class);

    @Autowired
    RemarkCodeService remarkCodeService;

    @Autowired
    RemarkService remarkService;

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.REMARK_CODES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REMARK_CATEGORIES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REMARK_WATER_RIGHT_TYPE_XREF_TABLE)
    })
    public ResponseEntity<RemarkCodeSearchPageDto> searchRemarkCodes(Long waterRightId,
        Integer pageNumber,
        Integer pageSize,
        RemarkCodeSortColumn sortColumn,
        SortDirection sortDirection,
        String remarkCode
    ) {
        LOGGER.info("Search remark codes");
        return ResponseEntity.ok(remarkCodeService.searchRemarkCodesByWaterRightType(pageNumber, pageSize, sortColumn, sortDirection, remarkCode, waterRightId));
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_VARIABLE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_ELEMENTS_TABLE)
    })
    public ResponseEntity<RemarkVariablesPageDto> getRemarkVariables(Long remarkId,
        Integer pageNumber,
        Integer pageSize,
        RemarkVariablesSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a Page of Variables for a Remark");

        RemarkVariablesPageDto dto = remarkService.getRemarkVariables(remarkId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_ELEMENTS_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.REMARK_ELEMENTS_TABLE)
    })
    public ResponseEntity<RemarkVariableDto> changeRemarkVariable(Long remarkId,
        Long variableId,
        RemarkVariableUpdateDto updateDto
    ) {
        LOGGER.info("Updating a Remark Variable");

        RemarkVariableDto dto = remarkService.editVariable(remarkId, variableId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_VARIABLE_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.REMARK_VARIABLE_TABLE)
    })
    public ResponseEntity<RemarkDto> changeRemark(Long remarkId,
        RemarkUpdateDto updateDto
    ) {
        LOGGER.info("Updating a Remark");

        RemarkDto dto = remarkService.updateRemark(remarkId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.REMARK_ELEMENTS_TABLE)
    })
    public ResponseEntity<Void> deleteRemark(Long remarkId) {
        LOGGER.info("Deleting a Remark");

        remarkService.deleteRemark(remarkId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_CODES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_ELEMENTS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_VARIABLE_TABLE)
    })
    public ResponseEntity<RemarkTextDto> getRemarkText(Long remarkId) {
        LOGGER.info("Getting the text of a remark");

        RemarkTextDto dto = remarkService.getRemarkText(remarkId);

        return ResponseEntity.ok(dto);
    }
}
