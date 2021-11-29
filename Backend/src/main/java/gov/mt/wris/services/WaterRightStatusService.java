package gov.mt.wris.services;

import gov.mt.wris.dtos.AllWaterRightStatusesDto;

public interface WaterRightStatusService {
    public AllWaterRightStatusesDto getWaterRightStatuses(String waterRightTypeCode);
    
}
