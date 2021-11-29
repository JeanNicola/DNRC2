package gov.mt.wris.repositories;

import gov.mt.wris.dtos.AddressSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAddressRepository {

    Page<Address> searchAddresses(Pageable pageable, AddressSortColumn sortColumn, SortDirection sortDirection, Long customerId);

}
