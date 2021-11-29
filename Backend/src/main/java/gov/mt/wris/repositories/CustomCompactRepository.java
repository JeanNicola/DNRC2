package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubcompactSortColumn;
import gov.mt.wris.models.Subcompact;

public interface CustomCompactRepository {
    public Page<Subcompact> searchSubcompacts(Pageable pageable, SubcompactSortColumn sortColumn, SortDirection sortDirection, String subcompact, String compact);
}
