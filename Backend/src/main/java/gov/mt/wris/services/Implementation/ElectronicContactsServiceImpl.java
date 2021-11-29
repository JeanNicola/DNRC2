package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.ElectronicContactsDto;
import gov.mt.wris.dtos.ElectronicContactsSearchPageDto;
import gov.mt.wris.dtos.ElectronicContactsSortColumn;
import gov.mt.wris.dtos.ElectronicContactsUpdateDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.ElectronicContacts;
import gov.mt.wris.models.Reference;
import gov.mt.wris.repositories.ElectronicContactsRepository;
import gov.mt.wris.repositories.ReferenceRepository;
import gov.mt.wris.services.ElectronicContactsService;

@Service
public class ElectronicContactsServiceImpl implements ElectronicContactsService {
    private static Logger LOGGER = LoggerFactory.getLogger(ElectronicContactsServiceImpl.class);

    @Autowired
    ElectronicContactsRepository electronicContactsRepository;

    @Autowired
    ReferenceRepository referenceRepository;

    @Override
    public ElectronicContactsSearchPageDto searchElectronicContacts(int pagenumber,
                                                                    int pagesize,
                                                                    ElectronicContactsSortColumn sortColumn,
                                                                    SortDirection sortDirection,
                                                                    Long customerId) {

        LOGGER.info("Getting a Page of Electronic Contacts");

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize);
        Page<ElectronicContacts> resultsPage = electronicContactsRepository.searchElectronicContacts(
                pageable, sortColumn, sortDirection, customerId);
        ElectronicContactsSearchPageDto ccPage = new ElectronicContactsSearchPageDto();

        ccPage.setResults(resultsPage.getContent().stream().map(contact -> {
            return getElectronicContactsSearchResultDto(contact);
        }).collect(Collectors.toList()));

        ccPage.setCurrentPage(resultsPage.getNumber() + 1);
        ccPage.setPageSize(resultsPage.getSize());

        ccPage.setTotalPages(resultsPage.getTotalPages());
        ccPage.setTotalElements(resultsPage.getTotalElements());

        ccPage.setSortColumn(sortColumn);
        ccPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(customerId != null) {
            filters.put("customerId", customerId.toString());
        }

        ccPage.setFilters(filters);
        return ccPage;

    }

    @Override
    public ElectronicContactsDto getElectronicContact(Long electronicId) {

        LOGGER.info("Getting Electronic Contact");

        Optional<ElectronicContacts> foundContact = electronicContactsRepository.findById(BigDecimal.valueOf(electronicId));
        if (!foundContact.isPresent())
            throw new NotFoundException("Electronic Contact with id " + electronicId + " does not exist");

        return getElectronicContactsDto(foundContact);

    }

    @Override
    public ElectronicContactsDto createElectronicContact(ElectronicContactsUpdateDto newContact) {

        LOGGER.info("Creating Electronic Contact");

        ElectronicContacts ec = new ElectronicContacts();
        ec.setCustomerId(BigDecimal.valueOf(newContact.getCustomerId()));
        ec.setElectronicValue(newContact.getElectronicValue());
        ec.setElectronicType(newContact.getElectronicType());
        ec.setElectronicNotes(newContact.getElectronicNotes());

        ElectronicContacts saved = electronicContactsRepository.saveAndFlush(ec);
        Optional<ElectronicContacts> foundContact = electronicContactsRepository.findById(saved.getElectronicId());
        if(!foundContact.isPresent())
            throw new DataIntegrityViolationException("Electronic Contact with id " + saved.getElectronicId() + " after save was not found.");

        return getElectronicContactsDto(foundContact);

    }

    @Override
    public ElectronicContactsDto changeElectronicContact(Long electronicId, ElectronicContactsUpdateDto contact) {

        LOGGER.info("Changing Electronic Contact");

        Optional<ElectronicContacts> foundContact = electronicContactsRepository.findById(BigDecimal.valueOf(electronicId));
        if(!foundContact.isPresent())
            throw new DataIntegrityViolationException("Electronic Contact with id " + electronicId + " was not found.");

        // setup for save
        ElectronicContacts ec = new ElectronicContacts();
        ec.setElectronicId(BigDecimal.valueOf(electronicId));
        ec.setCustomerId(BigDecimal.valueOf(contact.getCustomerId()));
        ec.setElectronicValue(contact.getElectronicValue());
        ec.setElectronicType(contact.getElectronicType());
        ec.setElectronicNotes(contact.getElectronicNotes());
        return getElectronicContactsDto(electronicContactsRepository.saveAndFlush(ec));

    }

    @Override
    public void deleteElectronicContact(Long customerId, Long electronicId) {

        LOGGER.info("Deleting Electronic Contact");
        try {
            electronicContactsRepository.deleteById(BigDecimal.valueOf(electronicId));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(
                    String.format("Electronic Contact for customer contact %s with id %s not found", customerId, electronicId)
            );
        }

    }

    private ElectronicContactsDto getElectronicContactsDto(Optional<ElectronicContacts> contact) {
        return getElectronicContactsDto(contact.get());
    }

    private ElectronicContactsDto getElectronicContactsDto(ElectronicContacts contact) {

        ElectronicContactsDto dto = new ElectronicContactsDto();

        dto.setElectronicId(contact.getElectronicId().longValue());
        dto.setCustomerId(Long.valueOf(contact.getCustomerId().toString()));
        dto.setElectronicType(contact.getElectronicType());
        dto.setElectronicValue(contact.getElectronicValue());
        dto.setElectronicNotes(contact.getElectronicNotes());
        return dto;

    }

    private ElectronicContactsDto getElectronicContactsSearchResultDto(ElectronicContacts contact) {

        ElectronicContactsDto dto = new ElectronicContactsDto();

        if (contact.getElectronicType() != null) {
            Reference reference = referenceRepository.findByValue(contact.getElectronicType());
            dto.setElectronicTypeValue(reference.getMeaning());
        }

        if (contact!=null) {
            dto.setElectronicId(Long.valueOf(contact.getElectronicId().toString()));
            dto.setElectronicType(contact.getElectronicType());
            dto.setCustomerId(Long.valueOf(contact.getCustomerId().toString()));
            dto.setElectronicValue(contact.getElectronicValue());
            dto.setElectronicNotes(contact.getElectronicNotes());
        }
        return dto;

    }

}
