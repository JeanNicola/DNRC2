package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.SubCompactsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubcompactPageDto;
import gov.mt.wris.dtos.SubcompactSortColumn;
import gov.mt.wris.services.SubcompactService;

@Controller
public class SubcompactsController implements SubCompactsApiDelegate {
	private static Logger LOGGER = LoggerFactory.getLogger(SubcompactsController.class);

    @Autowired
    SubcompactService subcompactService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.SUBCOMPACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COMPACT_TABLE)
    })
    public ResponseEntity<SubcompactPageDto> searchSubcompacts(Integer pageNumber,
        Integer pageSize,
        SubcompactSortColumn sortColumn,
        SortDirection sortDirection,
        String subcompact,
        String compact
    ) {
        LOGGER.info("Searching subcompacts");

        SubcompactPageDto dto = subcompactService.searchSubcompacts(pageNumber, pageSize, sortColumn, sortDirection, subcompact, compact);

        return ResponseEntity.ok(dto);
    }
}
