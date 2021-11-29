package gov.mt.wris.services;

import gov.mt.wris.dtos.AllOfficesDto;
import gov.mt.wris.dtos.SortDirection;

public interface OfficeService {
    public AllOfficesDto getAllOffices();
    public AllOfficesDto getAllRegionalOffices();
}