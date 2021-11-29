package gov.mt.wris.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.services.EndpointService;
import gov.mt.wris.api.EndpointsApiDelegate;

@Controller
public class EndpointsController implements EndpointsApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(EndpointsController.class);

    @Autowired
    private EndpointService endpointService;

    @Override
    public ResponseEntity<Map<String, List<String>>> getEndpoints() {
        LOGGER.info("Get all the End Points");
        Map<String, List<String>> endpoints = endpointService.getEndpoints();
        return ResponseEntity.ok(endpoints);
    }
}
