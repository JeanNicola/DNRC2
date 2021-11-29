package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.PeriodOfUse;
import gov.mt.wris.models.PurposeVolumeCalculation;
import gov.mt.wris.repositories.PeriodOfUseRepository;
import gov.mt.wris.repositories.PlaceOfUseRepository;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.PurposeVolumeCalculationService;

@Service
public class PurposeVolumeCalculationServiceImpl implements PurposeVolumeCalculationService {

    private static Logger LOGGER = LoggerFactory.getLogger(PurposeVolumeCalculationServiceImpl.class);

    @Autowired
    PurposeRepository purposeRepository;

    @Autowired
    PlaceOfUseRepository placeOfUseRepository;

    @Autowired
    PeriodOfUseRepository periodOfUseRepository;

    @Autowired
    WaterRightVersionRepository waterRightVersionRepository;

    public PurposeVolumeCalculation calculateVolume(BigDecimal purposeId,
        String purposeType, int household, String clarCode, BigDecimal animalUnits, List<PeriodOfUse> periods) {

        LOGGER.info("Calculate Purpose Volume");

        PurposeVolumeCalculation pvc = new PurposeVolumeCalculation();
        List<String> messages = new ArrayList<>();
        BigDecimal volume = null;
        boolean error = false;

        switch (purposeType) {
            case Constants.PURPOSE_TYPE_CODE_DOMESTIC:
            case Constants.PURPOSE_TYPE_CODE_MULTIPLE_DOMESTIC:
            {
                if (!hasValidPeriod(periods)) {
                    messages.add("Period Begin and End Dates have not been entered");
                    error = true;
                }
                if (household == 0) {
                    messages.add("# of Households have not been entered");
                    error = true;
                } else {
                    double year = 365;
                    double days = calculateDays(purposeId);
                    volume = new BigDecimal((household * 1) * (days/year));
                }
            }
            break;
            case Constants.PURPOSE_TYPE_CODE_STOCK:
            {
                if (!hasValidPeriod(periods)) {
                    messages.add("Period Begin and End Dates have not been entered");
                    error = true;
                }
                if (animalUnits == null || animalUnits.equals(BigDecimal.ZERO)) {
                    messages.add("# of Animals have not been entered");
                    error = true;
                } else {
                    double year = 365;
                    double days = calculateDays(purposeId);
                    //volume = new BigDecimal((animalUnits * .017) * (days/year));
                    volume = animalUnits.multiply(new BigDecimal(.017)).multiply(new BigDecimal(days).divide(new BigDecimal(year), 2, RoundingMode.HALF_UP));
                }
            }
            break;
            case Constants.PURPOSE_TYPE_CODE_LAWN_GARDEN:
            {
                BigDecimal acreage1 = getPlaceOfUseAcreage(purposeId);
                BigDecimal factor = new BigDecimal("2.5");
                boolean hasOtherPouAcreage = false;
                boolean hasPouParcels = false;

				/* CHECK FOR IR PURPOSE AND IR POU PARCEL RECORDS WITH ACREAGE > 0
				   HAVE TO CHECK PURPOSE AND POU PARCELS SEPARATELY BECAUSE OF LG */
                boolean isMultiPurpose =  purposeRepository.findOtherPurposeCount(purposeId, Constants.PURPOSE_TYPE_CODE_IRRIGATION) > 0;
                if (isMultiPurpose)
                    hasOtherPouAcreage = checkOtherPouAcreage(purposeId, Constants.PURPOSE_TYPE_CODE_IRRIGATION);
                /* CHECK LG PURPOSE FOR PARCEL RECORDS AND GET ACREAGE */
                if (acreage1.compareTo(BigDecimal.ZERO)==1)
                    hasPouParcels = true;
                if (isMultiPurpose && (!hasOtherPouAcreage || !hasPouParcels)) {
                    messages.add("LG and IR both exist");
                    messages.add("Place Of Use Parcel Acreage must be entered for both IR and LG");
                    error = true;
                } else {
					/* isMultiPurpose = 'F' OR (isMultiPurpose = 'T' AND hasOtherPouAcreage = 'T' AND hasPouParcels = 'T')
					   NO IR PURPOSE OR (IR PURPOSE WITH ACREAGE AND LG WITH ACREAGE)  */
                    if (acreage1.compareTo(BigDecimal.ZERO)==1) {
                        volume = acreage1.multiply(factor);
                    } else {
                        messages.add("LG Parcel Acreage or Max Acres is 0");
                        error = true;
                    }
                }
            }
            break;
            case Constants.PURPOSE_TYPE_CODE_IRRIGATION:
            {
                List<String> climaticAreas = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
                BigDecimal acreage1 = getPlaceOfUseAcreage(purposeId);
                BigDecimal factor1 = new BigDecimal("2.3");
                BigDecimal factor2 = new BigDecimal("2.0");
                BigDecimal factor3 = new BigDecimal("1.8");
                BigDecimal factor4 = new BigDecimal("1.6");
                BigDecimal factor5 = new BigDecimal("1.3");
                boolean hasOtherPouAcreage = false;
                boolean hasPouParcels = false;

				/* CHECK FOR LG PURPOSE AND LG POU PARCEL RECORDS WITH ACREAGE > 0
				   HAVE TO CHECK PURPOSE AND POU PARCELS SEPARATELY BECAUSE OF LG */
                boolean isMultiPurpose =  purposeRepository.findOtherPurposeCount(purposeId, Constants.PURPOSE_TYPE_CODE_LAWN_GARDEN) > 0;
                if (isMultiPurpose)
                    hasOtherPouAcreage = checkOtherPouAcreage(purposeId, Constants.PURPOSE_TYPE_CODE_LAWN_GARDEN);
                /* CHECK LG PURPOSE FOR PARCEL RECORDS AND GET ACREAGE */
                if (acreage1.compareTo(BigDecimal.ZERO)==1)
                    hasPouParcels = true;
                if (isMultiPurpose && (!hasOtherPouAcreage || !hasPouParcels)) {
                    messages.add("LG and IR both exist");
                    messages.add("Place Of Use Parcel Acreage must be entered for both IR and LG");
                    error = true;
                } else {
					/* MULTI_PURP = 'F' OR (MULTI_PURP = 'T' AND OTHR_POU_PARCELS = 'T' AND POU_PARCELS = 'T')
					   NO IR PURPOSE OR (IR PURPOSE WITH ACREAGE AND LG WITH ACREAGE).
					   Customer would like to see these errors, if they exist, at same time:  */
                    if (acreage1.compareTo(BigDecimal.ZERO)==0) {
                        messages.add("IR Parcel Acreage or Max Acres is 0");
                        error = true;
                    }
                    if (!climaticAreas.contains(clarCode)) {
                        messages.add("Climatic Area has not been entered");
                        error = true;
                    }
                    if (!error) {
                        switch (clarCode) {
                            case "1":
                                volume = acreage1.multiply(factor1);
                                break;
                            case "2":
                                volume = acreage1.multiply(factor2);
                                break;
                            case "3":
                                volume = acreage1.multiply(factor3);
                                break;
                            case "4":
                                volume = acreage1.multiply(factor4);
                                break;
                            case "5":
                                volume = acreage1.multiply(factor5);
                                break;
                        }
                    }
                }
            }
        }

        if (!error) {
            pvc.setVolume(volume.setScale(2, RoundingMode.HALF_UP));
        } else {
            messages.add("Volume cannot be calculated");
        }
        pvc.setMessages(messages);
        return pvc;

    }

    private boolean hasValidPeriod(List<PeriodOfUse> periods) {

        // Periods can't be empty and one of them must have
        // not null begin & end date
        boolean valid = false;
        if (!periods.isEmpty()) {
            for (PeriodOfUse p : periods) {
                if ((p.beginDate != null) && p.endDate != null)
                    valid = true;
                    break;
            }
        }
        return valid;

    }

    private double calculateDays(BigDecimal purposeId) {

        double totalDays = 0;
        List<PeriodOfUse> periods =  periodOfUseRepository.findDistinctAllByPurposeId(purposeId);
        if (!periods.isEmpty()) {
            for (PeriodOfUse p : periods) {
                int bd = p.getBeginDate().getDayOfYear();
                int ed = p.getEndDate().getDayOfYear();
                if (bd < ed) {
                    totalDays = totalDays + ((ed - bd) + 1);
                } else {
                    totalDays = totalDays + (((365-bd)+ed)+1);
                }
            }
            if (totalDays>365)
                totalDays = 365;
        }
        return totalDays;

    }

    private boolean checkOtherPouAcreage(BigDecimal purposeId, String purposeType) {

        /* GET_OTHER_POU_ACREAGE */
        boolean hasOther = false;
        Optional<BigDecimal> acreage = placeOfUseRepository.getOtherPurposeAcreage(purposeId, purposeType);
        if (acreage.isPresent() && acreage.get().compareTo(BigDecimal.ZERO) ==1)
            hasOther = true;
        return hasOther;

    }

    private BigDecimal getPlaceOfUseAcreage(BigDecimal purposeId) {

        BigDecimal total = BigDecimal.ZERO;
        Optional<BigDecimal> acres = placeOfUseRepository.getPurposePlaceOfUseAcreage(purposeId);
        if (acres.isPresent())
            total = acres.get();
        /* if POU has no acreage then check water right version for max_acres */
        if (total.compareTo(BigDecimal.ZERO)<1) {
            Optional<BigDecimal> versionMaxAcres = purposeRepository.getWaterRightVersionMaxAcresByPurposeId(purposeId);
            if (versionMaxAcres.isPresent())
                total = versionMaxAcres.get();
        }
        return total;

    }

}
