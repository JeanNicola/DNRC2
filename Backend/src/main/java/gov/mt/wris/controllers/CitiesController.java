package gov.mt.wris.controllers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CitiesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.exceptions.DataUsedElsewhereException;
import gov.mt.wris.repositories.CityRepository;

@Controller
public class CitiesController implements CitiesApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(CitiesController.class);

    @Autowired
    private CityRepository cityRepository;

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.CITY_TABLE)
    })
    public ResponseEntity<Void> deleteCity(Long cityId) {
        LOGGER.info("Deleting an event");

        BigInteger id = BigInteger.valueOf(cityId);
        if(cityRepository.existsByZipCode(id)) {
            throw new DataUsedElsewhereException("This City is being used with a Zip Code");
        }
        if(cityRepository.existsByCourtCase(BigDecimal.valueOf(cityId))) {
            throw new DataUsedElsewhereException("This City is being used in a Water Court Case");
        }

        cityRepository.deleteById(id);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
