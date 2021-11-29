package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.JobPartiesDto;
import gov.mt.wris.dtos.JobPartiesPageDto;
import gov.mt.wris.dtos.JobPartiesSortColumn;
import gov.mt.wris.dtos.JobPartyByOfficeCreationDto;
import gov.mt.wris.dtos.JobPartyCreationDto;
import gov.mt.wris.dtos.OfficeContactDto;
import gov.mt.wris.dtos.OfficeContactPageDto;
import gov.mt.wris.dtos.OfficeContactSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.MailingJobCustomer;
import gov.mt.wris.models.OfficeCustomer;
import gov.mt.wris.repositories.MailingJobCustomerRepository;
import gov.mt.wris.services.MailingJobPartyService;
import gov.mt.wris.utils.Helpers;

@Service
public class MailingJobPartyServiceImpl implements MailingJobPartyService {
    private static Logger LOGGER = LoggerFactory.getLogger(MailingJobPartyService.class);

    @Autowired
    private MailingJobCustomerRepository xrefRepository;

    public JobPartiesPageDto getMailingJobParties(Long mailingJobId,
        int pagenumber,
        int pagesize,
        JobPartiesSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Interested Parties of a Mailing Job");

        Sort sort = getPartySort(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal id = BigDecimal.valueOf(mailingJobId);

        Page<MailingJobCustomer> results = xrefRepository.findByMailingJobId(pageable, id);

        JobPartiesPageDto page = new JobPartiesPageDto()
            .results(
                results.getContent().stream()
                .map(party -> getJobPartyDto(party))
                .collect(Collectors.toList())
            )
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalElements(results.getTotalElements())
            .totalPages(results.getTotalPages())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
        
        return page;
    }

    private static JobPartiesDto getJobPartyDto(MailingJobCustomer party) {
        Customer customer = party.getCustomer();
        return new JobPartiesDto()
            .contactId(customer.getCustomerId().longValue())
            .firstLastName(Helpers.buildFirstLastName(
                customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getLastName(),
                customer.getSuffix()
            ))
            .contactTypeDescription(customer.getContactTypeValue().getDescription());
    }

    private static Sort getPartySort(JobPartiesSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        switch(sortColumn) {
            case FIRSTLASTNAME:
                sort = Sort.by(direction, "c.firstName")
                    .and(Sort.by(direction, "c.middleInitial"))
                    .and(Sort.by(direction, "c.lastName"))
                    .and(Sort.by(direction, "c.suffix"));
                break;
            case CONTACTTYPEDESCRIPTION:
                sort = Sort.by(direction, "t.description");
                break;
            default:
                sort = Sort.by(direction, "contactId");
                break;
        }
        sort = sort.and(Sort.by(direction, "contactId"));

        return sort;
    }

    public void addInterestedParty(Long mailingJobId, JobPartyCreationDto creationDto) {
        LOGGER.info("Adding an Interested Party to a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);
        BigDecimal contactId = BigDecimal.valueOf(creationDto.getContactId());

        Optional<MailingJobCustomer> foundCustomer = xrefRepository.findByMailingJobIdAndContactId(id, contactId);
        if(foundCustomer.isPresent()) {
            return; // it already exists, so don't throw an error
        }

        MailingJobCustomer xref = new MailingJobCustomer();
        xref.setMailingJobId(id);
        xref.setContactId(contactId);

        try {
            xrefRepository.save(xref);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("MLJC_MLJB_FK")) {
                    throw new NotFoundException(String.format("This Mailing Job does not exist: %s", mailingJobId));
                } else if(constraintMessage.contains("MLJC_CUST_FK")) {
                    throw new NotFoundException("This Contact does not exist");
                }
            }
            throw e;
        }
    }

    public void addInterestedPartyByOffice(Long mailingJobId, Long officeId, JobPartyByOfficeCreationDto creationDto) {
        LOGGER.info("Add Interested Parties by Office");

        BigDecimal mailId = BigDecimal.valueOf(mailingJobId);
        BigDecimal offId = BigDecimal.valueOf(officeId);

        List<BigDecimal> ids = creationDto.getContactIds().stream().filter(id -> id != null).map(id -> BigDecimal.valueOf(id)).collect(Collectors.toList());

        // otherwise, the query would be not in (null), which always evaluates to false, since null is neither equal, nor not equal to anything
        if(ids.size() == 0) {
            ids = Arrays.asList(BigDecimal.ZERO);
        }
        // default to all include except the ids passed up
        if(creationDto.getIncludeAll()) {
            xrefRepository.addPartiesByOfficeInclusive(mailId, offId, ids);
        } else {
            xrefRepository.addPartiesByOfficeExclusive(mailId, offId, ids);
        }
    }

    public void removeInterestedParty(Long mailingJobId, Long contactId) {
        LOGGER.info("Removing an Interested Party from a Mailing Job");

        BigDecimal mailId = BigDecimal.valueOf(mailingJobId);
        BigDecimal custId = BigDecimal.valueOf(contactId);

        xrefRepository.deleteByMailingJobIdAndContactId(mailId, custId);
    }

    public OfficeContactPageDto getOfficeContacts(Long mailingJobId,
        Long officeId,
        int pagenumber,
        int pagesize,
        OfficeContactSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting Contacts of an Office");

        Sort sort = getContactSort(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal mailId = BigDecimal.valueOf(mailingJobId);
        BigDecimal id = BigDecimal.valueOf(officeId);

        Page<OfficeCustomer> results = xrefRepository.findByOfficeId(pageable, mailId, id);

        OfficeContactPageDto page = new OfficeContactPageDto()
            .results(
                results.getContent().stream()
                .map(party -> getContactDto(party))
                .collect(Collectors.toList())
            )
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalElements(results.getTotalElements())
            .totalPages(results.getTotalPages())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
        
        return page;
    }

    private static OfficeContactDto getContactDto(OfficeCustomer model) {
        Customer customer = model.getCustomer();
        return new OfficeContactDto()
            .contactId(customer.getCustomerId().longValue())
            .firstLastName(Helpers.buildFirstLastName(
                customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getLastName(),
                customer.getSuffix()
            ))
            .contactTypeDescription(customer.getContactTypeValue().getDescription());
    }

    private static Sort getContactSort(OfficeContactSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        switch(sortColumn) {
            case FIRSTLASTNAME:
                sort = Sort.by(direction, "c.firstName")
                    .and(Sort.by(direction, "c.middleInitial"))
                    .and(Sort.by(direction, "c.lastName"))
                    .and(Sort.by(direction, "c.suffix"));
                break;
            case CONTACTTYPEDESCRIPTION:
                sort = Sort.by(direction, "t.description");
                break;
            default:
                sort = Sort.by(direction, "contactId");
                break;
        }
        sort = sort.and(Sort.by(direction, "contactId"));

        return sort;
    }
}
