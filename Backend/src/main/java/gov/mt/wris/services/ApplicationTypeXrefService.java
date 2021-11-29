package gov.mt.wris.services;

import gov.mt.wris.dtos.TypeXrefDto;

public interface ApplicationTypeXrefService {
    TypeXrefDto addApplicationType(String eventCode, TypeXrefDto typeDto);   

    void removeApplicationType(String eventCode, String code);

    TypeXrefDto toUpperCase(TypeXrefDto typeDto);
}
