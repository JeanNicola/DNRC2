package gov.mt.wris.repositories;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gov.mt.wris.dtos.ApplicantDto;
import gov.mt.wris.dtos.ApplicantSortColumn;

public interface CustomOwnerRepository {
    Page<ApplicantDto> findApplicantsByApplicationId(
        Pageable pageable,
        BigDecimal applicationId,
        ApplicantSortColumn column,
        Sort.Direction direction
    );
}
