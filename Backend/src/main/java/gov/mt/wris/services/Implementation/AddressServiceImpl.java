package gov.mt.wris.services.Implementation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import gov.mt.wris.dtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Address;
import gov.mt.wris.repositories.AddressRepository;
import gov.mt.wris.services.AddressService;
import gov.mt.wris.utils.Helpers;

@Service
public class AddressServiceImpl implements AddressService {

    private static Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    AddressRepository addressRepository;

    @Override
    public AddressSearchPageDto searchAddresses(int pageNumber, int pageSize, AddressSortColumn sortColumn, SortDirection sortDirection, Long customerId) {

        LOGGER.info("Getting a Page of Addresses");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<Address> resultsPage = addressRepository.searchAddresses(
                pageable, sortColumn, sortDirection, customerId);
        AddressSearchPageDto page = new AddressSearchPageDto();

        page.setResults(resultsPage.getContent().stream().map(address -> {
            return getAddressSearchResultDto(address);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(customerId != null) {
            filters.put("customerId", customerId.toString());
        }

        page.setFilters(filters);
        return page;

    }

    @Override
    public AddressDto getAddress(Long customerId, Long addressId) {

        LOGGER.info("Getting Address");

        Optional<Address> foundAddress = addressRepository.getAddressByAddressId(BigInteger.valueOf(addressId));
        if (!foundAddress.isPresent())
            throw new NotFoundException("Address with id " + addressId + " does not exist");

        return getAddressDto(foundAddress);

    }

    @Override
    public AddressDto createAddress(Long customerId, AddressCreationDto newAddress) {

        LOGGER.info("Creating Address");

        newAddress.setCustomerId(customerId);
        Address address = getAddress(newAddress);
        Address saved = addressRepository.saveAndFlush(address);

        Optional<Address> foundAddress = addressRepository.getAddressByAddressId(saved.getAddressId());
        if(!foundAddress.isPresent())
            throw new DataIntegrityViolationException("Address with id " + saved.getAddressId() + " after save was not found.");

        // Is address just created the new primary address?
        if (foundAddress.get().getPrimaryMail().equals("Y"))
            resetPrimaryAddress(customerId, foundAddress.get().getAddressId().longValue());

        return getAddressDto(foundAddress);

    }

    @Override
    public AddressDto changeAddress(Long customerId, Long addressId, AddressUpdateDto updateAddress) {

        LOGGER.info("Changing Address");

        Optional<Address> foundAddress = addressRepository.getAddressByAddressId(BigInteger.valueOf(addressId));
        if(!foundAddress.isPresent())
            throw new DataIntegrityViolationException("Address with id " + addressId + " was not found.");

        // Is address being updated new primary address?
        if (foundAddress.get().getPrimaryMail().equals("N") && updateAddress.getPrimaryMail().equals("Y"))
            resetPrimaryAddress(foundAddress.get().getCustomerId().longValue(), addressId);

        // setup for save
        updateAddress.setCustomerId(customerId);
        updateAddress.setAddressId(addressId);
        Address address = getAddress(updateAddress);
        Address savedAddress = addressRepository.saveAndFlush(address);
        return getAddressDto(savedAddress);

    }

    @Override
    public void deleteAddress(Long customerId, Long addressId) {

        LOGGER.info("Deleting Address");

        Optional<Address> foundAddress = addressRepository.getAddressByAddressId(BigInteger.valueOf(addressId));
        if(!foundAddress.isPresent())
            throw new NotFoundException( String.format("Address %s for customer %s not found.", addressId, customerId));
        if (foundAddress.get().getPrimaryMail().equals("Y"))
            throw new DataIntegrityViolationException("Unable to delete primary Address id " + addressId + ".");
        addressRepository.deleteById(BigInteger.valueOf(addressId));

    }

    private AddressSearchResultDto getAddressSearchResultDto(Address address) {

        AddressSearchResultDto dto = new AddressSearchResultDto();

        dto.setAddressId(Long.valueOf(address.getAddressId().toString()));
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setAddressLine3(address.getAddressLine3());
        dto.setCustomerId(Long.valueOf(address.getCustomerId().toString()));
        dto.setCreatedBy(address.getCreatedBy());
        if (address.getCreatedByName()!=null)
           dto.setCreatedByValue(address.getCreatedByName().getFirstName() + " " + address.getCreatedByName().getLastName());
        dto.setCreatedDate(address.getDateCreated());
        dto.setModifiedBy(address.getModifiedBy());
        if (address.getModifiedByName()!=null)
            dto.setModifiedByValue(address.getModifiedByName().getFirstName() + " " + address.getModifiedByName().getLastName());
        dto.setModifiedDate(address.getDateModified());
        dto.setModReason(address.getModifiedReason());
        dto.setPl4(address.getPlFour());
        if (address.getUnresolvedFlagValue()!=null)
            dto.setUnresolvedFlagValue(address.getUnresolvedFlagValue().getMeaning());
        dto.setUnresolvedFlag(address.getUnresolvedFlag());
        if (address.getPrimaryMailValue()!=null)
            dto.setPrimaryMailValue(address.getPrimaryMailValue().getMeaning());
        dto.setPrimaryMail(address.getPrimaryMail());
        if (address.getForeignAddressValue()!=null)
            dto.setForeignAddressValue(address.getForeignAddressValue().getMeaning());
        dto.setForeignAddress(address.getForeignAddress());
        dto.setForeignPostal(address.getForeignPostal());
        /* foreign address won't have zip id */
        if (address.getForeignAddress()==null || address.getForeignAddress().equals("N")) {
            if (address.getZipCode()!=null) {
                dto.setZipCodeId(address.getZipCode().getZipCodeId().longValueExact());
                dto.setCityId(address.getZipCode().getCityIdSeq().longValueExact());
                dto.setZipCode(address.getZipCode().getZipCode());
                dto.setCityName(address.getZipCode().getCity().getCityName());
                dto.setStateCode(address.getZipCode().getCity().getState().getCode());
                dto.setStateName(address.getZipCode().getCity().getState().getName());

            }
        }
        return dto;

    }

    private AddressDto getAddressDto(Optional<Address> address) {
        return getAddressDto(address.get());
    }

    private AddressDto getAddressDto(Address address) {

        AddressDto dto = new AddressDto();
        dto.setAddressId(Long.valueOf(address.getAddressId().toString()));
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setAddressLine3(address.getAddressLine3());
        dto.setCustomerId(Long.valueOf(address.getCustomerId().toString()));
        dto.setCreatedBy(address.getCreatedBy());
        if (address.getCreatedByName()!=null)
            dto.setCreatedByValue(address.getCreatedByName().getFirstName() + " " + address.getCreatedByName().getLastName());
        dto.setCreatedDate(address.getDateCreated());
        dto.setModifiedBy(address.getModifiedBy());
        if (address.getModifiedByName()!=null)
            dto.setModifiedByValue(Helpers.buildName(address.getModifiedByName().getLastName(), address.getModifiedByName().getFirstName()));
        dto.setModifiedDate(address.getDateModified());
        dto.setModReason(address.getModifiedReason());
        dto.setPl4(address.getPlFour());
        if (address.getUnresolvedFlagValue()!=null)
            dto.setUnresolvedFlagValue(address.getUnresolvedFlagValue().getMeaning());
        dto.setUnresolvedFlag(address.getUnresolvedFlag());
        if (address.getPrimaryMailValue()!=null)
            dto.setPrimaryMailValue(address.getPrimaryMailValue().getMeaning());
        dto.setPrimaryMail(address.getPrimaryMail());
        if (address.getForeignAddressValue()!=null)
            dto.setForeignAddressValue(address.getForeignAddressValue().getMeaning());
        dto.setForeignAddress(address.getForeignAddress());
        dto.setForeignPostal(address.getForeignPostal());
        /* foreign address won't have zip id */
        if (address.getForeignAddress()==null || address.getForeignAddress().equals("N")) {
            if (address.getZipCode()!=null) {
                dto.setZipCodeId(address.getZipCode().getZipCodeId().longValueExact());
                dto.setCityId(address.getZipCode().getCityIdSeq().longValueExact());
                dto.setZipCode(address.getZipCode().getZipCode());
                dto.setCityName(address.getZipCode().getCity().getCityName());
                dto.setStateCode(address.getZipCode().getCity().getState().getCode());
                dto.setStateName(address.getZipCode().getCity().getState().getName());
            }
        }
        return dto;

    }

    private Address getAddress(AddressCreationDto dto) {

        // Creation does not have mod reason...
        Address address = new Address();
        address.setCustomerId(BigInteger.valueOf(dto.getCustomerId()));
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setAddressLine3(dto.getAddressLine3());
        address.setPlFour(dto.getPl4());
        address.setForeignPostal(dto.getForeignPostal());
        address.setPrimaryMail(dto.getPrimaryMail());
        address.setUnresolvedFlag(dto.getUnresolvedFlag());
        address.setForeignAddress(dto.getForeignAddress());
        address.setModifiedReason(dto.getModReason());
        if (dto.getZipCodeId() != null)
            // foreign address should be null or "N" at this point
            address.setZipCodeId(BigInteger.valueOf(dto.getZipCodeId()));

        return address;

    }

    private Address getAddress(AddressUpdateDto dto) {

        Address address = new Address();
        address.setAddressId(BigInteger.valueOf(dto.getAddressId()));
        address.setCustomerId(BigInteger.valueOf(dto.getCustomerId()));
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setAddressLine3(dto.getAddressLine3());
        address.setPlFour(dto.getPl4());
        address.setForeignPostal(dto.getForeignPostal());
        address.setPrimaryMail(dto.getPrimaryMail());
        address.setModifiedReason(dto.getModReason());
        address.setUnresolvedFlag(dto.getUnresolvedFlag());
        address.setForeignAddress(dto.getForeignAddress());
        address.setModifiedReason(dto.getModReason());
        if (dto.getZipCodeId() != null)
            address.setZipCodeId(BigInteger.valueOf(dto.getZipCodeId()));

        return address;

    }

    private void resetPrimaryAddress(Long customerId, long newPrimaryAddressId) {

        List<Address> addresses = addressRepository.findAllByCustomerId(BigInteger.valueOf(customerId));
        List<Address> addressesToChange = new ArrayList<>();
        addresses.forEach((a)-> {
            if (a.getAddressId().longValue() != newPrimaryAddressId) {
                a.setPrimaryMail("N");
                addressesToChange.add(a);
            }
        });
        addressRepository.saveAll(addressesToChange);

    }

}
