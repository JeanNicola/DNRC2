package gov.mt.wris.utils;

import gov.mt.wris.models.County;
import gov.mt.wris.models.LegalLandDescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import gov.mt.wris.models.County;
import gov.mt.wris.models.LegalLandDescription;

public class Helpers {
    public static boolean isAscii(String text) {
        //check everything between " " and "~" in character code
        String pattern = "^[ -~]+$";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(text);

        return m.matches();
    }

    public static String buildName(String ... names) {
        // last name
        String name = names[0];

        // first name
        if(names.length > 1 && names[1] != null) {
            name += ", " + names[1];
        }

        // middle initial
        if(names.length > 2 && names[2] != null) {
            name += " " + names[2];
        }

        // suffix
        if(names.length > 3 && names[3] != null) {
            name += ", " + names[3];
        }

        return name;
    }

    // Build a name in the usual order, First Name, Middle Initial, Last Name and Suffix,
    // with a delimiter of a space
    public static String buildFirstLastName(String ... names) {
        return Arrays.stream(names)
            .filter(name -> name != null)
            .collect(Collectors.joining(" "));
    }

    public static String buildCompleteWaterRightNumber(String ... values) {
        // basin
        String number = values[0];

        // waterRightNumber
        if(values.length > 1 && values[1] != null) {
            number += " " + values[1];
        }

        // ext
        if(values.length > 2 && values[2] != null) {
            number += " " + values[2];
        }

        return number;
    }

    public static String buildCompleteWaterRightVersion(String ... values) {
        // Version Type Description
        String number = values[0];

        // Version Id
        if(values.length > 1 && values[1] != null) {
            number += " " + values[1];
        }

        // Version Status Description
        if(values.length > 2 && values[2] != null) {
            number += " " + values[2];
        }

        return number;
    }

    public static <T> Set<T> findDuplicates(Collection<T> collection) {
        Set<T> duplicates = new HashSet<>();
        Set<T> uniques = new HashSet<>();

        for(T t : collection) {
            if(!uniques.add(t)) {
                duplicates.add(t);
            }
        }
        
        return duplicates;
    }

    public static String buildLegalLandDescription(LegalLandDescription land, County county) {
        List<String> descriptionArray = land != null ? new ArrayList<String>(Arrays.asList(
                land.getGovernmentLot() != null ? "Govt Lot " + land.getGovernmentLot().toString() : null,
                land.getDescription40(),
                land.getDescription80(),
                land.getDescription160(),
                land.getDescription320(),
                land.getTrs().getSection() != null ? land.getTrs().getSection().toString() : null,
                land.getTrs().getTownship() != null ? land.getTrs().getTownship().toString() : null,
                land.getTrs().getTownshipDirection(),
                land.getTrs().getRange() != null ? land.getTrs().getRange().toString() : null,
                land.getTrs().getRangeDirection()
        )) : new ArrayList<>();

        descriptionArray.addAll(Arrays.asList(
                county.getName(),
                county.getStateCode()
        ));

        return descriptionArray.stream().filter(part ->
                part != null
        ).collect(Collectors.joining(" "));
    }
}
