package gov.mt.wris.repositories;

import gov.mt.wris.dtos.ExaminationsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Examination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomExaminationRepository {
    public Page<Examination> searchExaminations(Pageable pageable, ExaminationsSortColumn sortColumn, SortDirection sortDirection, String basin, String waterRightNumber, String waterRightType, String versionType, String versionNumber);
}
