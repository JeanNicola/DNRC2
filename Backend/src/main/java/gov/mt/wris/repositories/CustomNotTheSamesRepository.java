package gov.mt.wris.repositories;

import gov.mt.wris.dtos.NotTheSameSearchResultDto;
import gov.mt.wris.dtos.NotTheSameSortColumn;
import gov.mt.wris.dtos.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CustomNotTheSamesRepository {
    public Page<NotTheSameSearchResultDto> searchNotTheSamesByCustomerId(Pageable pageable, NotTheSameSortColumn sortColumn, SortDirection sortDirection, BigDecimal contactId);
}
