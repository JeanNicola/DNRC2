package gov.mt.wris.repositories;

import gov.mt.wris.dtos.ElectronicContactsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.ElectronicContacts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CustomElectronicContactsRepository {

    Page<ElectronicContacts> searchElectronicContacts(Pageable pageable, ElectronicContactsSortColumn sortColumn, SortDirection sortDirection, Long customerId);

}
