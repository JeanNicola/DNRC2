package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.MailingJobCreationDto;
import gov.mt.wris.dtos.MailingJobDto;
import gov.mt.wris.dtos.MailingJobSortColumn;
import gov.mt.wris.dtos.MailingJobUpdateDto;
import gov.mt.wris.dtos.MailingJobsPageDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.MailingJob;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.MailingJobCustomerRepository;
import gov.mt.wris.repositories.MailingJobRepository;
import gov.mt.wris.repositories.MailingJobWaterRightRepository;
import gov.mt.wris.services.MailingJobService;

@Service
public class MailingJobServiceImpl implements MailingJobService {
    public static Logger LOGGER = LoggerFactory.getLogger(MailingJobService.class);

    @Autowired
    private MailingJobRepository mailingJobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MailingJobCustomerRepository customerXrefRepository;

    @Autowired
    private MailingJobWaterRightRepository waterRightXrefRepository;

    @Autowired
    private MailingJobWaterRightServiceImpl mailingJobWaterRightSWervice;

    public MailingJobsPageDto searchMailingJobs(int pagenumber,
        int pagesize,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId
    ) {
        LOGGER.info("Searching for Mailing Jobs");

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);
        

        Page<MailingJob> results = mailingJobRepository.searchMailingJobs(pageable, sortColumn, sortDirection, mailingJobNumber, mailingJobHeader, applicationId);

        MailingJobsPageDto page = new MailingJobsPageDto()
            .results(results.stream()
                .map(job -> getMailingJobDto(job))
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

    private MailingJobDto getMailingJobDto(MailingJob job) {
        MailingJobDto dto = new MailingJobDto()
            .applicationId(job.getApplicationId().longValue())
            .generatedDate(job.getDateGenerated())
            .mailingJobNumber(job.getId().longValue())
            .mailingJobHeader(job.getHeader());
        Application app = job.getApplication();
        if(app != null && app.getType() != null) {
            dto.setApplicationTypeDescription(app.getTypeCode() + " - " + app.getType().getDescription());
            BigDecimal officeId = app.getOfficeId();
            dto.setResponsibleOfficeId(officeId != null ? officeId.longValue() : null);
        }
        return dto;
    }

    @Transactional
    public MailingJobDto createMailingJob(MailingJobCreationDto creationDto) {
        LOGGER.info("Creating a new Mailing Job");

        BigDecimal appId = BigDecimal.valueOf(creationDto.getApplicationId());
        Optional<Application> foundApplication = applicationRepository.findById(appId);
        if(!foundApplication.isPresent()) {
            throw new NotFoundException("This Application does not exist");
        }
        Application app = foundApplication.get();

        MailingJob job = new MailingJob();
        job.setHeader(String.format("PN-%s-%d", app.getBasin(), app.getId().longValue()));
        job.setApplicationId(appId);

        job = mailingJobRepository.save(job);

        // Set default parties
        customerXrefRepository.addDefaultInterestedParties(job.getId());

        // Get initial water rights from application and add them to mailing job
        Set<WaterRightVersion> wrs = app.getWaterRightVersions();
        if(wrs != null) {
            BigDecimal mailJobId = job.getId();
            wrs.forEach((WaterRightVersion wr) -> {
                mailingJobWaterRightSWervice.addWaterRightToMailingJob(mailJobId, wr.getWaterRightId());
            });
        }

        return getMailingJobDto(job);
    }

    public MailingJobDto getMailingJob(Long mailingJobId) {
        LOGGER.info("Getting a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);

        Optional<MailingJob> foundMailingJob = mailingJobRepository.findWithApplication(id);
        if(!foundMailingJob.isPresent()) {
            throw new NotFoundException(String.format("Mailing Job with # %d does not exist.", mailingJobId));
        }
        MailingJob mailingJob = foundMailingJob.get();

        boolean partiesExist = mailingJobRepository.countInterestedParties(id) > 0;
        boolean waterRightsExist = mailingJobRepository.countWaterRights(id) > 0;

        MailingJobDto dto = getMailingJobDto(mailingJob);
        dto.setCanGenerateLabels(partiesExist || waterRightsExist);

        return dto;
    }

    public void generateMailingJob(Long mailingJobId) {
        LOGGER.info("Generating a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);

        Optional<MailingJob> foundMailingJob = mailingJobRepository.findWithApplication(id);
        if(!foundMailingJob.isPresent()) {
            throw new NotFoundException(String.format("Mailing Job with # %d does not exist.", mailingJobId));
        }
        MailingJob mailingJob = foundMailingJob.get();

        mailingJob.setDateGenerated(LocalDate.now());

        mailingJobRepository.save(mailingJob);
    }

    public MailingJobDto updateMailingJob(Long mailingJobId, MailingJobUpdateDto updateDto) {
        LOGGER.info("Updating a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);

        Optional<MailingJob> foundMailingJob = mailingJobRepository.findWithApplication(id);
        if(!foundMailingJob.isPresent()) {
            throw new NotFoundException(String.format("Mailing Job with # %d does not exist.", mailingJobId));
        }
        MailingJob mailingJob = foundMailingJob.get();

        mailingJob.setApplicationId(BigDecimal.valueOf(updateDto.getApplicationId()));
        mailingJob.setHeader(updateDto.getMailingJobHeader());

        mailingJob = mailingJobRepository.save(mailingJob);

        return getMailingJobDto(mailingJob);
    }

    @Transactional
    public void deleteMailingJob(Long mailingJobId) {
        LOGGER.info("Deleting a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);

        customerXrefRepository.deleteByMailingJobId(id);
        waterRightXrefRepository.deleteByMailingJobId(id);
        mailingJobRepository.deleteById(id);
    }
}
