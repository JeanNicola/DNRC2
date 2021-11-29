package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

public interface AddressService {

    public AddressSearchPageDto searchAddresses(int pageNumber,
                                                int pageSize,
                                                AddressSortColumn sortColumn,
                                                SortDirection sortDirection,
                                                Long customerId);

    public AddressDto getAddress(Long customerId, Long addressId);

    public AddressDto createAddress(Long customerId, AddressCreationDto newAddress);

    public AddressDto changeAddress(Long customerId, Long addressId, AddressUpdateDto updateAddress);

    public void deleteAddress(Long customerId, Long addressId);

}
