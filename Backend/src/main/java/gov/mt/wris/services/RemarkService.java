package gov.mt.wris.services;

import gov.mt.wris.dtos.RemarkDto;
import gov.mt.wris.dtos.RemarkTextDto;
import gov.mt.wris.dtos.RemarkUpdateDto;
import gov.mt.wris.dtos.RemarkVariableDto;
import gov.mt.wris.dtos.RemarkVariablesPageDto;
import gov.mt.wris.dtos.RemarkVariablesSortColumn;
import gov.mt.wris.dtos.RemarkVariableUpdateDto;
import gov.mt.wris.dtos.SortDirection;

public interface RemarkService {
    public RemarkVariablesPageDto getRemarkVariables(Long remarkId,
        int pagenumber,
        int pagesize,
        RemarkVariablesSortColumn sortColumn,
        SortDirection sortDirection);

    public RemarkDto updateRemark(Long remarkId,
        RemarkUpdateDto dto);

    public void deleteRemark(Long remarkId);

    public RemarkVariableDto editVariable(Long remarkId,
        Long variableId,
        RemarkVariableUpdateDto updateDto);

    public RemarkTextDto getRemarkText(Long remarkId);
}
