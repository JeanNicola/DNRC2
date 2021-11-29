package gov.mt.wris.repositories;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Usgs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUsgsRepository {

    public Page<Usgs> searchUsgsQuadMapValues(Pageable pageable, SortDirection sortDirection, String usgsQuadMapName);

}
