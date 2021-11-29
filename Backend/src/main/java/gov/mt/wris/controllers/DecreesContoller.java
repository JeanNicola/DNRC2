package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.DecreesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SearchBasinsResultPageDto;
import gov.mt.wris.dtos.SearchBasinsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.CaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class DecreesContoller implements DecreesApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(DecreesContoller.class);

    @Autowired
    private CaseService caseService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<SearchBasinsResultPageDto> searchBasins(Integer pageNumber,
                                                                  Integer pageSize,
                                                                  SearchBasinsSortColumn sortColumn,
                                                                  SortDirection sortDirection,
                                                                  String basin) {
        LOGGER.info("Search for Basins");
        SearchBasinsResultPageDto dto = caseService.searchBasins(pageNumber, pageSize, sortColumn, sortDirection, basin);
        return ResponseEntity.ok(dto);
    }

}
