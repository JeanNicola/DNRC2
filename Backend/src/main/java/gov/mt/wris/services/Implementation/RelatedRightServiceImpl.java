package gov.mt.wris.services.Implementation;

import static gov.mt.wris.constants.Constants.RELATED_RIGHT_TYPE_MULTIPLE;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
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

import gov.mt.wris.dtos.RelatedRightCreationDto;
import gov.mt.wris.dtos.RelatedRightCreationResultDto;
import gov.mt.wris.dtos.RelatedRightDto;
import gov.mt.wris.dtos.RelatedRightElementCreationDto;
import gov.mt.wris.dtos.RelatedRightElementsPageDto;
import gov.mt.wris.dtos.RelatedRightElementsSearchResultDto;
import gov.mt.wris.dtos.RelatedRightElementsSortColumn;
import gov.mt.wris.dtos.RelatedRightSortColumn;
import gov.mt.wris.dtos.RelatedRightWaterRightDto;
import gov.mt.wris.dtos.RelatedRightWaterRightPageDto;
import gov.mt.wris.dtos.RelatedRightWaterRightSortColumn;
import gov.mt.wris.dtos.RelatedRightsPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.UpdateRelatedRightDto;
import gov.mt.wris.dtos.WaterRightReferenceToRelatedRightCreationDto;
import gov.mt.wris.dtos.WaterRightReferenceToRelatedRightSearchResultDto;
import gov.mt.wris.dtos.WaterRightVersionsForRelatedRightSortColumn;
import gov.mt.wris.dtos.WaterRightsReferenceDto;
import gov.mt.wris.dtos.WaterRightsVersionsPageDto;
import gov.mt.wris.dtos.WaterRightsVersionsSearchResultDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.RelatedRight;
import gov.mt.wris.models.RelatedRightVerXref;
import gov.mt.wris.models.SharedElement;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.RelatedRightRepository;
import gov.mt.wris.repositories.RelatedRightVerXrefRepository;
import gov.mt.wris.repositories.SharedElementRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.RelatedRightService;
import gov.mt.wris.utils.Helpers;

@Service
public class RelatedRightServiceImpl implements RelatedRightService {

    private static Logger LOGGER = LoggerFactory.getLogger(RelatedRightServiceImpl.class);

    @Autowired
    private RelatedRightRepository relatedRightRepository;

    @Autowired
    private SharedElementRepository sharedElementRepository;

    @Autowired
    private WaterRightVersionRepository waterRightVersionRepository;

    @Autowired
    private RelatedRightVerXrefRepository relatedRightXrefRepository;

    @Override
    public RelatedRightsPageDto searchRelatedRights(int pageNumber, int pageSize, RelatedRightSortColumn sortColumn, SortDirection sortDirection, String relatedRightId, String relationshipType, String waterRightNumber, String basin, String ext) {

        LOGGER.info("Getting a Page of Related Rights");

        if (ext != null && waterRightNumber == null) {
            throw new ValidationException("Extension must have a Water Right Number");
        }

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<Object[]> resultsPage = relatedRightRepository.searchRelatedRights(pageable, sortColumn, sortDirection, relatedRightId, relationshipType, waterRightNumber, basin, ext);

        RelatedRightsPageDto page = new RelatedRightsPageDto();

        page.setResults(resultsPage.getContent().stream().map(object -> {
            return getRelatedRightDto((RelatedRight) object[0], (Long) object[1]);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(relatedRightId != null) {
            filters.put("relatedRightId", relatedRightId);
        }
        if(relationshipType != null) {
            filters.put("relationshipType", relationshipType);
        }

        page.setFilters(filters);

        return page;
    }


    private RelatedRightDto getRelatedRightDto(RelatedRight relatedRight, Long waterRightsCount) {
        RelatedRightDto dto = new RelatedRightDto();
        dto.setRelatedRightId(relatedRight.getRelatedRightId().longValue());
        dto.setRelationshipType(relatedRight.getRelationshipType());
        dto.setRelationshipTypeVal(relatedRight.getRelationshipTypeVal().getMeaning());
        dto.setWaterRightsCount(waterRightsCount);

        return dto;
    }

    @Override
    public RelatedRightDto getRelatedRightDetails(Long relatedRightId) {

        LOGGER.info("Getting details for one Related Right");

        Optional<RelatedRight> foundRelatedRight = relatedRightRepository.findById(BigDecimal.valueOf(relatedRightId));

        if(!foundRelatedRight.isPresent())
            throw new NotFoundException(String.format("Related Right id %s not found.", relatedRightId));

        boolean hasRelatedElements = sharedElementRepository.existsSharedElementByRelatedRightId(BigDecimal.valueOf(relatedRightId));

        RelatedRight relatedRight = foundRelatedRight.get();

        RelatedRightDto dto = getRelatedRightDetailsDto(relatedRight);

        dto.setHasRelatedElements(hasRelatedElements);

        return dto;

    }

    private RelatedRightDto getRelatedRightDetailsDto(RelatedRight relatedRight) {
        RelatedRightDto dto = new RelatedRightDto();
        dto.setRelatedRightId(relatedRight.getRelatedRightId().longValue());
        dto.setRelationshipType(relatedRight.getRelationshipType());
        if (relatedRight.getRelationshipTypeVal() != null) dto.setRelationshipTypeVal(relatedRight.getRelationshipTypeVal().getMeaning());
        if (relatedRight.getFlowRateUnitVal() != null) dto.setFlowRateUnitVal(relatedRight.getFlowRateUnitVal().getMeaning());
        if (relatedRight.getMaxFlowRate() != null) dto.setMaxFlowRate(relatedRight.getMaxFlowRate().longValue());
        if (relatedRight.getFlowRateUnit() != null) dto.setFlowRateUnit(relatedRight.getFlowRateUnit());
        if (relatedRight.getMaxAcres() != null) dto.setMaxAcres(relatedRight.getMaxAcres().doubleValue());
        if (relatedRight.getMaxVolume() != null) dto.setMaxVolume(relatedRight.getMaxVolume().doubleValue());
        return dto;
    }

    @Override
    public RelatedRightWaterRightPageDto getRelatedRightWaterRights(Integer pageNumber, Integer pageSize, RelatedRightWaterRightSortColumn sortColumn, SortDirection sortDirection, Long relatedRightId, String returnVersions) {

        LOGGER.info("Getting Water Rights for one Related Right");

        Pageable pageable;

        RelatedRightWaterRightPageDto page = new RelatedRightWaterRightPageDto();

        if ("Y".equals(returnVersions)) {
            Page<RelatedRightVerXref> resultPage;
            pageable = PageRequest.of(pageNumber -1, pageSize, getRelatedRightVerXrefSort(sortColumn, sortDirection));
            resultPage = relatedRightRepository.getRelatedRightWaterRightsWithVersions(pageable, BigDecimal.valueOf(relatedRightId));

            page.setResults(resultPage.getContent().stream().map(model -> {
                return getWaterRightDtoFromRelatedRightVerXref(model);
            }).collect(Collectors.toList()));

            page.setCurrentPage(resultPage.getNumber() + 1);
            page.setPageSize(resultPage.getSize());
            page.setSortDirection(sortDirection);
            page.setSortColumn(sortColumn);
            page.setTotalElements(resultPage.getTotalElements());
            page.setTotalPages(resultPage.getTotalPages());

        } else {
            Page<WaterRight> resultPage;
            pageable = PageRequest.of(pageNumber -1, pageSize, getRelatedRightWaterRightSort(sortColumn, sortDirection));
            resultPage = relatedRightRepository.getRelatedRightWaterRights(pageable, BigDecimal.valueOf(relatedRightId));

            page.setResults(resultPage.getContent().stream().map(model -> {
                return getWaterRightDto(model);
            }).collect(Collectors.toList()));

            page.setCurrentPage(resultPage.getNumber() + 1);
            page.setPageSize(resultPage.getSize());
            page.setSortDirection(sortDirection);
            page.setSortColumn(sortColumn);
            page.setTotalElements(resultPage.getTotalElements());
            page.setTotalPages(resultPage.getTotalPages());
        }


        return page;
    }

    private RelatedRightWaterRightDto getWaterRightDto(WaterRight model) {
        RelatedRightWaterRightDto newDto = new RelatedRightWaterRightDto();

        newDto.setWaterRightId(model.getWaterRightId().longValue());
        newDto.setBasin(model.getBasin());
        newDto.setExt(model.getExt());
        newDto.setStatus(model.getWaterRightStatus() != null ? model.getWaterRightStatus().getDescription() : null);
        newDto.setTypeDescription(model.getWaterRightType() != null ? model.getWaterRightType().getDescription() : null);
        newDto.setWaterRightNumber(model.getWaterRightNumber() != null ? model.getWaterRightNumber().longValue() : null);
        newDto.setCompleteWaterRightNumber(Helpers.buildCompleteWaterRightNumber(model.getBasin(), model.getWaterRightNumber().toString(), model.getExt()));
        return newDto;
    }


    private RelatedRightWaterRightDto getWaterRightDtoFromRelatedRightVerXref(RelatedRightVerXref model) {
        RelatedRightWaterRightDto newDto = new RelatedRightWaterRightDto();

        newDto.setWaterRightId(model.getWaterRightId().longValue());
        newDto.setBasin(model.getWaterRight().getBasin());
        newDto.setExt(model.getWaterRight().getExt());
        newDto.setStatus(model.getWaterRight().getWaterRightStatus() != null ? model.getWaterRight().getWaterRightStatus().getDescription() : null);
        newDto.setTypeDescription(model.getWaterRight().getWaterRightType() != null ? model.getWaterRight().getWaterRightType().getDescription() : null);
        newDto.setWaterRightNumber(model.getWaterRight().getWaterRightNumber() != null ? model.getWaterRight().getWaterRightNumber().longValue() : null);
        newDto.setCompleteWaterRightNumber(Helpers.buildCompleteWaterRightNumber(model.getWaterRight().getBasin(), model.getWaterRight().getWaterRightNumber().toString(), model.getWaterRight().getExt()));
        if (model.getVersionId() != null) newDto.setVersionId(model.getVersionId().longValue());
        if (model.getVersion() != null && model.getVersion().getTypeCode() != null) {
            newDto.setVersionType(model.getVersion().getTypeCode());
            newDto.setVersion(
                    String.format("%s %s", model.getVersion().getTypeReference().getMeaning(), model.getVersionId().toString())
            );
        }
        return newDto;
    }

    private Sort getRelatedRightWaterRightSort(RelatedRightWaterRightSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary, tertiary;

        switch (column) {
            case BASIN:
            case TYPEDESCRIPTION:
            case EXT:
            case STATUS:
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getWaterRightSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                sortGroup = primary.and(secondary);
                break;
            case COMPLETEWATERRIGHTNUMBER:
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(RelatedRightWaterRightSortColumn.BASIN));
                secondary = Sort.by(sortOrderDirection, getWaterRightSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                tertiary = Sort.by(sortOrderDirection, getWaterRightSortColumn(RelatedRightWaterRightSortColumn.EXT));
                sortGroup = primary.and(secondary).and(tertiary);
                break;
            default:
                // WATERRIGHTNUMBER
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                sortGroup = primary;
        }

        return sortGroup;

    }

    private Sort getRelatedRightVerXrefSort(RelatedRightWaterRightSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary, tertiary, quaternary, quinary;

        switch (column) {
            case BASIN:
            case TYPEDESCRIPTION:
            case EXT:
            case STATUS:
                primary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(column));

                secondary = Sort.by(Sort.Direction.ASC, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.BASIN));
                tertiary = Sort.by(Sort.Direction.ASC, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                quaternary = Sort.by(Sort.Direction.ASC, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.EXT));
                sortGroup = primary.and(secondary).and(tertiary).and(quaternary);
                break;
            case COMPLETEWATERRIGHTNUMBER:
                primary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.BASIN));
                secondary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                tertiary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.EXT));
                sortGroup = primary.and(secondary).and(tertiary);
                break;
            case VERSION:
                primary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.VERSIONTYPE));
                secondary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.VERSIONID));

                tertiary = Sort.by(Sort.Direction.ASC, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.BASIN));
                quaternary = Sort.by(Sort.Direction.ASC, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                quinary = Sort.by(Sort.Direction.ASC, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.EXT));

                sortGroup = primary.and(secondary).and(tertiary).and(quaternary).and(quinary);
                break;
            default:
                // WATERRIGHTNUMBER
                primary = Sort.by(sortOrderDirection, getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER));
                sortGroup = primary;
        }

        return sortGroup;

    }

    private String getWaterRightSortColumn(RelatedRightWaterRightSortColumn sortColumn) {
        if (sortColumn == RelatedRightWaterRightSortColumn.BASIN)
            return "basin";
        else if (sortColumn == RelatedRightWaterRightSortColumn.EXT)
            return "ext";
        else if (sortColumn == RelatedRightWaterRightSortColumn.STATUS)
            return "waterRightStatus.description";
        else if (sortColumn == RelatedRightWaterRightSortColumn.TYPEDESCRIPTION)
            return "waterRightType.description";
        else if (sortColumn == RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER)
            return "waterRightNumber";
        else {
            return "waterRightNumber";
        }
    }

    private String getRelatedRightVerXrefSortColumn(RelatedRightWaterRightSortColumn sortColumn) {
        if (sortColumn == RelatedRightWaterRightSortColumn.BASIN)
            return "w.basin";
        else if (sortColumn == RelatedRightWaterRightSortColumn.EXT)
            return "w.ext";
        else if (sortColumn == RelatedRightWaterRightSortColumn.STATUS)
            return "w.waterRightStatus.description";
        else if (sortColumn == RelatedRightWaterRightSortColumn.TYPEDESCRIPTION)
            return "w.waterRightType.description";
        else if (sortColumn == RelatedRightWaterRightSortColumn.WATERRIGHTNUMBER)
            return "w.waterRightNumber";
        else if (sortColumn == RelatedRightWaterRightSortColumn.VERSIONID)
            return "version.version";
        else if (sortColumn == RelatedRightWaterRightSortColumn.VERSIONTYPE)
            return "version.typeReference.meaning";
        else {
            return "w.waterRightNumber";
        }
    }

    @Override
    @Transactional
    public void deleteRelatedRightElement(Long relatedRightId, String elementType) {

        LOGGER.info("Delete Shared Element for Related Right");

        if ((sharedElementRepository.deleteByRelatedRightIdAndTypeCode(new BigDecimal(relatedRightId), elementType)) !=1 )
            throw new DataIntegrityViolationException(String.format("Unable to delete %s element for related right id %s", elementType, relatedRightId));

    }

    @Override
    public RelatedRightElementsPageDto getRelatedRightElements(Integer pageNumber,
                                                               Integer pageSize,
                                                               RelatedRightElementsSortColumn sortColumn,
                                                               SortDirection sortDirection,
                                                               Long relatedRightId) {

        LOGGER.info("Get Shared Elements for Related Right");

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(
                sortDirection.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, getRelatedRightElementsSortColumn(sortColumn))
        );

        Page<SharedElement> resultPage = sharedElementRepository.findAllByRelatedRightId(pageable, new BigDecimal(relatedRightId));
        RelatedRightElementsPageDto page = new RelatedRightElementsPageDto();

        page.setResults(resultPage.getContent().stream().map(record -> {
            return getRelatedRightElementsSearchResultDto(record);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;

    }

    private String getRelatedRightElementsSortColumn(RelatedRightElementsSortColumn column) {

       if (column == RelatedRightElementsSortColumn.ELEMENTTYPE)
           return "typeCode";
        if (column == RelatedRightElementsSortColumn.ELEMENTTYPEVALUE)
            return "typeCodeValue";
        if (column == RelatedRightElementsSortColumn.RELATEDRIGHTID)
            return "relatedRightId";
        return "typeCode";

    }

    private RelatedRightElementsSearchResultDto getRelatedRightElementsSearchResultDto(SharedElement model) {

        RelatedRightElementsSearchResultDto dto = new RelatedRightElementsSearchResultDto();
        dto.setRelatedRightId(model.getRelatedRightId().longValue());
        dto.setElementType(model.getTypeCode());
        if (model.getTypeCodeValue()!=null)
           dto.setElementTypeValue(model.getTypeCodeValue().getDescription());
        return dto;

    }

    @Override
    public RelatedRightElementsSearchResultDto createRelatedRightElement(Long relatedRightId, RelatedRightElementCreationDto newElement) {

        LOGGER.info("Create Shared Element for Related Right");

        Optional<SharedElement> testElement = sharedElementRepository.findByRelatedRightIdAndTypeCode(new BigDecimal(relatedRightId), newElement.getElementType());
        if (testElement.isPresent())
            throw new DataIntegrityViolationException(
                    String.format("Related right element for related right id %s of type %s can not be added more than once.", relatedRightId, newElement.getElementType())
            );

        SharedElement newSharedElement = new SharedElement();
        newSharedElement.setRelatedRightId(new BigDecimal(relatedRightId));
        newSharedElement.setTypeCode(newElement.getElementType());
        SharedElement saved = sharedElementRepository.saveAndFlush(newSharedElement);
        Optional<SharedElement> element = sharedElementRepository.findByRelatedRightIdAndTypeCode(new BigDecimal(relatedRightId), newElement.getElementType());
        if(!element.isPresent())
            throw new DataIntegrityViolationException(
                String.format("Related right element for related right id %s of type %s not found after save.", saved.getRelatedRightId(), saved.getTypeCode())
            );

        return getRelatedRightElementsSearchResultDto(element.get());

    }

    @Override
    @Transactional
    public WaterRightReferenceToRelatedRightSearchResultDto createWaterRightReferenceToRelatedRight(Long relatedRightId, WaterRightReferenceToRelatedRightCreationDto dtoIn) {

        LOGGER.info("Add Water Right references to Related Right");

        List<WaterRightsReferenceDto> newReferences = insertRelatedRightXref(relatedRightId, dtoIn.getWaterRights());
        WaterRightReferenceToRelatedRightSearchResultDto dtoOut = new WaterRightReferenceToRelatedRightSearchResultDto();
        dtoOut.setWaterRights(newReferences);
        return dtoOut;

    }

    private List<WaterRightsReferenceDto> insertRelatedRightXref(Long relatedRightId, List<WaterRightsReferenceDto> input) throws NotFoundException {

        List<WaterRightsReferenceDto> output = new ArrayList<>();
        List<RelatedRightVerXref> list = new ArrayList<>();
        for (WaterRightsReferenceDto ref : input) {

            RelatedRightVerXref xref = new RelatedRightVerXref();
            xref.setRelatedRightId(new BigDecimal(relatedRightId));
            xref.setWaterRightId(new BigDecimal(ref.getWaterRightId()));
            xref.setVersionId(new BigDecimal(ref.getVersionId()));
            list.add(xref);

            WaterRightsReferenceDto wrrd = new WaterRightsReferenceDto();
            wrrd.setWaterRightId(ref.getWaterRightId());
            wrrd.setVersionId(ref.getVersionId());
            output.add(wrrd);
        }

        try {
            relatedRightXrefRepository.saveAllAndFlush(list);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("RRVX_RLRT_FK")) {
                    throw new NotFoundException(
                            String.format("Invalid Related Right Id %s", relatedRightId)
                    );
                } else { // constraintMessage.contains("RRVX_VERS_FK")
                    throw new NotFoundException(
                            String.format("Invalid Version or Water Right while creating references to Related Right Id %s", relatedRightId)
                    );
                }
            }
            throw e;
        }

        return output;

    }

    @Override
    @Transactional
    public void deleteWaterRightReferenceToRelatedRight(Long relatedRightId, Long waterRightId, Long versionId) {

        LOGGER.info("Delete Water Right reference for Related Right");

        if ((relatedRightXrefRepository.deleteByRelatedRightIdAndWaterRightId(new BigDecimal(relatedRightId), new BigDecimal(waterRightId), new BigDecimal(versionId))) !=1 )
            throw new DataIntegrityViolationException(String.format("Unable to delete water right id %s reference for related right id %s", waterRightId, relatedRightId));

    }

    @Override
    public WaterRightsVersionsPageDto searchWaterRightsVersionsAll(Integer pageNumber, Integer pageSize, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, String basin, String waterNumber, String ext) {

        LOGGER.info("Search all Water Rights Versions");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        WaterRightsVersionsPageDto page = new WaterRightsVersionsPageDto();
        Page<WaterRightVersion> resultsPage =
                waterRightVersionRepository.getWaterRightVersionsAll(pageable, sortColumn, sortDirection, basin, waterNumber, ext);

        page.setResults(resultsPage.getContent().stream().map(record -> {
            return loadWaterRightsVersionsSearchResultDto(record);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultsPage.getTotalElements());
        page.setTotalPages(resultsPage.getTotalPages());

        Map<String, String> filters = new HashMap<String, String>();
        if (basin != null)
            filters.put("basin", basin);
        if (waterNumber != null)
            filters.put("waterNumber", waterNumber);
        if (ext != null)
            filters.put("ext", ext);
        page.setFilters(filters);

        return page;

    }

    @Override
    public WaterRightsVersionsPageDto searchWaterRightsVersions(Long relatedRightId, Integer pageNumber, Integer pageSize, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, String basin, String waterNumber, String ext) {

        LOGGER.info("Search Water Rights Versions");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        WaterRightsVersionsPageDto page = new WaterRightsVersionsPageDto();
        Page<WaterRightVersion> resultsPage =
                waterRightVersionRepository.getWaterRightVersionsForRelatedRightReference(pageable, sortColumn, sortDirection, relatedRightId, basin, waterNumber, ext);

        page.setResults(resultsPage.getContent().stream().map(record -> {
            return loadWaterRightsVersionsSearchResultDto(record);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultsPage.getTotalElements());
        page.setTotalPages(resultsPage.getTotalPages());

        Map<String, String> filters = new HashMap<String, String>();
        if (basin != null)
            filters.put("basin", basin);
        if (waterNumber != null)
            filters.put("waterNumber", waterNumber);
        if (ext != null)
            filters.put("ext", ext);
        page.setFilters(filters);

        return page;

    }

    private WaterRightsVersionsSearchResultDto loadWaterRightsVersionsSearchResultDto(WaterRightVersion model) {

        WaterRightsVersionsSearchResultDto dto = new WaterRightsVersionsSearchResultDto();
        dto.setVersionId(model.getVersion().longValue());
        dto.setVersionType(model.getTypeCode());
        dto.setWaterRightId(model.getWaterRightId().longValue());
        dto.setStatus(model.getWaterRight().getWaterRightStatus().getDescription());
        dto.setWaterRightNumber(model.getWaterRight().getWaterRightNumber().longValue());
        dto.setBasin(model.getWaterRight().getBasin());
        dto.setExt(model.getWaterRight().getExt());
        dto.setTypeDescription(model.getWaterRight().getWaterRightType().getDescription());
        dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                        model.getWaterRight().getBasin(),
                        model.getWaterRight().getWaterRightNumber().toString(),
                        model.getWaterRight().getExt()
                )
        );
        dto.setVersion(
                String.format("%s %s", model.getTypeReference().getMeaning(), model.getVersion().toString())
        );
        return dto;

    }

    @Transactional
    @Override
    public RelatedRightDto changeRelatedRight(BigDecimal relatedRightId, UpdateRelatedRightDto updateRelatedRightDto) {

        LOGGER.info("Change an Related Right");

        Optional<RelatedRight> foundRelatedRight = relatedRightRepository.findById(relatedRightId);
        if(!foundRelatedRight.isPresent())
            throw new NotFoundException(String.format("Related Right id %s not found.", relatedRightId));

        RelatedRight oldRelatedRight = foundRelatedRight.get();

        /* Done this way to enable isolation of 'header' vs 'detail' updates by frontend */
        if (updateRelatedRightDto.getRelationshipType() != null) {

            if ((updateRelatedRightDto.getRelationshipType().equals(RELATED_RIGHT_TYPE_MULTIPLE)) &&
                    (!oldRelatedRight.getRelationshipType().equals(RELATED_RIGHT_TYPE_MULTIPLE))) {
                if (sharedElementRepository.deleteByRelatedRightId(relatedRightId) < 0) {
                    throw new DataIntegrityViolationException(String.format("Unable to delete shared elements for related right id %s", relatedRightId));
                }
            }
            oldRelatedRight.setRelationshipType(updateRelatedRightDto.getRelationshipType());

            if ((updateRelatedRightDto.getRelationshipType().equals(RELATED_RIGHT_TYPE_MULTIPLE)) &&
                    (!oldRelatedRight.getRelationshipType().equals(RELATED_RIGHT_TYPE_MULTIPLE))) {
                if (sharedElementRepository.deleteByRelatedRightId(relatedRightId) < 0) {
                    throw new DataIntegrityViolationException(String.format("Unable to delete shared elements for related right id %s", relatedRightId));
                }
            }
            oldRelatedRight.setRelationshipType(updateRelatedRightDto.getRelationshipType());
        } else {
            oldRelatedRight.setFlowRateUnit(updateRelatedRightDto.getFlowRateUnit());
            oldRelatedRight.setMaxFlowRate(updateRelatedRightDto.getMaxFlowRate());
            oldRelatedRight.setMaxAcres(updateRelatedRightDto.getMaxAcres());
            oldRelatedRight.setMaxVolume(updateRelatedRightDto.getMaxVolume());
        }

        relatedRightRepository.saveAndFlush(oldRelatedRight);

        Optional<RelatedRight> afterSaveFoundRelatedRight = relatedRightRepository.findById(relatedRightId);
        if(!afterSaveFoundRelatedRight.isPresent())
            throw new NotFoundException(String.format("Related Right id %s not found.", relatedRightId));

        /* Being done because find is not triggering model (join) refresh */
        afterSaveFoundRelatedRight.get().setRelationshipTypeVal(null);
        return getRelatedRightDetailsDto(afterSaveFoundRelatedRight.get());
    }

    @Transactional
    @Override
    public void deleteRelatedRight(BigDecimal relatedRightId) {

        LOGGER.info("Delete Related Right");

        Optional<RelatedRight> foundRelatedRight = relatedRightRepository.findById(relatedRightId);
        if(!foundRelatedRight.isPresent())
            throw new NotFoundException(
                String.format("Related right %s not found.", relatedRightId)
            );

        RelatedRight relatedRight = foundRelatedRight.get();
        if (relatedRight.getRelatedRightVerXref().size() > 0)
            relatedRightXrefRepository.deleteAllRelatedRightWaterRightReferences(relatedRightId);

        if (relatedRight.getSharedElement().size() > 0)
            sharedElementRepository.deleteByRelatedRightId(relatedRightId);

        if ((relatedRightRepository.deleteRelatedRight(relatedRightId)) !=1 )
            throw new DataIntegrityViolationException(
                String.format("Unable to delete related right %s.", relatedRightId)
            );

    }

    @Transactional
    @Override
    public RelatedRightCreationResultDto createRelatedRight(RelatedRightCreationDto dtoIn) {

        LOGGER.info("Create Related Right");

        RelatedRight relatedRight = new RelatedRight();
        relatedRight.setRelationshipType(dtoIn.getRelationshipType());
        RelatedRight newRelatedRight = relatedRightRepository.saveAndFlush(relatedRight);
        RelatedRightCreationResultDto dtoOut = new RelatedRightCreationResultDto();
        if (dtoIn.getWaterRights().size() > 0) {
            List<WaterRightsReferenceDto> newReferences = insertRelatedRightXref(newRelatedRight.getRelatedRightId().longValue(), dtoIn.getWaterRights());
            dtoOut.setWaterRights(newReferences);
        }
        dtoOut.setRelatedRightId(newRelatedRight.getRelatedRightId().longValue());
        dtoOut.setRelationshipType(newRelatedRight.getRelationshipType());

        return  dtoOut;

    }

}
