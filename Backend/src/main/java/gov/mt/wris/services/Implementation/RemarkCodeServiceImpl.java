package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.AllRemarkCodeReferencesDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.dtos.RemarkCodeDto;
import gov.mt.wris.dtos.RemarkCodeReferenceDto;
import gov.mt.wris.dtos.RemarkCodeSearchPageDto;
import gov.mt.wris.dtos.RemarkCodeSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.RemarkCategory;
import gov.mt.wris.models.RemarkCode;
import gov.mt.wris.models.RemarkWaterRightTypeXref;
import gov.mt.wris.repositories.RemarkCodeRepository;
import gov.mt.wris.repositories.RemarkWaterRightTypeXrefRepository;
import gov.mt.wris.services.RemarkCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RemarkCodeServiceImpl implements RemarkCodeService {

    private static Logger LOGGER = LoggerFactory.getLogger(RemarkCodeServiceImpl.class);

    @Autowired
    RemarkWaterRightTypeXrefRepository remarkWaterRightTypeXrefRepository;

    @Autowired
    RemarkCodeRepository codeRepository;

    public RemarkCodeSearchPageDto searchRemarkCodesByWaterRightType(int pagenumber,
        int pagesize,
        RemarkCodeSortColumn sortColumn,
        SortDirection sortDirection,
        String remarkCode,
        Long waterRightId
    ) {
        LOGGER.info("Search remark codes by water right type");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getCodeSortColumn(sortColumn)).and(Sort.by(Sort.Direction.ASC, "r.code"));

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        Page<RemarkWaterRightTypeXref> results = remarkWaterRightTypeXrefRepository.searchRemarkCodesByWaterRightType(pageable, remarkCode, waterId);

        RemarkCodeSearchPageDto dto = new RemarkCodeSearchPageDto();

        dto.setResults(results.getContent().stream().map(obj -> {
            return getRemarkCodeDto(obj);
        }).collect(Collectors.toList()));

        dto.setCurrentPage(results.getNumber() + 1);
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private String getCodeSortColumn(RemarkCodeSortColumn sortColumn) {
        switch(sortColumn) {
            case REMARKCATEGORYDESCRIPTION:
                return "c.description";
            case REMARKTYPEDESCRIPTION:
                return "t.meaning";
            case REMARKCODE:
                return "r.code";
            default:
                return "r.code";
        }
    }

    private RemarkCodeDto getRemarkCodeDto(RemarkWaterRightTypeXref xref) {

        RemarkCodeDto dto = new RemarkCodeDto();
        RemarkCode model = xref.getRemarkCodeReference();
        RemarkCategory category = model.getCategoryReference();
        dto.setRemarkCode(model.getCode());
        dto.setRemarkTypeDescription(model.getTypeReference().getMeaning());
        dto.setRemarkCategoryDescription(category.getDescription());
        dto.setElementTypeDescription(category.getElementTypeReference().getDescription());
        return dto;
    }

    public AllRemarkCodeReferencesDto getReportRemarkCodes() {
        LOGGER.info("Get the Remark Codes that can be used for a Measurement Report");

        AllRemarkCodeReferencesDto dto = new AllRemarkCodeReferencesDto();

        dto.setResults(
            Stream.concat(
                codeRepository.getMeasurmentReportRemarkCodes()
                .stream()
                .map(code -> getRemarkReferenceDto(code).createable(true)),
                codeRepository.getUsedMeasurementReportRemarkCodes()
                .stream()
                .map(code -> getRemarkReferenceDto(code).createable(false))
            )
            .collect(Collectors.toList())
        );

        return dto;
    }

    private RemarkCodeReferenceDto getRemarkReferenceDto(RemarkCode model) {
        return new RemarkCodeReferenceDto()
            .value(model.getCode())
            .description(model.getCode() + " - " + model.getCategoryReference().getDescription());
    }

}
