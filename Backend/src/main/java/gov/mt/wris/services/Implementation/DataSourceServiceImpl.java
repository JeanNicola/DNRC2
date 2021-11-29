package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.AerialPhoto;
import gov.mt.wris.models.LegalLandDescription;
import gov.mt.wris.models.PlaceOfUse;
import gov.mt.wris.models.PlaceOfUseExaminationXref;
import gov.mt.wris.models.PouExamUsgsMapXref;
import gov.mt.wris.models.PouExamination;
import gov.mt.wris.models.TRS;
import gov.mt.wris.models.WaterResourceSurvey;
import gov.mt.wris.models.WaterSurveyPouExamXref;
import gov.mt.wris.repositories.AerialPhotoRepository;
import gov.mt.wris.repositories.PlaceOfUseExaminationXrefRepository;
import gov.mt.wris.repositories.PlaceOfUseRepository;
import gov.mt.wris.repositories.PouExamUsgsMapXrefRepository;
import gov.mt.wris.repositories.PouExaminationRepository;
import gov.mt.wris.repositories.WaterSurveyPouExamXrefRepository;
import gov.mt.wris.services.DataSourceService;
import gov.mt.wris.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    private static Logger LOGGER = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Autowired
    private PouExamUsgsMapXrefRepository pouExamUsgsMapXrefRepository;

    @Autowired
    private PouExaminationRepository pouExaminationRepository;

    @Autowired
    private AerialPhotoRepository aerialPhotoRepository;

    @Autowired
    private PlaceOfUseRepository placeOfUseRepository;

    @Autowired
    private WaterSurveyPouExamXrefRepository waterSurveyPouExamXrefRepository;

    @Autowired
    private PlaceOfUseExaminationXrefRepository placeOfUseExaminationXrefRepository;

    public DataSourceDto getDataSourceDetails(BigDecimal pexmId) {

        LOGGER.info("Getting Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        return ExaminationServiceImpl.getDataSourceDto(foundDataSource.get());
    }

    public DataSourceDto updateDataSource(BigDecimal pexmId, DataSourceCreationDto dataSourceCreationDto) {

        LOGGER.info("Updating Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        PouExamination oldDataSource = foundDataSource.get();

        oldDataSource.setInvestigationDate(dataSourceCreationDto.getInvestigationDate());

        return ExaminationServiceImpl.getDataSourceDto(pouExaminationRepository.saveAndFlush(oldDataSource));
    }


    private Sort getUsgsSort(UsgsSortColumn column, SortDirection direction) {

        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort =  Sort.by(sortOrderDirection, "usgs.name");

        return sort;

    }

    private Sort getAerialPhotoSort(AerialPhotoSortColumn column, SortDirection direction) {
        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;
        String mainSortColumn = "typeCode";

        switch (column) {
            case AERIALPHOTONUMBER:
                primary = Sort.by(sortOrderDirection, "number");
                secondary = Sort.by(Sort.Direction.ASC, mainSortColumn);
                sort = primary.and(secondary);
                break;
            case AERIALPHOTODATE:
                primary = Sort.by(sortOrderDirection, "date");
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

    private Sort getWaterResourceSurveySort(WaterResourceSurveySortColumn column, SortDirection direction) {
        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;
        String mainSortColumn = "waterResourceSurvey.county.name";

        switch (column) {
            case YEAR:
                primary = Sort.by(sortOrderDirection, "waterResourceSurvey.yr");
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

    private UsgsDto getUsgsDto(PouExamUsgsMapXref model) {

        UsgsDto dto = new UsgsDto();

        dto.setPexmId(model.getPexmId().longValue());
        dto.setUtmpId(model.getUtmpId().longValue());
        if (model.getUsgs() != null) dto.setName(model.getUsgs().getName());
        if (model.getPouExamination() !=  null) dto.setExaminationId(model.getPouExamination().getExaminationId().longValue());

        return dto;

    }

    private AerialPhotoDto getAerialPhotoDto(AerialPhoto model) {

        AerialPhotoDto dto = new AerialPhotoDto();

        dto.setPexmId(model.getPexmId().longValue());
        dto.setAerialId(model.getAerialId().longValue());
        dto.setTypeCode(model.getTypeCode());
        dto.setAerialPhotoNumber(model.getNumber());
        dto.setAerialPhotoDate(model.getDate());

        return dto;

    }

    private WaterResourceSurveyDto getWaterResourceSurveyDto(WaterSurveyPouExamXref model) {

        WaterResourceSurveyDto dto = new WaterResourceSurveyDto();

        dto.setPexmId(model.getPexmId().longValue());
        dto.setSurveyId(model.getSurveyId().longValue());

        if (model.getWaterResourceSurvey() != null) {
            dto.setYear(model.getWaterResourceSurvey().getYr());
            dto.setCountyId(model.getWaterResourceSurvey().getCountyId().longValue());
            dto.setCountyName(model.getWaterResourceSurvey().getCounty().getName());
        }

        return dto;

    }

    public UsgsPageDto getUsgsQuads(BigDecimal pexmId, Integer pageNumber, Integer pageSize, UsgsSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting Usgs for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));


        Pageable pageable;
        UsgsPageDto page = new UsgsPageDto();

        Page<PouExamUsgsMapXref> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, getUsgsSort(sortColumn, sortDirection));
        resultPage = pouExamUsgsMapXrefRepository.findByPexmId(pageable, pexmId);

        page.setResults(resultPage.getContent().stream().map(model -> getUsgsDto(model)).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    public UsgsDto createUsgsQuadMap(BigDecimal pexmId, UsgsCreationDto usgsCreationDto) {

        LOGGER.info("Creating Usgs Quad Map for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        PouExamUsgsMapXref model = new PouExamUsgsMapXref();
        model.setUtmpId(new BigDecimal(usgsCreationDto.getUtmpId()));
        model.setPexmId(pexmId);

        return getUsgsDto(pouExamUsgsMapXrefRepository.save(model));
    }


    public void deleteUsgsQuadMap(BigDecimal pexmId, BigDecimal utmpId) {

        LOGGER.info("Deleting Usgs Quad Map from Data Source: " + pexmId);

        pouExamUsgsMapXrefRepository.deleteByUtmpIdAndPexmId(utmpId, pexmId);

    }

    public AerialPhotoPageDto getAerialPhotos(BigDecimal pexmId, Integer pageNumber, Integer pageSize, AerialPhotoSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting Aerial Photos for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        Pageable pageable;
        AerialPhotoPageDto page = new AerialPhotoPageDto();

        Page<AerialPhoto> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, getAerialPhotoSort(sortColumn, sortDirection));
        resultPage = aerialPhotoRepository.findByPexmId(pageable, pexmId);

        page.setResults(resultPage.getContent().stream().map(model -> getAerialPhotoDto(model)).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    public AerialPhotoDto createAerialPhoto(BigDecimal pexmId, AerialPhotoCreationDto aerialPhotoCreationDto) {

        LOGGER.info("Creating Aerial Photo for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        AerialPhoto model = new AerialPhoto();
        model.setDate(aerialPhotoCreationDto.getAerialPhotoDate());
        model.setNumber(aerialPhotoCreationDto.getAerialPhotoNumber());
        model.setTypeCode(aerialPhotoCreationDto.getTypeCode());
        model.setPexmId(pexmId);

        return getAerialPhotoDto(aerialPhotoRepository.save(model));
    }


    public AerialPhotoDto updateAerialPhoto(BigDecimal pexmId, BigDecimal aerialId, AerialPhotoCreationDto aerialPhotoCreationDto) {

        LOGGER.info("Updating Aerial Photo for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        Optional<AerialPhoto> foundAerialPhoto = aerialPhotoRepository.findByAerialId(aerialId);
        if(!foundAerialPhoto.isPresent())
            throw new NotFoundException(String.format("Aerial id %s not found.", pexmId));;


        AerialPhoto oldAerialPhoto = foundAerialPhoto.get();
        oldAerialPhoto.setTypeCode(aerialPhotoCreationDto.getTypeCode());
        oldAerialPhoto.setNumber(aerialPhotoCreationDto.getAerialPhotoNumber());
        oldAerialPhoto.setDate(aerialPhotoCreationDto.getAerialPhotoDate());

        return getAerialPhotoDto(aerialPhotoRepository.saveAndFlush(oldAerialPhoto));
    }

    @Transactional
    public void deleteAerialPhoto(BigDecimal pexmId, BigDecimal aerialId) {

        LOGGER.info("Deleting Aerial Photo from Data Source: " + pexmId);

        Optional<AerialPhoto> foundAerialPhoto = aerialPhotoRepository.findByPexmIdAndAerialId(pexmId, aerialId);
        if(!foundAerialPhoto.isPresent())
            throw new NotFoundException(String.format("Aerial Photo id %s not found.", aerialId));

        AerialPhoto aerialPhoto = foundAerialPhoto.get();

        if (aerialPhoto.getPlacesOfUse().size() > 0) {
            placeOfUseExaminationXrefRepository.removeAerialFromPlaceOfUseExaminations(aerialPhoto.getPouExamination().getExamination().getPurposeId(), aerialPhoto.getAerialId());
        }

        aerialPhotoRepository.delete(aerialPhoto);

    }

    public WaterResourceSurveyPageDto getWaterSurveys(BigDecimal pexmId, Integer pageNumber, Integer pageSize, WaterResourceSurveySortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting Water Resource Surveys for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        Pageable pageable;
        WaterResourceSurveyPageDto page = new WaterResourceSurveyPageDto();

        Page<WaterSurveyPouExamXref> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, getWaterResourceSurveySort(sortColumn, sortDirection));
        resultPage = waterSurveyPouExamXrefRepository.findByPexmId(pageable, pexmId);

        page.setResults(resultPage.getContent().stream().map(model -> getWaterResourceSurveyDto(model)).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());


        return page;
    }

    public WaterResourceSurveyDto createWaterSourceSurvey(BigDecimal pexmId, WaterResourceSurveyCreationDto waterResourceSurveyCreationDto) {

        LOGGER.info("Creating Water Resource Survey for Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        WaterSurveyPouExamXref model = new WaterSurveyPouExamXref();

        model.setPexmId(pexmId);
        model.setSurveyId(new BigDecimal(waterResourceSurveyCreationDto.getSurveyId()));

        return getWaterResourceSurveyDto(waterSurveyPouExamXrefRepository.save(model));
    }

    @Transactional
    public void deleteWaterResourceSurvey(BigDecimal pexmId, BigDecimal surveyId) {

        LOGGER.info("Deleting Water Resource Survey from a Data Source: " + pexmId);

        Optional<WaterSurveyPouExamXref> foundWrsXref = waterSurveyPouExamXrefRepository.findByPexmIdAndSurveyId(pexmId, surveyId);
        if(!foundWrsXref.isPresent())
            throw new NotFoundException(String.format("Water Survey id %s not found.", surveyId));

        WaterSurveyPouExamXref wrsXref = foundWrsXref.get();

        if (wrsXref.getPlacesOfUse().size() > 0) {
            placeOfUseExaminationXrefRepository.removeSurveyFromPlaceOfUseExaminations(wrsXref.getPouExamination().getExamination().getPurposeId(), wrsXref.getSurveyId());
        }

        waterSurveyPouExamXrefRepository.delete(wrsXref);

    }

    public void populateParcels(BigDecimal pexmId) {

        LOGGER.info("Populating Places Of Use in Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findByIdAndFetchExaminationAndPurpose(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        BigDecimal purposeId = foundDataSource.get().getExamination().getPurposeId();
        String purposeTypeCode = foundDataSource.get().getExamination().getPurpose().getPurposeTypeCode();
        PouExamination dataSource = foundDataSource.get();

        if (!purposeTypeCode.equals("IR")) {
            throw new ValidationException("The purpose type is not \"IRRIGATION\" and parcel records cannot be populated");
        }

        List<PlaceOfUse> placesOfUse = placeOfUseRepository.findAllByPurposeId(purposeId);
        final BigDecimal aerialId;
        final BigDecimal surveyId;

        if (dataSource.getSourceType().equals("WRS") && dataSource.getWaterSurveyXrefs().size() == 1) {
            surveyId = dataSource.getWaterSurveyXrefs().get(0).getSurveyId();
        } else {
            surveyId = null;
        }

        if ((dataSource.getSourceType().equals("AER") || dataSource.getSourceType().equals("WAE")) && dataSource.getAerialPhotos().size() == 1) {
            aerialId = dataSource.getAerialPhotos().get(0).getAerialId();
        } else {
            aerialId  = null;
        }


        placesOfUse.forEach(pou -> {
            PlaceOfUseExaminationXref model = new PlaceOfUseExaminationXref();
            model.setPexmId(pexmId);
            model.setPurposeId(pou.getPurposeId());
            model.setPlaceId(pou.getPlaceId());
            model.setAerialId(aerialId);
            model.setSurveyId(surveyId);
            dataSource.getPlaceOfUseExaminations().add(model);
        });

        pouExaminationRepository.saveAndFlush(dataSource);

    }

    private Sort getParcelSort(ParcelSortColumn column, SortDirection direction) {

        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;
        String mainSortColumn = "placeId";

        switch (column) {
            case ACREAGE:
                primary = Sort.by(sortOrderDirection, "pou.acreage");
                secondary = Sort.by(Sort.Direction.ASC, mainSortColumn);
                sort = primary.and(secondary);
                break;
            case EXAMINEDACREAGE:
                primary = Sort.by(sortOrderDirection, "acreage");
                secondary = Sort.by(Sort.Direction.ASC, mainSortColumn);
                sort = primary.and(secondary);
                break;
            case COMPLETELEGALLANDDESCRIPTION:
                primary = Sort.by(sortOrderDirection, "lld.governmentLot");
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description320"));
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description160"));
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description80"));
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description40"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.section"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.township"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.townshipDirection"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.range"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.rangeDirection"));
                primary = primary.and(Sort.by(sortOrderDirection, "c.name"));
                primary = primary.and(Sort.by(sortOrderDirection, "c.stateCode"));
                secondary = Sort.by(Sort.Direction.ASC, mainSortColumn);
                sort = primary.and(secondary);
                break;
            default:
                sort = Sort.by(sortOrderDirection, mainSortColumn);
        }
        return sort;

    }

    private ParcelDto getParcelDto(PlaceOfUseExaminationXref model) {

        ParcelDto pouDto = new ParcelDto();
        pouDto.setAcreage(model.getPlaceOfUse().getAcreage());
        pouDto.setPlaceId(model.getPlaceId().longValue());
        if (model.getAerialId() != null) pouDto.setAerialId(model.getAerialId().longValue());
        if (model.getSurveyId() != null) pouDto.setSurveyId(model.getSurveyId().longValue());

        if (model.getPlaceOfUse() != null) {
            if (model.getPlaceOfUse().getLegalLandDescription() != null && model.getPlaceOfUse().getCounty() != null)
                pouDto.setCompleteLegalLandDescription(Helpers.buildLegalLandDescription(model.getPlaceOfUse().getLegalLandDescription(), model.getPlaceOfUse().getCounty()));
            if(model.getPlaceOfUse().getLegalLandDescription() != null) {
                LegalLandDescription land = model.getPlaceOfUse().getLegalLandDescription();
                pouDto.setDescription320(land.getDescription320());
                pouDto.setDescription160(land.getDescription160());
                pouDto.setDescription80(land.getDescription80());
                pouDto.setDescription40(land.getDescription40());
                if(land.getGovernmentLot() != null) pouDto.setGovernmentLot(land.getGovernmentLot().longValue());
                TRS trs = land.getTrs();
                if(trs.getTownship() != null) pouDto.setTownship(trs.getTownship().longValue());
                pouDto.setTownshipDirection(trs.getTownshipDirection());
                if(trs.getRange() != null) pouDto.setRange(trs.getRange().longValue());
                pouDto.setRangeDirection(trs.getRangeDirection());
                if(trs.getSection() != null) pouDto.setSection(trs.getSection().longValue());
            }
        }

        pouDto.setCountyId(model.getPlaceOfUse().getCountyId().longValue());
        pouDto.setLegalId(model.getPlaceOfUse().getLegalLandDescriptionId().longValue());
        pouDto.setExaminedAcreage(model.getAcreage());

        if (model.getPouExamination() != null) {
            if (model.getPouExamination().getSourceType().equals("FLD") && model.getPouExamination().getInvestigationDate() != null) {
                pouDto.setExamInfo(model.getPouExamination().getInvestigationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            }
            if ((model.getPouExamination().getSourceType().equals("AER") || model.getPouExamination().getSourceType().equals("WAE"))
                    && model.getAerialPhoto() != null) {
                AerialPhoto aerialPhoto = model.getPouExamination().getAerialPhotos().get(0);
                pouDto.setExamInfo(model.getAerialPhoto().getTypeCode() + " - " + model.getAerialPhoto().getNumber() + " - " + model.getAerialPhoto().getDate());
            }
            if (model.getPouExamination().getSourceType().equals("WRS") && model.getWaterSurveyPouExamXref() != null) {
                WaterResourceSurvey waterSurvey = model.getWaterSurveyPouExamXref().getWaterResourceSurvey();
                pouDto.setExamInfo(waterSurvey.getCounty().getName() + " - " + waterSurvey.getYr().toString());
            }
        }


        return pouDto;

    }

    public ParcelPageDto getParcels(BigDecimal pexmId, Integer pageNumber, Integer pageSize, ParcelSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting Parcels of Data Source: " + pexmId);

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        Pageable pageable;
        ParcelPageDto page = new ParcelPageDto();

        Page<PlaceOfUseExaminationXref> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, getParcelSort(sortColumn, sortDirection));
        resultPage = placeOfUseExaminationXrefRepository.findByPexmId(pageable, pexmId);

        page.setResults(resultPage.getContent().stream().map(model -> getParcelDto(model)).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    public ParcelDto updateParcel(BigDecimal pexmId, BigDecimal placeId, ParcelUpdateDto parcelUpdateDto) {

        LOGGER.info("Changing Parcel");

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        Optional<PlaceOfUseExaminationXref> foundPouExamXref = placeOfUseExaminationXrefRepository.findByPlaceIdAndPexmId(placeId, pexmId);
        if(!foundPouExamXref.isPresent())
            throw new NotFoundException(String.format("Place id %s not found.", placeId));

        PlaceOfUseExaminationXref oldPouExamXref = foundPouExamXref.get();

        oldPouExamXref.setAcreage(parcelUpdateDto.getExaminedAcreage());

        oldPouExamXref.setAerialId(null);
        oldPouExamXref.setSurveyId(null);
        if (parcelUpdateDto.getAerialId() != null) oldPouExamXref.setAerialId(new BigDecimal(parcelUpdateDto.getAerialId()));
        if (parcelUpdateDto.getSurveyId() != null) oldPouExamXref.setSurveyId(new BigDecimal(parcelUpdateDto.getSurveyId()));

        return getParcelDto(placeOfUseExaminationXrefRepository.saveAndFlush(oldPouExamXref));

    }

    public void deleteParcel(BigDecimal pexmId, BigDecimal placeId) {

        LOGGER.info("Deleting Parcel");

        placeOfUseExaminationXrefRepository.deleteByPlaceIdAndPexmId(placeId, pexmId);

    }

    public AllReferencesDto getExamInfoValues(BigDecimal pexmId) {

        LOGGER.info("Getting Exam Info Values of Data Source");

        Optional<PouExamination> foundDataSource = pouExaminationRepository.findById(pexmId);
        if(!foundDataSource.isPresent())
            throw new NotFoundException(String.format("Data Source id %s not found.", pexmId));

        AllReferencesDto allReferences = new AllReferencesDto();

        if (foundDataSource.get().getSourceType().equals("AER") || foundDataSource.get().getSourceType().equals("WAE")) {
            allReferences.setResults(foundDataSource.get().getAerialPhotos().stream().map(ap -> {
                String description = ap.getTypeCode() + " - " + ap.getNumber() + " - " + ap.getDate();
                ReferenceDto ref =  new ReferenceDto();
                ref.setDescription(description);
                ref.setValue(ap.getAerialId().toString());
                return ref;
            }).collect(Collectors.toList()));
        }

        if (foundDataSource.get().getSourceType().equals("WRS")) {
            List<WaterSurveyPouExamXref> waterResourceSurveys = waterSurveyPouExamXrefRepository.findByPexmId(pexmId);
            allReferences.setResults(waterResourceSurveys.stream().map(wrs -> {
                String description = wrs.getWaterResourceSurvey().getCounty().getName() + " - " + wrs.getWaterResourceSurvey().getYr().toString();
                ReferenceDto ref =  new ReferenceDto();
                ref.setDescription(description);
                ref.setValue(wrs.getSurveyId().toString());
                return ref;
            }).collect(Collectors.toList()));
        }

        return allReferences;
    }
}