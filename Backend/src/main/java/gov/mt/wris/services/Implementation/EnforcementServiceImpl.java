package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllEnforcementsDto;
import gov.mt.wris.dtos.EnforcementDto;
import gov.mt.wris.dtos.EnforcementPodDto;
import gov.mt.wris.dtos.EnforcementPodPageDto;
import gov.mt.wris.dtos.EnforcementPodsSortColumn;
import gov.mt.wris.dtos.EnforcementSearchResultDto;
import gov.mt.wris.dtos.EnforcementsSearchPageDto;
import gov.mt.wris.dtos.EnforcementsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.EnforcementArea;
import gov.mt.wris.models.PointOfDiversionEnforcement;
import gov.mt.wris.repositories.EnforcementAreaRepository;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.PointOfDiversionEnforcementRepository;
import gov.mt.wris.services.EnforcementService;
import gov.mt.wris.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnforcementServiceImpl implements EnforcementService {
    private static Logger LOGGER = LoggerFactory.getLogger(EnforcementService.class);

    @Autowired
    private MasterStaffIndexesRepository generalStaffRepo;

    @Autowired
    private EnforcementAreaRepository enforcementRepository;

    @Autowired
    private PointOfDiversionEnforcementRepository pointOfDiversionEnforcementRepository;

    public AllEnforcementsDto findAll() {
        LOGGER.info("Getting all the Enforcement Areas");

        List<EnforcementDto> results = enforcementRepository.findAllByOrderById().stream()
            .map(enforcement -> getEnforcementDto(enforcement))
            .collect(Collectors.toList());

        return new AllEnforcementsDto().results(results);
    }

    private static EnforcementDto getEnforcementDto(EnforcementArea enforcement) {
        return new EnforcementDto()
            .areaId(enforcement.getId())
            .name(enforcement.getName());
    }

    public EnforcementDto createEnforcement(EnforcementDto creationDto) {
        LOGGER.info("Creating a new Enforcement");

        Optional<EnforcementArea> foundEnforcement = enforcementRepository.findById(creationDto.getAreaId());
        if(foundEnforcement.isPresent()) {
            throw new ValidationException("An Enforcement Area with this Enf Area already exists");
        }

        EnforcementArea enforcement = getEnforcement(creationDto);

        enforcement = enforcementRepository.save(enforcement);

        return getEnforcementDto(enforcement);
    }

    private static EnforcementArea getEnforcement(EnforcementDto dto) {
        EnforcementArea enforcement = new EnforcementArea();
        enforcement.setId(dto.getAreaId());
        enforcement.setName(dto.getName());
        return enforcement;
    }

    public EnforcementsSearchPageDto searchEnforcements(int pageNumber, int pageSize, EnforcementsSortColumn sortColumn, SortDirection sortDirection, String area, String name, String enforcementNumber, String basin, String waterNumber) {

        LOGGER.info("Search Enforcement Area");

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<PointOfDiversionEnforcement> results = enforcementRepository.searchEnforcements(pageable, sortColumn, sortDirection, area, name, enforcementNumber, basin, waterNumber);

        EnforcementsSearchPageDto page = new EnforcementsSearchPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return enforcementSearchResultDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if (area != null) filters.put("area", area);
        if (name != null) filters.put("name", name);
        if (enforcementNumber != null) filters.put("enforcementNumber", enforcementNumber);
        if (basin != null) filters.put("basin", basin);
        if (waterNumber != null) filters.put("waterNumber", waterNumber);
        page.setFilters(filters);
        return page;

    }

    private EnforcementSearchResultDto enforcementSearchResultDtoLoader(PointOfDiversionEnforcement model) {

        EnforcementSearchResultDto dto = new EnforcementSearchResultDto();
        dto.setEnforcementArea(model.getEnforcementArea().getId());
        dto.setEnforcementName(model.getEnforcementArea().getName());
        dto.setEnforcementNumber(model.getEnforcementNumber());
        dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                        model.getPointOfDiversion().getVersion().getWaterRight().getBasin(),
                        model.getPointOfDiversion().getVersion().getWaterRight().getWaterRightNumber().toString(),
                        model.getPointOfDiversion().getVersion().getWaterRight().getExt()
                )
        );
        return dto;

    }

    public EnforcementPodPageDto getEnforcementPods(int pageNumber, int pageSize, EnforcementPodsSortColumn sortColumn, SortDirection sortDirection, String area) {

        LOGGER.info("Get Enforcement Area PODs");

        Sort sortDtoColumn = getEnforcementPodsSortColumn(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<PointOfDiversionEnforcement> results = pointOfDiversionEnforcementRepository.findAllByEnforcementId(pageable, area);

        EnforcementPodPageDto page = new EnforcementPodPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return enforcementPodDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getEnforcementPodsSortColumn(EnforcementPodsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = null;
        switch(sortColumn) {
            case COMPLETEVERSION:
                sort = Sort.by(direction, "v.typeReference.meaning");
                sort = sort.and(Sort.by(direction, "v.version"));
                sort = sort.and(Sort.by(direction, "v.versionStatus.description"));
                break;
            case ENFORCEMENTNUMBER:
                sort = Sort.by(direction, "enforcementNumber");
                break;
            case DITCHLEGALLANDDESCRIPTION:
                sort = Sort.by(direction, "dll.governmentLot");
                sort = sort.and(Sort.by(direction, "dll.description320"));
                sort = sort.and(Sort.by(direction, "dll.description160"));
                sort = sort.and(Sort.by(direction, "dll.description80"));
                sort = sort.and(Sort.by(direction, "dll.description40"));
                sort = sort.and(Sort.by(direction, "dtrs.section"));
                sort = sort.and(Sort.by(direction, "dtrs.township"));
                sort = sort.and(Sort.by(direction, "dtrs.townshipDirection"));
                sort = sort.and(Sort.by(direction, "dtrs.range"));
                sort = sort.and(Sort.by(direction, "dtrs.rangeDirection"));
                sort = sort.and(Sort.by(direction, "dc.name"));
                sort = sort.and(Sort.by(direction, "dc.stateCode"));
                break;
            case DITCHNAME:
                sort = Sort.by(direction, "d.name");
                break;
            case SHORTCOMMENT:
                sort = Sort.by(direction, "comments");
                break;
            case PODNUMBER:
                sort = Sort.by(direction, "pod.number");
                break;
            case MAJORTYPEDESCRIPTION:
                sort = Sort.by(direction, "mj.meaning");
                break;
            case MEANSOFDIVERSIONDESCRIPTION:
                sort = Sort.by(direction, "md.meaning");
                break;
            case LEGALLANDDESCRIPTION:
                sort = Sort.by(direction, "ll.governmentLot");
                sort = sort.and(Sort.by(direction, "ll.description320"));
                sort = sort.and(Sort.by(direction, "ll.description160"));
                sort = sort.and(Sort.by(direction, "ll.description80"));
                sort = sort.and(Sort.by(direction, "ll.description40"));
                sort = sort.and(Sort.by(direction, "trs.section"));
                sort = sort.and(Sort.by(direction, "trs.township"));
                sort = sort.and(Sort.by(direction, "trs.townshipDirection"));
                sort = sort.and(Sort.by(direction, "trs.range"));
                sort = sort.and(Sort.by(direction, "trs.rangeDirection"));
                sort = sort.and(Sort.by(direction, "c.name"));
                sort = sort.and(Sort.by(direction, "c.stateCode"));
                break;
            default:
                sort = Sort.by(direction, "w.basin");
                sort = sort.and(Sort.by(direction, "w.waterRightNumber"));
                sort = sort.and(Sort.by(direction, "w.ext"));
        }

        if (sortColumn != null && !sortColumn.equals(EnforcementPodsSortColumn.COMPLETEWATERRIGHTNUMBER)) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "w.basin"));
            sort = sort.and(Sort.by(Sort.Direction.ASC, "w.waterRightNumber"));
            sort = sort.and(Sort.by(Sort.Direction.ASC, "w.ext"));
            sort = sort.and(Sort.by(Sort.Direction.ASC, "pod.number"));
        }

        return sort;

    }

    private static EnforcementPodDto enforcementPodDtoLoader(PointOfDiversionEnforcement model) {

        EnforcementPodDto dto = new EnforcementPodDto();
        dto.setComment(model.getComments());
        dto.setVersionNumber(model.getPointOfDiversion().getVersionId().longValue());
        dto.setWaterRightId(model.getPointOfDiversion().getWaterRightId().longValue());
        dto.setPodId(model.getPointOfDiversion().getId().longValue());
        dto.setPodNumber(model.getPointOfDiversion().getNumber().longValue());
        dto.setEnforcementNumber(model.getEnforcementNumber());
        if (model.getPointOfDiversion().getMajorTypeReference() != null)
            dto.setMajorTypeDescription(model.getPointOfDiversion().getMajorTypeReference().getMeaning());
        if (model.getPointOfDiversion().getMeansOfDiversion() != null)
            dto.setMeansOfDiversionDescription(model.getPointOfDiversion().getMeansOfDiversion().getDescription());
        if(model.getPointOfDiversion().getLegalLandDescription() != null) {
            dto.setLegalLandDescription(Helpers.buildLegalLandDescription(
                    model.getPointOfDiversion().getLegalLandDescription(),
                    model.getPointOfDiversion().getCounty())
            );
        }
        dto.setCompleteWaterRightNumber(
            Helpers.buildCompleteWaterRightNumber(
                model.getPointOfDiversion().getVersion().getWaterRight().getBasin(),
                model.getPointOfDiversion().getVersion().getWaterRight().getWaterRightNumber().toString(),
                model.getPointOfDiversion().getVersion().getWaterRight().getExt())
        );
        dto.setCompleteVersion(
            Helpers.buildCompleteWaterRightVersion(
                    model.getPointOfDiversion().getVersion().getTypeReference().getMeaning(),
                    model.getPointOfDiversion().getVersion().getVersion().toString(),
                    model.getPointOfDiversion().getVersion().getVersionStatus().getDescription())
        );
        if(model.getPointOfDiversion().getDitch() != null) {
            dto.setDitchLegalLandDescription(Helpers.buildLegalLandDescription(
                    model.getPointOfDiversion().getDitch().getLegalLandDescription(),
                    model.getPointOfDiversion().getDitch().getCounty())
            );
            dto.setDitchName(model.getPointOfDiversion().getDitch().getName());
        }
        return dto;

    }

    public EnforcementDto getEnforcement(String area) {

        LOGGER.info("Get Enforcement Area");

        Optional<EnforcementArea> fndArea =  enforcementRepository.getEnforcementAreaById(area);
        if (!fndArea.isPresent())
            throw new NotFoundException(String.format("Enforcement Area %s not found",area));
        EnforcementArea ea = fndArea.get();
        EnforcementDto dto = new EnforcementDto();
        dto.setAreaId(ea.getId());
        dto.setName(ea.getName());
        return dto;

    }

    public EnforcementDto updateEnforcementArea(String areaId, EnforcementDto updateDto) {

        LOGGER.info("Update Enforcement Area");
        Optional<EnforcementArea> fndArea = enforcementRepository.getEnforcementAreaById(areaId);
        if (!fndArea.isPresent())
            throw new NotFoundException(String.format("Area Id %s not found", areaId));
        EnforcementArea oldArea = fndArea.get();
        oldArea.setName(updateDto.getName());
        EnforcementArea enforcement = enforcementRepository.saveAndFlush(oldArea);
        EnforcementDto dto = new EnforcementDto();
        dto.setAreaId(enforcement.getId());
        dto.setName(enforcement.getName());
        return dto;

    }

}
