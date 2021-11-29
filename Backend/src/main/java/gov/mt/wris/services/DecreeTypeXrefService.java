package gov.mt.wris.services;

import gov.mt.wris.dtos.TypeXrefDto;

public interface DecreeTypeXrefService {
    TypeXrefDto addDecreeType(String eventCode, TypeXrefDto typeDto);   

    void removeDecreeType(String eventCode, String code);

    TypeXrefDto toUpperCase(TypeXrefDto typeDto);
}
