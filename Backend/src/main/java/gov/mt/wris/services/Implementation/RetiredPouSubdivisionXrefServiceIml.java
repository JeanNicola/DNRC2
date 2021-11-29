package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionCreationDto;
import gov.mt.wris.dtos.SubdivisionDto;
import gov.mt.wris.dtos.SubdivisionPageDto;
import gov.mt.wris.dtos.SubdivisionSortColumn;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Purpose;
import gov.mt.wris.models.RetiredPlaceOfUse;
import gov.mt.wris.models.RetiredPouSubdivisionXref;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.repositories.RetiredPlaceOfUseRepository;
import gov.mt.wris.repositories.RetiredPouSubdivisionXrefRepository;
import gov.mt.wris.services.RetiredPouSubdivisionXrefService;
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
public class RetiredPouSubdivisionXrefServiceIml implements RetiredPouSubdivisionXrefService {

    private static Logger LOGGER = LoggerFactory.getLogger(PurposeServiceImpl.class);

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private RetiredPouSubdivisionXrefRepository retiredPouSubdivisionXrefRepository;

    @Autowired
    private RetiredPlaceOfUseRepository retiredPlaceOfUseRepository;

    private Sort getSubdivisionSort(SubdivisionSortColumn column, SortDirection direction) {

        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        secondary = Sort.by(Sort.Direction.ASC, "subdivisionCode.dnrcName");

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
                secondary = Sort.by(sortOrderDirection, "subdivisionCode.dorName");
        }

        sort = primary.and(secondary);

        return sort;

    }

    static SubdivisionDto getSubdivisionDto(RetiredPouSubdivisionXref model) {
        SubdivisionDto dto = new SubdivisionDto();

        dto.setPlaceId(model.getRetiredPlaceId().longValue());
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

    public SubdivisionPageDto getSubdivisionsForRetPou(BigDecimal retiredPlaceId, BigDecimal purposeId, Integer pageNumber, Integer pageSize, SubdivisionSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Searching Subdivisions for Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);

        Optional<RetiredPlaceOfUse> foundRetiredPlaceOfUse = retiredPlaceOfUseRepository.findByRetiredPlaceIdAndPurposeId(retiredPlaceId, purposeId);
        if(!foundRetiredPlaceOfUse.isPresent())
            throw new NotFoundException(String.format("Retired Place Of Use id: %s of Purpose: %s not found.", retiredPlaceId, purposeId));

        Pageable pageable;

        SubdivisionPageDto page = new SubdivisionPageDto();

        Page<RetiredPouSubdivisionXref> resultPage;
        pageable = PageRequest.of(pageNumber -1, pageSize, getSubdivisionSort(sortColumn, sortDirection));
        resultPage = retiredPouSubdivisionXrefRepository.findByRetiredPlaceIdAndPurposeId(pageable, retiredPlaceId, purposeId);

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

    public SubdivisionDto createSubdivisionForRetPou(BigDecimal retiredPlaceId, BigDecimal purposeId, SubdivisionCreationDto subdivisionCreationDto) {

        LOGGER.info("Create Subdivision for Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);

        Optional<RetiredPlaceOfUse> foundRetiredPlaceOfUse = retiredPlaceOfUseRepository.findByRetiredPlaceIdAndPurposeId(retiredPlaceId, purposeId);
        if(!foundRetiredPlaceOfUse.isPresent())
            throw new NotFoundException(String.format("Retired Place Of Use id: %s of Purpose: %s not found.", retiredPlaceId, purposeId));


        RetiredPouSubdivisionXref model = new RetiredPouSubdivisionXref();

        model.setRetiredPlaceId(retiredPlaceId);
        model.setPurposeId(purposeId);
        model.setCode(subdivisionCreationDto.getCode());
        model.setLot(subdivisionCreationDto.getLot());
        model.setBlk(subdivisionCreationDto.getBlk());

        return getSubdivisionDto(retiredPouSubdivisionXrefRepository.save(model));
    }

    public SubdivisionDto updateSubdivisionForRetPou(BigDecimal purposeId, BigDecimal retiredPlaceId, String code, SubdivisionCreationDto subdivisionCreationDto) {

        LOGGER.info("Update Subdivision " + code  +" for Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);

        Optional<RetiredPouSubdivisionXref> optional = retiredPouSubdivisionXrefRepository.findByRetiredPlaceIdAndPurposeIdAndCode(retiredPlaceId, purposeId, code);
        if(!optional.isPresent())
            throw new NotFoundException(String.format("Subdivision: %s for Retired Place id: %s and Purpose: %s not found.", code, retiredPlaceId, purposeId));

        RetiredPouSubdivisionXref foundSubdivision = optional.get();

        foundSubdivision.setBlk(subdivisionCreationDto.getBlk());
        foundSubdivision.setLot(subdivisionCreationDto.getLot());

        return getSubdivisionDto(retiredPouSubdivisionXrefRepository.save(foundSubdivision));
    }

    public void deleteSubdivisionFromRetPou(BigDecimal purposeId, BigDecimal retiredPlaceId, String code) {

        LOGGER.info("Delete Subdivision " + code  +" from Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);

        Optional<RetiredPouSubdivisionXref> optional = retiredPouSubdivisionXrefRepository.findByRetiredPlaceIdAndPurposeIdAndCode(retiredPlaceId, purposeId, code);
        if(!optional.isPresent())
            throw new NotFoundException(String.format("Subdivision: %s for Retired Place id: %s and Purpose: %s not found.", code, retiredPlaceId, purposeId));

        RetiredPouSubdivisionXref foundSubdivision = optional.get();

        retiredPouSubdivisionXrefRepository.delete(foundSubdivision);

    }

    public Integer retiredPousCopyPods(BigDecimal purposeId) {

        LOGGER.info("Copying POU's to Retired Place Of Use in Purpose: " + purposeId);

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);
        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));

        if (foundPurpose.get().getRetiredPlacesOfUses().size() > 1) {
            throw new ValidationException("No Place of Use Retired records can exist to perform the copy.");
        }

        return retiredPlaceOfUseRepository.replicateRetPods(purposeId);
    }
}
