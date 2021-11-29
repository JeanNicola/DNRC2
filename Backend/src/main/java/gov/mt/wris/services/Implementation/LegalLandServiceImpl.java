package gov.mt.wris.services.Implementation;

import gov.mt.wris.models.County;
import gov.mt.wris.models.LegalLandDescription;
import gov.mt.wris.repositories.LegalLandDescriptionRepository;
import gov.mt.wris.services.LegalLandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

@Service
public class LegalLandServiceImpl implements LegalLandService {

    @Autowired
    LegalLandDescriptionRepository legalRepository;

    public Long getLegalLandDescriptionId(String description320, String description160, String description80, String description40, Long governmentLot, Long township, String townshipDirection, Long range, String rangeDirection, Long section, Long countyId) {
        Long legalId = legalRepository.validateLegalLandDescription(description320,
                description160,
                description80,
                description40,
                governmentLot,
                township,
                townshipDirection,
                range,
                rangeDirection,
                section,
                countyId);
        if(legalId == -1) {
            throw new ValidationException("Invalid TRS information");
        } else if (legalId == -2) {
            throw new ValidationException("County and TRS do not match");
        } else if (legalId == -3 || legalId == -4) {
            throw new ValidationException("Invalid Legal Land Description");
        }
        return legalId;
    }

    public String buildLegalLandDescription(LegalLandDescription land, County county) {
        if (county == null && land == null) {
            return null;
        } else if (land == null) {
            return county.getName() + " " + county.getStateCode();
        }

        List<String> descriptionArray = land != null ? new ArrayList<String>(Arrays.asList(
            land.getGovernmentLot() != null ? "Govt Lot " + land.getGovernmentLot().toString() : null,
            land.getDescription40(),
            land.getDescription80(),
            land.getDescription160(),
            land.getDescription320(),
            land.getTrs().getSection() != null ? land.getTrs().getSection().toString() : null,
            Arrays.asList(
                land.getTrs().getTownship() != null ? land.getTrs().getTownship().toString() : null,
                land.getTrs().getTownshipDirection()
            ).stream().filter(part -> part != null).collect(Collectors.joining("")),
            Arrays.asList(
                land.getTrs().getRange() != null ? land.getTrs().getRange().toString() : null,
                land.getTrs().getRangeDirection()
            ).stream().filter(part -> part != null).collect(Collectors.joining(""))
        )) : new ArrayList<>();

        if(county != null) {
            descriptionArray.addAll(Arrays.asList(
                county.getName(),
                county.getStateCode()
            ));
        }

        return descriptionArray.stream().filter(part -> 
            part != null
        ).collect(Collectors.joining(" "));
    }
}
