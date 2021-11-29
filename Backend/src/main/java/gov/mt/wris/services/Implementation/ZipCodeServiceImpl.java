package gov.mt.wris.services.Implementation;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodePageDto;
import gov.mt.wris.dtos.ZipCodeSortColumn;
import gov.mt.wris.exceptions.DataUsedElsewhereException;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.City;
import gov.mt.wris.models.ZipCode;
import gov.mt.wris.repositories.CityRepository;
import gov.mt.wris.repositories.ZipCodeRepository;
import gov.mt.wris.services.ZipCodeService;

@Service
public class ZipCodeServiceImpl implements ZipCodeService{
    private static Logger LOGGER = LoggerFactory.getLogger(ZipCodeServiceImpl.class); 

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ZipCodeRepository zipRepository;

    @Override
    public ZipCodePageDto getZipCodes(int pagenumber, int pagesize, ZipCodeSortColumn sortDTOColumn, SortDirection sortDirection, String zipCode, String cityName, String stateCode) {
        Pageable request = PageRequest.of(pagenumber-1, pagesize);
        Page<ZipCodeDto> resultsPage = zipRepository.getZipCodes(request, sortDTOColumn, sortDirection, zipCode, cityName, stateCode);

        ZipCodePageDto zipPage = new ZipCodePageDto();

        List<ZipCodeDto> zipCodeLists = resultsPage.getContent();

        zipPage.setResults(zipCodeLists);

        zipPage.setCurrentPage(resultsPage.getNumber() + 1);
        zipPage.setPageSize(resultsPage.getSize());
        
        zipPage.setTotalPages(resultsPage.getTotalPages());
        zipPage.setTotalElements(resultsPage.getTotalElements());

        zipPage.setSortColumn(sortDTOColumn);
        zipPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(zipCode != null) {
            filters.put("zipCode", zipCode);
        }
        if(cityName != null) {
            filters.put("cityName", cityName);
        }
        if(stateCode != null) {
            filters.put("stateCode", stateCode);
        }
        zipPage.setFilters(filters);

        return zipPage;
    }


    @Override
    public ZipCodeDto createZipCode(ZipCodeDto dto) {
        LOGGER.info("Creating a new Zip Code");
        // need to check if it already exists, otherwise save will just update
        if(cityRepository.findByZipCodeAndCityNameAndStateCode(dto.getZipCode(), dto.getCityName(), dto.getStateCode()).size() > 0) {
            throw new DataConflictException("A Zip Code with this city already exists");
        }
        // find or create the city
        City city = findOrCreateCity(dto.getCityName(), dto.getStateCode());

        ZipCode zipCode = new ZipCode();
        zipCode.setZipCode(dto.getZipCode());
        zipCode.setCity(city);
        zipCode.setCityIdSeq(city.getCityId());
        zipCode = zipRepository.save(zipCode);

        return getZipDto(zipCode);
    }

    @Override
    @Transactional
    public void deleteZipCode(Long zipCodeId) {
        // find record to delete
        Optional<ZipCode> optionalZip = zipRepository.findById(BigInteger.valueOf(zipCodeId));
        if(!optionalZip.isPresent()) {
            throw new NotFoundException("This Zip Code could not be found");
        }

        if(zipRepository.existsInAddresses(BigInteger.valueOf(zipCodeId))) {
            throw new DataUsedElsewhereException("This Zip Code has Addresses associated with it");
        }
        try {
	        // delete zip code record
	        zipRepository.delete(optionalZip.get());
	        // delete the city record if not used for anything else
	        cityRepository.deleteByIdIfNoZipCodesOrCases(optionalZip.get().getCityIdSeq().longValue());
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ zipCodeId +" is in use.");
		}
    }

    @Override
    @Transactional
    public ZipCodeDto changeZipCode(Long zipCodeId, ZipCodeDto dto) {
        LOGGER.info("Changing a Zip Code");
        // need to check if it already exists, prevent duplicates
        List<ZipCode> newZipCodes = cityRepository.findByZipCodeAndCityNameAndStateCode(dto.getZipCode(), dto.getCityName(), dto.getStateCode());
        if(newZipCodes.size() > 0) {
            throw new DataConflictException("A Zip Code with this city already exists");
        }

        Optional<ZipCode> possibleZipCode = zipRepository.findById(BigInteger.valueOf(zipCodeId));
        if(!possibleZipCode.isPresent()) {
            throw new NotFoundException("The original Zip Code with this city doesn't exist");
        }
        ZipCode zipCode = possibleZipCode.get();
        City city = zipCode.getCity();

        // check if zip code changed
        if(!zipCode.getZipCode().equals(dto.getZipCode())) {
            zipCode.setZipCode(dto.getZipCode());
        }

        // check if city changed
        // if so, try deleting the old one, then do the normal find or create bit
        String oldCityName = city.getCityName();
        String oldStateCode = city.getStateCode();
        long oldCityId = city.getCityId().longValue();
        if(!oldCityName.equals(dto.getCityName()) ||
            !oldStateCode.equals(dto.getStateCode())) {
            // find or create the city
            city = findOrCreateCity(dto.getCityName(), dto.getStateCode());

            zipCode.setCity(city);
            zipCode.setCityIdSeq(city.getCityId());

            // delete the old city record if not used for anything else
            cityRepository.deleteByIdIfNoZipCodesOrCases(oldCityId);
        }
        zipCode = zipRepository.save(zipCode);

        return getZipDto(zipCode);
    }

    private City findOrCreateCity(String cityName, String stateCode) {
        List<City> cities = cityRepository.findByCityNameAndStateCode(cityName, stateCode);
        City city = new City();
        if(cities.size() > 0) {
            city = cities.get(0);
        } else {
            // if city not found, make sure that the state exists
            city.setCityName(cityName);
            city.setStateCode(stateCode);
            city = cityRepository.save(city);
        }
        return city;
    }

    public ZipCodeDto toUpperCase(ZipCodeDto zipDto) {
        zipDto.setZipCode(zipDto.getZipCode().toUpperCase());
        zipDto.setCityName(zipDto.getCityName().toUpperCase());
        zipDto.setStateCode(zipDto.getStateCode().toUpperCase());
        return zipDto;
    }

    private ZipCodeDto getZipDto(ZipCode model) {
        ZipCodeDto dto = new ZipCodeDto();
        dto.setZipCode(model.getZipCode());
        dto.setCityName(model.getCity().getCityName());
        dto.setStateCode(model.getCity().getStateCode());
        dto.setCityId(model.getCity().getCityId().longValue());
        dto.setId(model.getZipCodeId().longValue());
        return dto;
    }
}
