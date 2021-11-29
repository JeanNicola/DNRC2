package gov.mt.wris.services;

import gov.mt.wris.dtos.AllBasinsDto;
import gov.mt.wris.dtos.AllSubBasinsDto;

public interface BasinService {
    AllBasinsDto getBasins();

    AllSubBasinsDto getSubBasins();
}