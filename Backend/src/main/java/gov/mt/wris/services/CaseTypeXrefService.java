package gov.mt.wris.services;

import gov.mt.wris.dtos.TypeXrefDto;

public interface CaseTypeXrefService {
    TypeXrefDto addCaseType(String eventCode, TypeXrefDto typeDto);   

    void removeCaseType(String eventCode, String code);

    TypeXrefDto toUpperCase(TypeXrefDto typeDto);
}
