package gov.mt.wris.services;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.CaseAllReferencesDto;

public interface ReferenceService {
    AllReferencesDto findAllProgramsByTable(String domain);
    AllReferencesDto findAllProgramsByTableOrderByMeaning(String domain);
    AllReferencesDto findAllProgramsByTableOrderByLowValue(String domain);
    AllReferencesDto findAllProgramsByTableOrderByCode(String domain);
    AllReferencesDto findDistinctContactType();
    AllReferencesDto findElectronicContactTypes(String domain);
    AllReferencesDto findElementType();
    AllReferencesDto getReportUrl(String env);
    AllReferencesDto findAllClimaticAreas();
    AllReferencesDto findAllPurposeTypes();
    AllReferencesDto findAllIrrigationTypes();
    AllReferencesDto findAllMaxVolumeDescriptions();
    CaseAllReferencesDto findCaseTypes(Integer supported);
    AllReferencesDto findObjectionTypes(Integer supported);
}
