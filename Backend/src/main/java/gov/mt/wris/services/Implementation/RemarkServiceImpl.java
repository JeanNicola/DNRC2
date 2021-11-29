package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.RemarkDto;
import gov.mt.wris.dtos.RemarkTextDto;
import gov.mt.wris.dtos.RemarkUpdateDto;
import gov.mt.wris.dtos.RemarkVariableDto;
import gov.mt.wris.dtos.RemarkVariablesPageDto;
import gov.mt.wris.dtos.RemarkVariablesSortColumn;
import gov.mt.wris.dtos.RemarkVariableUpdateDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.RemarkVariableDto.VariableTypeEnum;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.RemarkElement;
import gov.mt.wris.models.Variable;
import gov.mt.wris.models.VersionRemark;
import gov.mt.wris.repositories.RemarkElementRepository;
import gov.mt.wris.repositories.VersionRemarkRepository;
import gov.mt.wris.services.RemarkService;

@Service
public class RemarkServiceImpl implements RemarkService {
    private static Logger LOGGER = LoggerFactory.getLogger(RemarkService.class);

    @Autowired
    RemarkElementRepository elementRepository;

    @Autowired
    VersionRemarkRepository versionRemarkRepository;

    public RemarkVariablesPageDto getRemarkVariables(Long remarkId,
        int pagenumber,
        int pagesize,
        RemarkVariablesSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a Page of Variables belonging to a Remark");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        Sort sort = getVariableSortColumn(sortColumn, sortDirection);

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort); 

        Page<RemarkElement> results = elementRepository.findFullElement(pageable, id);

        RemarkVariablesPageDto dto = new RemarkVariablesPageDto();

        dto.setResults(results.getContent().stream()
            .map(element -> getVersionElementDto(element))
            .collect(Collectors.toList()));
        
        dto.setCurrentPage(results.getNumber() + 1);
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private Sort getVariableSortColumn(RemarkVariablesSortColumn sortColumn, SortDirection sortDirection) {
        Sort sort;
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        switch(sortColumn) {
            case VARIABLENUMBERTYPE:
                sort = Sort.by(direction, "v.variableNumber").and(Sort.by(direction, "v.typeCode"));
                break;
            case PRECEDINGTEXT:
                sort = Sort.by(direction, "v.precedingText");
                break;
            case VARIABLETEXT:
                sort = Sort.by(direction, "value");
                break;
            case TRAILINGTEXT:
                sort = Sort.by(direction, "v.trailingText");
                break;
            default:
                sort = Sort.by(direction, "value");
        }
        return sort.and(Sort.by(Sort.Direction.ASC, "id"));
    }

    private RemarkVariableDto getVersionElementDto(RemarkElement element) {
        Variable variable = element.getVariable();
        return new RemarkVariableDto()
            .dataId(element.getId().longValue())
            .precedingText(variable.getPrecedingText())
            .variableText(element.getValue())
            .trailingText(variable.getTrailingText())
            .variableNumberType(variable.getVariableNumber().toString() + "-" + getVariableTypeAbbreviation(element.getVariable().getTypeCode()))
            .variableType(getVariableType(variable.getTypeCode()))
            .variableId(variable.getVariableId().longValue())
            .maxLength(variable.getLength() != null ? variable.getLength().longValue() : null);
    }

    private String getVariableTypeAbbreviation(String typeCode) {
        if("DATE".equals(typeCode)) {
            return "DT";
        } else if ("NUM".equals(typeCode)) {
            return "NM";
        } else {
            return "CH";
        }
    }

    private VariableTypeEnum getVariableType(String typeCode) {
        if ("DATE".equals(typeCode)) {
            return VariableTypeEnum.DATE;
        } else if ("NUM".equals(typeCode)) {
            return VariableTypeEnum.NUMERIC;
        } else {
            return VariableTypeEnum.STRING;
        }
    }

    public RemarkDto updateRemark(Long remarkId,
        RemarkUpdateDto dto
    ) {
        LOGGER.info("Updating Remark");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        Optional<VersionRemark> foundRemark = versionRemarkRepository.findById(id);
        if(!foundRemark.isPresent()) {
            throw new NotFoundException("This remark no longer exists");
        }
        VersionRemark remark = foundRemark.get();

        remark.setDate(dto.getAddedDate());

        versionRemarkRepository.save(remark);

        return new RemarkDto()
            .remarkId(remark.getId().longValue())
            .remarkCode(remark.getRemarkCode())
            .addedDate(remark.getDate());
    }

    @Transactional
    public void deleteRemark(Long remarkId) {
        LOGGER.info("Deleting a remark");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        elementRepository.deleteByRemarkId(id);

        versionRemarkRepository.deleteById(id);
    }

    public RemarkVariableDto editVariable(Long remarkId,
        Long variableId,
        RemarkVariableUpdateDto updateDto
    ) {
        LOGGER.info("Updating a Variable");

        BigDecimal varId = BigDecimal.valueOf(variableId);

        Optional<RemarkElement> foundElement = elementRepository.findFullElementById(varId);
        if(!foundElement.isPresent()) {
            throw new NotFoundException("This Variable doesn't exist");
        }
        RemarkElement element = foundElement.get();

        Variable variable = element.getVariable();
        if(variable.getTable() != null && variable.getColumn() != null) {
            if(!elementRepository.validateAllowableVariableText(variable.getTable(), variable.getColumn(), updateDto.getVariableText())) {
                throw new ValidationException(updateDto.getVariableText() + " was not found in column " + variable.getColumn() + " on table " + variable.getTable() + ". Pleas re-enter.");
            }
        }

        element.setValue(updateDto.getVariableText());

        element = elementRepository.save(element);

        return getVersionElementDto(element);
    }

    public RemarkTextDto getRemarkText(Long remarkId) {
        LOGGER.info("Getting the text of a remark");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        List<RemarkElement> elements = elementRepository.findAllElements(id);

        String text = elements.stream()
        .flatMap(element ->
            Stream.of(element.getVariable().getPrecedingText(),
                        element.getValue(),
                        element.getVariable().getTrailingText())
        ).filter(fragment -> fragment != null)
        .collect(Collectors.joining(" "))
        .replaceAll(" ([.:])", "$1")
        .replaceAll("\\.{2}", ".");

        RemarkTextDto dto = new RemarkTextDto()
            .remarkText(text);

        return dto;
    }
}
