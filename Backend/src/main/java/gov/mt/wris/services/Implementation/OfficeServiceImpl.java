package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllOfficesDto;
import gov.mt.wris.dtos.OfficeDropdownDto;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.Office;
import gov.mt.wris.models.OfficeCustomer;
import gov.mt.wris.repositories.OfficeRepository;
import gov.mt.wris.services.OfficeService;
import gov.mt.wris.utils.Helpers;

@Service
public class OfficeServiceImpl implements OfficeService {
    private static Logger LOGGER = LoggerFactory.getLogger(OfficeServiceImpl.class);

    @Autowired
    private OfficeRepository officeRepo;

    public AllOfficesDto getAllOffices() {
        LOGGER.info("Getting a list of all the offices");

        List<Office> allOffices = officeRepo.findAllByOrderByDescriptionAsc();

        AllOfficesDto dto = new AllOfficesDto();
        dto.setResults(allOffices.stream().map(office -> {
            return getOfficeDto(office);
        }).collect(Collectors.toList()));

        return dto;
    }

    private OfficeDropdownDto getOfficeDto(Office model) {
        OfficeDropdownDto dto = new OfficeDropdownDto();
        dto.setDescription(model.getDescription());
        dto.setOfficeId(model.getId().longValue());
        return dto;
    }

    public AllOfficesDto getAllRegionalOffices() {
        LOGGER.info("Getting a list of all the Regional Offices");

        List<Office> allOffices = officeRepo.findAllRegionalOffices();

        AllOfficesDto dto = new AllOfficesDto();
        dto.setResults(allOffices.stream().map(office -> {
            return getOfficeDto(office);
        }).collect(Collectors.toList()));

        return dto;
    }
}
