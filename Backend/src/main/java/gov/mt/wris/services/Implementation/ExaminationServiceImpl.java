package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.DataSourceCreationDto;
import gov.mt.wris.dtos.DataSourceDto;
import gov.mt.wris.dtos.DataSourcePageDto;
import gov.mt.wris.dtos.DataSourceSortColumn;
import gov.mt.wris.dtos.DataSourceTypes;
import gov.mt.wris.dtos.ExaminationCreationDto;
import gov.mt.wris.dtos.ExaminationDetailDto;
import gov.mt.wris.dtos.ExaminationsDto;
import gov.mt.wris.dtos.ExaminationsSearchPageDto;
import gov.mt.wris.dtos.ExaminationsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Examination;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.models.PouExamination;
import gov.mt.wris.models.Purpose;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.ExaminationRepository;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.PouExaminationRepository;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.services.DataSourceService;
import gov.mt.wris.services.ExaminationService;
import gov.mt.wris.services.InquiryService;
import gov.mt.wris.utils.Helpers;

@Service
public class ExaminationServiceImpl implements ExaminationService {

    private static Logger LOGGER = LoggerFactory.getLogger(ExaminationServiceImpl.class);

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private PouExaminationRepository pouExaminationRepository;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private MasterStaffIndexesRepository generalStaffRepo;

    public ExaminationsSearchPageDto searchExaminations(Integer pageNumber, Integer pageSize, ExaminationsSortColumn sortColumn, SortDirection sortDirection, String basin, String waterRightNumber, String waterRightType, String versionType, String versionNumber) {

        LOGGER.info("Searching Examinations");

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ExaminationsSearchPageDto examinationsSearchPageDto = new ExaminationsSearchPageDto();

        Page<Examination> resultsPage = examinationRepository.searchExaminations(pageable, sortColumn, sortDirection, basin, waterRightNumber, waterRightType, versionType, versionNumber);

        examinationsSearchPageDto.setResults(resultsPage.getContent().stream().map(e -> {
            return examinationDtoLoader(e);
        }).collect(Collectors.toList()));

        examinationsSearchPageDto.setCurrentPage(resultsPage.getNumber() + 1);
        examinationsSearchPageDto.setPageSize(resultsPage.getSize());

        examinationsSearchPageDto.setTotalPages(resultsPage.getTotalPages());
        examinationsSearchPageDto.setTotalElements(resultsPage.getTotalElements());

        return examinationsSearchPageDto;
    }

    private ExaminationsDto examinationDtoLoader(Examination model) {

        ExaminationsDto dto = new ExaminationsDto();
        dto.setExaminationId(model.getExaminationId().longValue());
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setWaterRightId(model.getPurpose().getWaterRightId().longValue());
        dto.setVersionId(model.getPurpose().getVersionId().longValue());
        dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                        model.getPurpose().getWaterRightVersion().getWaterRight().getBasin(),
                        model.getPurpose().getWaterRightVersion().getWaterRight().getWaterRightNumber().toString(),
                        model.getPurpose().getWaterRightVersion().getWaterRight().getExt()
                )
        );
        if (model.getPurpose().getWaterRightVersion().getWaterRight() != null) {
            dto.setWaterRightStatusCode(model.getPurpose().getWaterRightVersion().getWaterRight().getWaterRightStatus().getCode());
            dto.setWaterRightStatusDescription(model.getPurpose().getWaterRightVersion().getWaterRight().getWaterRightStatus().getDescription());
            dto.setWaterRightTypeCode(model.getPurpose().getWaterRightVersion().getWaterRight().getWaterRightType().getCode());
            dto.setWaterRightTypeDescription(model.getPurpose().getWaterRightVersion().getWaterRight().getWaterRightType().getDescription());
        }
        dto.setCompleteWaterRightVersion(
                Helpers.buildCompleteWaterRightVersion(
                        model.getPurpose().getWaterRightVersion().getTypeReference().getMeaning(),
                        model.getPurpose().getVersionId().toString(),
                        model.getPurpose().getWaterRightVersion().getVersionStatus() != null ? model.getPurpose().getWaterRightVersion().getVersionStatus().getDescription() : ""
                )
        );
        return dto;
    }

    public ExaminationDetailDto getExamination(Long examinationId) {

        LOGGER.info("Getting Examination");

        Optional<Examination> foundExamination = examinationRepository.findById(BigDecimal.valueOf(examinationId));

        if(!foundExamination.isPresent())
            throw new NotFoundException(String.format("Examination id %s not found.", examinationId));

        Examination examination = foundExamination.get();

        ExaminationDetailDto dto = getExaminationDetailDto(examination);

        MasterStaffIndexes staff = examination.getStaff();
        if (staff != null) {
            String name = Helpers.buildFirstLastName(staff.getFirstName(), staff.getLastName());
            dto.setName(name);
        }

        Purpose purpose = examination.getPurpose();

        BigDecimal totalAcreageIr = examinationRepository.getPurposeTotalAcreageIr(purpose.getWaterRightId(), purpose.getVersionId());
        BigDecimal totalAcreage = examinationRepository.getPurposeTotalAcreage(purpose.getWaterRightId(), purpose.getVersionId());

        if (totalAcreageIr != null && totalAcreageIr.intValue() > 0) {

            Double cnt = 0.8 * Math.pow(totalAcreageIr.doubleValue(), 0.6);
            Double cntPos = totalAcreageIr.doubleValue() + cnt;
            Double cntNeg = totalAcreageIr.doubleValue() - cnt;

            dto.setCntPos(BigDecimal.valueOf(cntPos).setScale(2, RoundingMode.HALF_EVEN));
            dto.setCntNeg(BigDecimal.valueOf(cntNeg).setScale(2, RoundingMode.HALF_EVEN));
        }

        dto.setTotalClaimedAcres(totalAcreage);

        return dto;
    }

    private ExaminationDetailDto getExaminationDetailDto(Examination model) {

        ExaminationDetailDto dto = new ExaminationDetailDto();

        dto.setBeginDate(model.getBeginDate());
        dto.setEndDate(model.getEndDate());
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setExaminationId(model.getExaminationId().longValue());
        dto.setCanPrintDecreeReport(generalStaffRepo.hasRoles(Arrays.asList(Constants.PRINT_DECREE_REPORT)) > 0);

        if (model.getExaminer() != null) dto.setDnrcId(Long.valueOf(model.getExaminer()));
        if (model.getPurpose() != null) {
            String basin = model.getPurpose().getWaterRightVersion().getWaterRight().getBasin();
            String waterRightNumber = model.getPurpose().getWaterRightVersion().getWaterRight().getWaterRightNumber().toString();
            String ext = model.getPurpose().getWaterRightVersion().getWaterRight().getExt();

            dto.setCompleteWaterRightNumber(
                    Helpers.buildCompleteWaterRightNumber(basin, waterRightNumber, ext)
            );
        }

        if (model.getPurpose() != null && model.getPurpose().getWaterRightVersion().getWaterRight() != null) {

            WaterRight wr = model.getPurpose().getWaterRightVersion().getWaterRight();

            dto.setWaterRightId(wr.getWaterRightId().longValue());
            dto.setVersionTypeCode(model.getPurpose().getWaterRightVersion().getTypeCode());

            if (wr.getWaterRightStatus() != null) {
                dto.setWaterRightStatusCode(wr.getWaterRightStatus().getCode());
                dto.setWaterRightStatusDescription(wr.getWaterRightStatus().getDescription());
            }

            if (wr.getWaterRightType() != null) {
                dto.setWaterRightTypeCode(wr.getWaterRightType().getCode());
                dto.setWaterRightTypeDescription(wr.getWaterRightType().getDescription());
            }

            Map<String, Boolean> decreeFlags = inquiryService.isUneditable(wr.getWaterRightId(), model.getPurpose().getVersionId(), model.getPurpose().getWaterRightVersion().getTypeCode());

            dto.setIsDecreed(decreeFlags.get("isDecreed"));
            dto.setIsVersionLocked(decreeFlags.get("isVersionLocked"));
            dto.setIsEditableIfDecreed(decreeFlags.get("isEditableIfDecreed"));
            dto.setCanReexamineDecree(decreeFlags.get("canReexamineDecree"));
            dto.setCanModifySplitDecree(decreeFlags.get("canModifySplitDecree"));
        }

        if (model.getPurpose() != null)  dto.setVersionNumber(model.getPurpose().getWaterRightVersion().getVersion().longValue());

        return dto;

    }

    public ExaminationDetailDto createExamination(BigDecimal purposeId, ExaminationCreationDto examinationCreationDto) {

        LOGGER.info("Creating Examination");

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);

        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));

        Examination examination = new Examination();
        examination.setPurposeId(purposeId);
        examination.setExaminer(examinationCreationDto.getDnrcId().toString());
        examination.setBeginDate(examinationCreationDto.getBeginDate());
        examination.setEndDate(examinationCreationDto.getEndDate());

        examination = examinationRepository.saveAndFlush(examination);

        return getExaminationDetailDto(examination);
    }

    public ExaminationDetailDto updateExamination(BigDecimal examinationId, ExaminationCreationDto examinationCreationDtoDto) {

        LOGGER.info("Changing Examination");

        Optional<Examination> foundExamination = examinationRepository.findById(examinationId);
        if(!foundExamination.isPresent())
            throw new NotFoundException(String.format("Examination id %s not found.", examinationId));

        Examination oldExamination = foundExamination.get();

        oldExamination.setBeginDate(examinationCreationDtoDto.getBeginDate());
        oldExamination.setEndDate(examinationCreationDtoDto.getEndDate());
        if (examinationCreationDtoDto.getDnrcId() != null) oldExamination.setExaminer(examinationCreationDtoDto.getDnrcId().toString());

        oldExamination = examinationRepository.saveAndFlush(oldExamination);

        return getExaminationDetailDto(oldExamination);
    }

    private Sort getDataSourcesSort(DataSourceSortColumn column, SortDirection direction) {

        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;
        String mainSortColumn = "sourceTypeReference.meaning";

        switch (column) {
            case INVESTIGATIONDATE:
                primary = Sort.by(sortOrderDirection, "investigationDate");
                secondary = Sort.by(Sort.Direction.ASC, mainSortColumn);
                sort = primary.and(secondary);
                break;
            default:
                primary = Sort.by(sortOrderDirection, mainSortColumn);
                sort = primary;
                break;
        }

        return sort;

    }

    static DataSourceDto getDataSourceDto(PouExamination model) {

        DataSourceDto dto = new DataSourceDto();

        dto.setExaminationId(model.getExaminationId().longValue());
        dto.setInvestigationDate(model.getInvestigationDate());
        dto.setSourceType(model.getSourceType());
        dto.setPexmId(model.getPexmId().longValue());
        if (model.getExamination() != null) dto.setPurposeId(model.getExamination().getPurposeId().longValue());
        if (model.getSourceTypeReference() != null) {
            dto.setSourceTypeDescription(model.getSourceTypeReference().getMeaning());
        }

        return dto;

    }

    public DataSourcePageDto getExaminationDataSources(BigDecimal examinationId, Integer pageNumber, Integer pageSize, DataSourceSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting Data Sources for Examination: " + examinationId);

        Optional<Examination> foundExamination = examinationRepository.findById(examinationId);
        if(!foundExamination.isPresent())
            throw new NotFoundException(String.format("Examination id %s not found.", examinationId));

        Pageable pageable;

        DataSourcePageDto page = new DataSourcePageDto();

        Page<PouExamination> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, getDataSourcesSort(sortColumn, sortDirection));
        resultPage = pouExaminationRepository.findByExaminationId(pageable, examinationId);

        page.setResults(resultPage.getContent().stream().map(model -> {
            DataSourceDto dto = getDataSourceDto(model);
            dto.setTotalExaminedAcres(model.getDataSourceTotalAcres().getTotalAcres());
            return dto;
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    @Transactional
    public DataSourceDto createDataSourceForExamination(BigDecimal examinationId, DataSourceCreationDto dataSourceCreationDto) {

        LOGGER.info("Creating Data Source for Examination: " + examinationId);

        Optional<Examination> foundExamination = examinationRepository.findById(examinationId);
        if(!foundExamination.isPresent())
            throw new NotFoundException(String.format("Examination id %s not found.", examinationId));


        foundExamination.get().getPouExaminations().forEach(pou -> {
            if (pou.getSourceType().equals(dataSourceCreationDto.getSourceType()) && !pou.getSourceType().equals(DataSourceTypes.FLD.toString())) {
                throw new ValidationException("This Data Source cannot be duplicated");
            }
        });

        PouExamination model = new PouExamination();

        model.setExaminationId(examinationId);
        model.setInvestigationDate(dataSourceCreationDto.getInvestigationDate());
        model.setSourceType(dataSourceCreationDto.getSourceType());

        DataSourceDto result = getDataSourceDto(pouExaminationRepository.save(model));

        if (dataSourceCreationDto.getUsgs() != null) {
            dataSourceService.createUsgsQuadMap(new BigDecimal(result.getPexmId()), dataSourceCreationDto.getUsgs());
        }

        if (dataSourceCreationDto.getAerialPhoto() != null) {
            dataSourceService.createAerialPhoto(new BigDecimal(result.getPexmId()), dataSourceCreationDto.getAerialPhoto());
        }

        if (dataSourceCreationDto.getWaterResourceSurvey() != null) {
            dataSourceService.createWaterSourceSurvey(new BigDecimal(result.getPexmId()), dataSourceCreationDto.getWaterResourceSurvey());
        }

        return result;
    }

    public void deleteDataSource(BigDecimal examinationId, BigDecimal pexmId) {

        LOGGER.info("Deleting Data Source: " + pexmId + " of Examination: " + examinationId);

        pouExaminationRepository.deleteById(pexmId);

    }

}
