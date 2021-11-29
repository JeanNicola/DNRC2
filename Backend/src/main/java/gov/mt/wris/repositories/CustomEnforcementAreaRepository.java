package gov.mt.wris.repositories;

import gov.mt.wris.dtos.EnforcementsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.PointOfDiversionEnforcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEnforcementAreaRepository {

    public Page<PointOfDiversionEnforcement> searchEnforcements(Pageable pageable, EnforcementsSortColumn sortColumn, SortDirection sortDirection, String area, String name, String enforcementNumber, String basin, String waterNumber);

}
