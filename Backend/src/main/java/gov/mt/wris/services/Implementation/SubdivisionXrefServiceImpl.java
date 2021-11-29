package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionCreationDto;
import gov.mt.wris.dtos.SubdivisionDto;
import gov.mt.wris.dtos.SubdivisionPageDto;
import gov.mt.wris.dtos.SubdivisionSortColumn;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.PlaceOfUse;
import gov.mt.wris.models.Purpose;
import gov.mt.wris.models.SubdivisionXref;
import gov.mt.wris.repositories.PlaceOfUseRepository;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.repositories.SubdivisionXrefRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.SubdivisionXrefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubdivisionXrefServiceImpl implements SubdivisionXrefService {

    private static Logger LOGGER = LoggerFactory.getLogger(SubdivisionXrefServiceImpl.class);

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private SubdivisionXrefRepository subdivisionXrefRepository;

    @Autowired
    private PlaceOfUseRepository placeOfUseRepository;

    @Autowired
    private WaterRightVersionRepository waterRightVersionRepository;

    public SubdivisionPageDto getSubdivisionsForPlaceOfUse(BigDecimal placeId, BigDecimal purposeId, Integer pageNumber, Integer pageSize, SubdivisionSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Get Subdivisions for Place Of Use");

        Optional<PlaceOfUse> fndPlou = placeOfUseRepository.findPlaceOfUseByPlaceIdAndPurposeId(placeId, purposeId);
        if(!fndPlou.isPresent())
            throw new NotFoundException(String.format("Place Of Use %s of Purpose %s not found.", placeId, purposeId));

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize, getSubdivisionSort(sortColumn, sortDirection));
        Page<SubdivisionXref> resultPage = subdivisionXrefRepository.findByPlaceIdAndPurposeId(pageable, placeId, purposeId);

        SubdivisionPageDto page = new SubdivisionPageDto();
        page.setResults(resultPage.getContent().stream().map(model -> {
            return getSubdivisionDto(model);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;

    }

    private Sort getSubdivisionSort(SubdivisionSortColumn column, SortDirection direction) {

        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        switch (column) {
            case DORNAME:
                primary = Sort.by(sortOrderDirection, "subdivisionCode.dorName");
                break;
            case BLK:
                primary = Sort.by(sortOrderDirection, "blk");
                break;
            case LOT:
                primary = Sort.by(sortOrderDirection, "lot");
                break;
            default:
                primary = Sort.by(sortOrderDirection, "subdivisionCode.dnrcName");
        }
        secondary = Sort.by(Sort.Direction.ASC, "subdivisionCode.dnrcName");
        sort = primary.and(secondary);
        return sort;

    }

    static SubdivisionDto getSubdivisionDto(SubdivisionXref model) {

        SubdivisionDto dto = new SubdivisionDto();
        dto.setPlaceId(model.getPlaceId().longValue());
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setCode(model.getCode());
        if (model.getSubdivisionCode() != null) {
            dto.setDnrcName(model.getSubdivisionCode().getDnrcName());
            dto.setDorName(model.getSubdivisionCode().getDorName());
        }
        dto.setBlk(model.getBlk());
        dto.setLot(model.getLot());

        return dto;

    }

    public SubdivisionDto createSubdivisionForPlaceOfUse(BigDecimal placeId, BigDecimal purposeId, SubdivisionCreationDto subdivisionCreationDto) {

        LOGGER.info("Create Subdivision");

        Optional<PlaceOfUse> fndPlou = placeOfUseRepository.findPlaceOfUseByPlaceIdAndPurposeId(placeId, purposeId);
        if(!fndPlou.isPresent())
            throw new NotFoundException(String.format("Place Of Use %s of Purpose %s not found.", placeId, purposeId));

        SubdivisionXref model = new SubdivisionXref();
        model.setPlaceId(placeId);
        model.setPurposeId(purposeId);
        model.setCode(subdivisionCreationDto.getCode());
        model.setLot(subdivisionCreationDto.getLot());
        model.setBlk(subdivisionCreationDto.getBlk());
        return getSubdivisionDto(subdivisionXrefRepository.save(model));

    }

    public SubdivisionDto updateSubdivisionForPlaceOfUse(BigDecimal purposeId, BigDecimal placeId, String code, SubdivisionCreationDto subdivisionCreationDto) {

        LOGGER.info("Update Subdivision");

        Optional<SubdivisionXref> fndSubdivision = subdivisionXrefRepository.findSubdivisionByPlaceIdAndPurposeIdAndCode(placeId, purposeId, code);
        if(!fndSubdivision.isPresent())
            throw new NotFoundException(String.format("Subdivision %s for Place Of Use %s and Purpose %s not found.", code, placeId, purposeId));

        SubdivisionXref subdivision = fndSubdivision.get();
        subdivision.setBlk(subdivisionCreationDto.getBlk());
        subdivision.setLot(subdivisionCreationDto.getLot());
        return getSubdivisionDto(subdivisionXrefRepository.save(subdivision));

    }

    public void deleteSubdivisionFromPlaceOfUse(BigDecimal purposeId, BigDecimal placeId, String code) {

        LOGGER.info("Delete Subdivision");

        Optional<SubdivisionXref> fndSubdivision = subdivisionXrefRepository.findSubdivisionByPlaceIdAndPurposeIdAndCode(placeId, purposeId, code);
        if(!fndSubdivision.isPresent())
            throw new NotFoundException(String.format("Subdivision %s for Place Of Use %s and Purpose %s not found.", code, placeId, purposeId));
        SubdivisionXref subdivision = fndSubdivision.get();
        subdivisionXrefRepository.delete(subdivision);

    }

    public Integer PlaceOfUseCopyPods(BigDecimal purposeId) {

        LOGGER.info("Copy POD to Place Of Use");

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);
        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose %s not found.", purposeId));
        if (!foundPurpose.get().getPlaces().isEmpty())
            throw new ValidationException("No Place of Use records can exist to perform the copy.");
        Integer result = placeOfUseRepository.replicatePodsPlus(purposeId);

        Purpose purpose = foundPurpose.get();
        String program = purpose.getWaterRightVersion().getWaterRight().getWaterRightType().getProgram();

        if ("NA".equals(program)) {
            BigDecimal maxVolume = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion());
            waterRightVersionRepository.updateMaxVolume(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion(), maxVolume);
        }

        return result;

    }

}
