package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.RemarkDto;
import gov.mt.wris.dtos.RemarkUpdateDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.VersionRemarkCreateDto;
import gov.mt.wris.dtos.VersionRemarksPageDto;
import gov.mt.wris.dtos.VersionRemarksSortColumn;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.RemarkCode;
import gov.mt.wris.models.VersionRemark;
import gov.mt.wris.repositories.RemarkElementRepository;
import gov.mt.wris.repositories.VersionRemarkRepository;
import gov.mt.wris.services.VersionRemarkService;

@Service
public class VersionRemarkServiceImpl implements VersionRemarkService {
    private static Logger LOGGER = LoggerFactory.getLogger(VersionRemarkService.class);

    @Autowired
    VersionRemarkRepository remarkRepository;

    @Autowired
    RemarkElementRepository elementRepository;

    public VersionRemarksPageDto getVersionRemarks(Long waterRightId,
        Long versionNumber,
        int pagenumber,
        int pagesize,
        VersionRemarksSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a Page of Remarks for a Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getRemarkSortColumn(sortColumn)).and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        Page<VersionRemark> results = remarkRepository.findRemarksByVersion(pageable, waterId, version);

        VersionRemarksPageDto dto = new VersionRemarksPageDto();

        dto.setResults(results.getContent().stream()
            .map(remark -> getVersionRemarkDto(remark))
            .collect(Collectors.toList()));
        
        dto.setCurrentPage(results.getNumber());
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private String getRemarkSortColumn(VersionRemarksSortColumn sortColumn) {
        switch(sortColumn) {
            case REMARKCODE:
                return "remarkCode";
            case ADDEDDATE:
                return "date";
            case REMARKCATEGORYDESCRIPTION:
                return "c.description";
            case REMARKTYPEDESCRIPTION:
                return "t.meaning";
            case REMARKSTATUSDESCRIPTION:
                return "s.meaning";
            default:
                return "remarkCode";
        }
    }

    private RemarkDto getVersionRemarkDto(VersionRemark remark) {
        RemarkCode library = remark.getRemarkCodeLibrary();
        return new RemarkDto()
            .remarkId(remark.getId().longValue())
            .remarkCode(remark.getRemarkCode())
            .addedDate(remark.getDate())
            .remarkCategoryDescription(library.getCategoryReference().getDescription())
            .remarkTypeDescription(library.getTypeReference().getMeaning())
            .remarkStatusDescription(library.getStatusReference().getMeaning());
    }

    @Transactional
    public RemarkDto createRemark(Long waterRightId,
        Long versionNumber,
        VersionRemarkCreateDto createDto
    ) {
        LOGGER.info("Creating a Remark attached to a Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        VersionRemark remark = getVersionRemark(createDto);
        remark.setWaterRightId(waterId);
        remark.setVersion(version);

        remark = remarkRepository.save(remark);

        elementRepository.createVariables(remark.getId());

        return new RemarkDto()
                .remarkId(remark.getId().longValue())
                .remarkCode(remark.getRemarkCode())
                .addedDate(remark.getDate());
    }

    private VersionRemark getVersionRemark(VersionRemarkCreateDto dto) {
        VersionRemark remark = new VersionRemark();
        remark.setRemarkCode(dto.getRemarkCode());
        remark.setDate(dto.getAddedDate());
        remark.setTypeIndicator("R");
        return remark;
    }
}
