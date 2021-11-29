package gov.mt.wris.services;

import gov.mt.wris.dtos.ElectronicContactsDto;
import gov.mt.wris.dtos.ElectronicContactsSearchPageDto;
import gov.mt.wris.dtos.ElectronicContactsSortColumn;
import gov.mt.wris.dtos.ElectronicContactsUpdateDto;
import gov.mt.wris.dtos.SortDirection;

public interface ElectronicContactsService {

    public ElectronicContactsSearchPageDto searchElectronicContacts(int pagenumber,
                                                                    int pagesize,
                                                                    ElectronicContactsSortColumn sortColumn,
                                                                    SortDirection sortDirection,
                                                                    Long customerId);

    public ElectronicContactsDto getElectronicContact(Long electronicId);

    public ElectronicContactsDto createElectronicContact(ElectronicContactsUpdateDto newContact);

    public ElectronicContactsDto changeElectronicContact(Long electronicId, ElectronicContactsUpdateDto contact);

    public void deleteElectronicContact(Long customerId, Long electronicId);
}