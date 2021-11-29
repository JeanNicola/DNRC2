package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.VersionCompactDto;
import gov.mt.wris.dtos.VersionCompactSortColumn;
import gov.mt.wris.dtos.VersionCompactsPageDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Subcompact;
import gov.mt.wris.models.VersionCompact;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.VersionCompactRepository;
import gov.mt.wris.services.VersionCompactService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VersionCompactServiceImpl implements VersionCompactService {
    private static Logger LOGGER = LoggerFactory.getLogger(VersionCompactService.class);

    @Autowired
    VersionCompactRepository compactRepository;

    @Autowired
    MasterStaffIndexesRepository staffRepository;

    public VersionCompactsPageDto getVersionCompacts(
        int pagenumber,
        int pagesize,
        VersionCompactSortColumn sortColumn,
        SortDirection sortDirection,
        Long waterRightId,
        Long versionNumber
    ) {
        LOGGER.info("Getting a page of Compacts attached to a Version");

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getVersionCompactSortColumn(sortColumn));
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        Page<VersionCompact> results = compactRepository.findCompactsByVersion(pageable, waterId, version);

        VersionCompactsPageDto dto = new VersionCompactsPageDto();
        dto.setResults(results.getContent().stream()
            .map(compact -> getVersionCompactDto(compact))
            .collect(Collectors.toList())
        );

        dto.setCurrentPage(results.getNumber() + 1);
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private String getVersionCompactSortColumn(VersionCompactSortColumn sortColumn) {
        switch(sortColumn) {
            case COMPACT:
                return "c.name";
            case EXEMPTCOMPACT:
                return "exempt";
            case ALLOCATION:
                return "affects";
            case BLM:
                return "transbasin";
            default:
                return "s.name";
        }
    }

    private VersionCompactDto getVersionCompactDto(VersionCompact compact) {
        VersionCompactDto dto = new VersionCompactDto()
            .id(compact.getId().longValue())
            .subcompactId(compact.getSubcompactId().longValue())
            .allocation("Y".equals(compact.getAffects()))
            .blm("Y".equals(compact.getTransbasin()))
            .exemptCompact("Y".equals(compact.getExempt()));
        Subcompact subcompact = compact.getSubcompact();
        if (subcompact != null) {
            dto.setSubcompact(subcompact.getName());
            dto.setCompactId(subcompact.getCompact().getId().longValue());
            dto.setCompact(subcompact.getCompact().getName());
        }
        return dto;
    }

    public VersionCompactDto createVersionCompact(Long waterRightId, Long versionNumber, VersionCompactDto dto) {
        LOGGER.info("Adding a new Compact to a Version");

        validateCompactDto(dto);

        VersionCompact compact = getVersionCompact(dto, waterRightId, versionNumber);

        try {
            compact = compactRepository.save(compact);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("AFWR_SBWR_UK")) {
                    throw new ValidationException("Cannot add the same Compact to a Water Right Version twice.");
                }
            }
            throw e;
        }

        return getVersionCompactDto(compact);
    }

    private VersionCompact getVersionCompact(VersionCompactDto dto, Long waterRightId, Long versionNumber) {
        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);
        VersionCompact compact = new VersionCompact();
        compact.setWaterRightId(waterId);
        compact.setVersionId(version);
        compact.setSubcompactId(BigDecimal.valueOf(dto.getSubcompactId()));
        compact.setTransbasin(dto.getBlm() != null && dto.getBlm() ? "Y" : "N");
        compact.setAffects(dto.getAllocation() != null && dto.getAllocation() ? "Y" : "N");
        compact.setExempt(dto.getExemptCompact() != null && dto.getExemptCompact() ? "Y" : "N");
        return compact;
    }

    private void validateCompactDto(VersionCompactDto dto) {
        if(((dto.getExemptCompact() != null && dto.getExemptCompact()) ||
            (dto.getAllocation() != null && dto.getAllocation())) &&
            (dto.getBlm() != null && dto.getBlm())
        ) {
            throw new ValidationException("When Exempt From Compact or Version Affect Allocation is checked, BLM Transbasin must be unchecked.");
        }
    }

    public VersionCompactDto updateVersionCompact(Long waterRightId, Long versionNumber, Long compactId, VersionCompactDto dto) {
        LOGGER.info("Adding a new Compact to a Version");

        validateCompactDto(dto);

        BigDecimal id = BigDecimal.valueOf(compactId);

        VersionCompact compact = getVersionCompact(dto, waterRightId, versionNumber);
        Optional<VersionCompact> foundCompact = compactRepository.findFullCompactById(id);
        if(!foundCompact.isPresent()) {
            throw new NotFoundException("This Compact does not exist");
        }
        compact = foundCompact.get();

        String allocation = dto.getAllocation() != null && dto.getAllocation() ? "Y" : "N";
        if (
            staffRepository.hasRoles(Arrays.asList(Constants.COMPACT_ROLE)) == 0 &&
            !allocation.equals(compact.getAffects())
        ) {
            throw new ValidationException("Not allowed to change the Compact Role");
        }

        compact.setTransbasin(dto.getBlm() != null && dto.getBlm() ? "Y" : "N");
        compact.setAffects(allocation);
        compact.setExempt(dto.getExemptCompact() != null && dto.getExemptCompact() ? "Y" : "N");

        try {
            compact = compactRepository.save(compact);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("AFWR_SBWR_UK")) {
                    throw new ValidationException("Cannot add the same Compact to a Water Right Version twice.");
                }
            }
            throw e;
        }

        return getVersionCompactDto(compact);
    }

    public void deleteVersionCompact(Long compactId) {
        LOGGER.info("Delete a Compact attached to a Version");

        BigDecimal id = BigDecimal.valueOf(compactId);

        compactRepository.deleteById(id);
    }
}
