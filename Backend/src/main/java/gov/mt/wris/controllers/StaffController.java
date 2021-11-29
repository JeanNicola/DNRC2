package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.StaffApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllStaffDto;
import gov.mt.wris.dtos.SearchStaffPageDto;
import gov.mt.wris.dtos.SearchStaffSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.MasterStaffIndexesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class StaffController implements StaffApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(StaffController.class);

    @Autowired
    private MasterStaffIndexesService staffService;

    public ResponseEntity<AllStaffDto> getAllStaff() {
        LOGGER.info("Getting all the staff");

        AllStaffDto dto = staffService.getAllStaff();

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<SearchStaffPageDto> searchStaff(Integer pageNumber,
                                                          Integer pageSize,
                                                          SearchStaffSortColumn sortColumn,
                                                          SortDirection sortDirection,
                                                          String lastName,
                                                          String firstName) {

        LOGGER.info("Search for Staff");
        SearchStaffPageDto dto = staffService.searchStaffByName(pageNumber, pageSize, sortColumn, sortDirection, lastName, firstName);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<AllStaffDto> getDistrictCourtStaff(Integer districtCourt) {

        LOGGER.info("Search for Staff");
        AllStaffDto dto = staffService.getDistrictCourtStaff(districtCourt);
        return ResponseEntity.ok(dto);

    }
}
