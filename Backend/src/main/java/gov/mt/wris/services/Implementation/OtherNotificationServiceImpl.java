package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OtherNotificationDto;
import gov.mt.wris.dtos.OtherNotificationPageDto;
import gov.mt.wris.dtos.OtherNotificationSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Customer;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.OtherNotificationRepository;
import gov.mt.wris.services.OtherNotificationService;
import gov.mt.wris.utils.Helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OtherNotificationServiceImpl implements OtherNotificationService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private OtherNotificationRepository repo;

    private OtherNotificationDto getOtherNotificationDto(Customer model) {
        OtherNotificationDto newDto = new OtherNotificationDto();
        newDto.setContactId(model.getCustomerId().longValue());
        String name = Helpers.buildName(model.getLastName(), model.getFirstName(), model.getMiddleInitial());
        newDto.setName(name);
        return newDto;
    }

    private Sort getPartiesSortColumn(OtherNotificationSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection.getValue().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        LOGGER.info(sortDirection.getValue());
        List<Sort.Order> orders = new ArrayList<>();
        if (sortColumn == OtherNotificationSortColumn.CONTACTID) {
            orders.add(new Sort.Order(direction, "customerId"));
        } else {
            orders.add(new Sort.Order(direction, "lastName"));
            orders.add(new Sort.Order(direction, "firstName"));
            orders.add(new Sort.Order(direction, "middleInitial"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "customerId"));
        Sort fullSort = Sort.by(orders);
        return fullSort;
    }

    @Override
    public OtherNotificationPageDto findOtherNotificationsByAppIdAndByMailingJobId(
            String applicationId,
            String mailingJobId,
            Integer pageNumber,
            Integer pageSize,
            OtherNotificationSortColumn sortColumn,
            SortDirection sortDirection
    ) {
        LOGGER.info("Find the Other Parties Notified for a particular mailing job");
        LOGGER.info("Appl Id: " + applicationId);
        LOGGER.info("Mailing Id: " + mailingJobId);
        Sort sort = getPartiesSortColumn(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Customer> resultPage = repo.findOtherNotificationByAppId(pageable, new BigDecimal(applicationId), new BigDecimal(mailingJobId));
        OtherNotificationPageDto page = new OtherNotificationPageDto();

        page.setResults(resultPage.getContent().stream().map(model -> getOtherNotificationDto(model)).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;
    }
}
