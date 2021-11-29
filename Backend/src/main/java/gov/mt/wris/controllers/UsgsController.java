package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.UsgsQuadMapValuesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.UsgsPageDto;
import gov.mt.wris.services.UsgsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class UsgsController implements UsgsQuadMapValuesApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(UsgsController.class);

    @Autowired
    private UsgsService usgsService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.USGS_TABLE)
    })
    public ResponseEntity<UsgsPageDto> getUsgsQuadMapsList(Integer pageNumber, Integer pageSize, SortDirection sortDirection, String name) {
        LOGGER.info("Search Usgs Quad Map values");
        UsgsPageDto dto = usgsService.getUsgsQuadMapsList(pageNumber, pageSize, sortDirection, name);
        return ResponseEntity.ok(dto);
    }

}
