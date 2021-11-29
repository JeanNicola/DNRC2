package gov.mt.wris.repositories;

import gov.mt.wris.dtos.RelatedRightSortColumn;
import gov.mt.wris.dtos.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomRelatedRightRepository {

    public Page<Object[]> searchRelatedRights(Pageable pageable, RelatedRightSortColumn sortColumn, SortDirection sortDirection, String relatedRightId, String relationshipType, String waterRightNumber, String basin, String ext);

}
