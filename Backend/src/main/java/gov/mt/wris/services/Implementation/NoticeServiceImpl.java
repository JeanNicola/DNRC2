package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.ApplicationMailingJobDto;
import gov.mt.wris.dtos.ApplicationMailingJobPageDto;
import gov.mt.wris.dtos.ApplicationMailingJobSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.MailingJob;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.NoticeRepository;
import gov.mt.wris.services.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private NoticeRepository repo;

    @Autowired
    private ApplicationRepository appRepo;


    /**
     * @param model
     * @return
     */
    private ApplicationMailingJobDto getApplicationMailingJobDto(MailingJob model) {
        ApplicationMailingJobDto newDto = new ApplicationMailingJobDto();
        newDto.setMailingJobId(model.getId().longValue());
        newDto.setDateGenerated(model.getDateGenerated());
        return newDto;
    }

    @Override
    public ApplicationMailingJobPageDto findNotices(
            String applicationId,
            Integer pageNumber,
            Integer pageSize,
            ApplicationMailingJobSortColumn sortColumn,
            DescSortDirection sortDirection
    ) {
        Sort.Direction sortOrderDirection =  sortDirection.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(
                sortOrderDirection, getEntitySortColumn(sortColumn)).
                and(Sort.by(Sort.Direction.DESC, "id"))
        );
        Page<MailingJob> resultPage = repo.findAllByApplicationId(pageable, new BigDecimal(applicationId));
        ApplicationMailingJobPageDto page = new ApplicationMailingJobPageDto();

        page.setResults(resultPage.getContent().stream().map(model -> {
            return getApplicationMailingJobDto(model);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;
    }

    private String getEntitySortColumn(ApplicationMailingJobSortColumn sortColumn) {
        if (sortColumn == ApplicationMailingJobSortColumn.MAILINGJOBID)
            return "id";
        if (sortColumn == ApplicationMailingJobSortColumn.DATEGENERATED)
            return "dateGenerated";
        return "id";
    }
}
