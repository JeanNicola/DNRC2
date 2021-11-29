package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.LegalLandCreationDto;
import gov.mt.wris.dtos.PlaceOfUseCreationDto;
import gov.mt.wris.dtos.PlaceOfUseDto;
import gov.mt.wris.dtos.PlacesOfUsePageDto;
import gov.mt.wris.dtos.PlacesOfUseSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Purpose;
import gov.mt.wris.models.RetiredPlaceOfUse;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.repositories.RetiredPlaceOfUseRepository;
import gov.mt.wris.repositories.RetiredPouSubdivisionXrefRepository;
import gov.mt.wris.services.LegalLandService;
import gov.mt.wris.services.RetiredPlacesOfUseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RetiredPlacesOfUseServiceImpl implements RetiredPlacesOfUseService {

    private static Logger LOGGER = LoggerFactory.getLogger(PurposeServiceImpl.class);

    @Autowired
    private RetiredPouSubdivisionXrefRepository retiredPouSubdivisionXrefRepository;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private RetiredPlaceOfUseRepository retiredPlaceOfUseRepository;

    @Autowired
    LegalLandService legalLandService;

    public PlacesOfUsePageDto getRetiredPlacesOfUse(Integer pageNumber, Integer pageSize, PlacesOfUseSortColumn sortColumn, SortDirection sortDirection, BigDecimal purposeId) {

        LOGGER.info("Getting Retired Places Of Use for Purpose: " + purposeId);

        Pageable pageable;

        PlacesOfUsePageDto page = new PlacesOfUsePageDto();

        Page<RetiredPlaceOfUse> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, PurposeServiceImpl.getPlaceOfUseSort(sortColumn, sortDirection, PlaceOfUseStatus.RETIRED));
        resultPage = retiredPlaceOfUseRepository.findByPurposeId(pageable, purposeId);

        page.setResults(resultPage.getContent().stream().map(model -> {
            PlaceOfUseDto dto = PurposeServiceImpl.getPlaceOfUseDto(model);
            dto.setPlaceId(model.getRetiredPlaceId().longValue());
            dto.setHasSubdivisions(model.getSubdivisions().size() > 0);
            if (model.getModifiedReference()!= null) {
                dto.setModifiedByThisChange(model.getModifiedReference().getValue());
                dto.setModifiedByThisChangeDescription(model.getModifiedReference().getMeaning());
            }
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

    public PlaceOfUseDto createRetiredPlaceOfUse(BigDecimal purposeId, PlaceOfUseCreationDto placeOfUseCreationDto, Boolean sort) {

        LOGGER.info("Creating Retired Place Of Use for Purpose: " + purposeId);

        LegalLandCreationDto legalLand = placeOfUseCreationDto.getLegalLand();

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);
        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));

        Long legalId = legalLandService.getLegalLandDescriptionId(legalLand.getDescription320(), legalLand.getDescription160(), legalLand.getDescription80(), legalLand.getDescription40(), legalLand.getGovernmentLot(), legalLand.getTownship(), legalLand.getTownshipDirection(), legalLand.getRange(), legalLand.getRangeDirection(), legalLand.getSection(), legalLand.getCountyId());

        RetiredPlaceOfUse pou = new RetiredPlaceOfUse();

        BigDecimal retiredPlaceId = retiredPlaceOfUseRepository.getNextRetiredPlaceOfUseId(purposeId);
        pou.setRetiredPlaceId(retiredPlaceId);
        pou.setPurposeId(purposeId);
        if (placeOfUseCreationDto.getAcreage()!=null)
            pou.setAcreage(placeOfUseCreationDto.getAcreage());
        pou.setElementOrigin(placeOfUseCreationDto.getElementOrigin());
        pou.setLegalLandDescriptionId(new BigDecimal(legalId));
        pou.setCountyId(new BigDecimal(legalLand.getCountyId()));
        pou.setModified(placeOfUseCreationDto.getModifiedByThisChange());

        PlaceOfUseDto result = PurposeServiceImpl.getPlaceOfUseDto(retiredPlaceOfUseRepository.save(pou));
        result.setPlaceId(retiredPlaceId.longValue());

        if (sort)
            retiredPlaceOfUseRepository.reNumberRetPlaceOfUse("TRS", foundPurpose.get().getWaterRightId(), foundPurpose.get().getVersionId());

        return result;
    }

    public PlaceOfUseDto updateRetiredPlaceOfUse(BigDecimal purposeId, BigDecimal retiredPlaceId, PlaceOfUseCreationDto placeOfUseCreationDto) {

        LOGGER.info("Updating Retired Place Of Use " + retiredPlaceId + " for Purpose: " + purposeId);

        LegalLandCreationDto legalLand = placeOfUseCreationDto.getLegalLand();

        Optional<RetiredPlaceOfUse> foundRetiredPlaceOfUse = retiredPlaceOfUseRepository.findByRetiredPlaceIdAndPurposeId(retiredPlaceId, purposeId);
        if(!foundRetiredPlaceOfUse.isPresent())
            throw new NotFoundException(String.format("Retired Place Of Use id: %s of Purpose: %s not found.", retiredPlaceId, purposeId));

        Long legalId = legalLandService.getLegalLandDescriptionId(legalLand.getDescription320(), legalLand.getDescription160(), legalLand.getDescription80(), legalLand.getDescription40(), legalLand.getGovernmentLot(), legalLand.getTownship(), legalLand.getTownshipDirection(), legalLand.getRange(), legalLand.getRangeDirection(), legalLand.getSection(), legalLand.getCountyId());

        RetiredPlaceOfUse pou = new RetiredPlaceOfUse();

        pou.setRetiredPlaceId(retiredPlaceId);
        pou.setPurposeId(purposeId);
        if (placeOfUseCreationDto.getAcreage()!=null)
            pou.setAcreage(placeOfUseCreationDto.getAcreage());
        pou.setElementOrigin(placeOfUseCreationDto.getElementOrigin());
        pou.setLegalLandDescriptionId(new BigDecimal(legalId));
        pou.setCountyId(new BigDecimal(legalLand.getCountyId()));
        pou.setModified(placeOfUseCreationDto.getModifiedByThisChange());

        return PurposeServiceImpl.getPlaceOfUseDto(retiredPlaceOfUseRepository.save(pou));
    }

    public void deleteRetiredPlaceOfUse(BigDecimal purposeId, BigDecimal retiredPlaceId) {

        LOGGER.info("Deleting Retired Place Of Use " + retiredPlaceId + " for Purpose: " + purposeId);

        Optional<RetiredPlaceOfUse> optional = retiredPlaceOfUseRepository.findByRetiredPlaceIdAndPurposeId(retiredPlaceId, purposeId);
        if(!optional.isPresent())
            throw new NotFoundException(String.format("Retired Place Of Use id: %s of Purpose: %s not found.", retiredPlaceId, purposeId));

        RetiredPlaceOfUse foundRetiredPlaceOfUse = optional.get();
        retiredPlaceOfUseRepository.delete(foundRetiredPlaceOfUse);
        retiredPlaceOfUseRepository.reNumberRetPlaceOfUse("TRS", foundRetiredPlaceOfUse.getPurpose().getWaterRightId(), foundRetiredPlaceOfUse.getPurpose().getVersionId());

    }
}
