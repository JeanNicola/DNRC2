package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.CaseAllReferencesDto;
import gov.mt.wris.dtos.CaseReferenceDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.models.CaseType;
import gov.mt.wris.models.ClimaticArea;
import gov.mt.wris.models.CustomerTypes;
import gov.mt.wris.models.ElementType;
import gov.mt.wris.models.IrrigationType;
import gov.mt.wris.models.PurposeType;
import gov.mt.wris.models.Reference;
import gov.mt.wris.repositories.CaseTypeRepository;
import gov.mt.wris.repositories.ClimaticAreaRepository;
import gov.mt.wris.repositories.CustomerRepository;
import gov.mt.wris.repositories.CustomerTypesRepository;
import gov.mt.wris.repositories.ElementTypeRepository;
import gov.mt.wris.repositories.IrrigationTypeRepository;
import gov.mt.wris.repositories.PurposeTypeRepository;
import gov.mt.wris.repositories.ReferenceRepository;
import gov.mt.wris.services.ReferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReferenceServiceImpl implements ReferenceService{
    private static Logger LOGGER = LoggerFactory.getLogger(ReferenceService.class);
    enum ListType {CONTACT_STATUS, SUFFIX}

    @Autowired
    private ReferenceRepository refRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerTypesRepository customerTypesRepository;

    @Autowired
    private ElementTypeRepository elementTypeRepository;

    @Autowired
    private ClimaticAreaRepository climaticAreaRepository;

    @Autowired
    private PurposeTypeRepository purposeTypeRepository;

    @Autowired
    private IrrigationTypeRepository irrigationTypeRepository;

    @Autowired
    private CaseTypeRepository caseTypeRepository;

    @Override
    public AllReferencesDto findAllProgramsByTable(String domain){
        LOGGER.info("Finding the References for " + domain);

        AllReferencesDto allReferences = new AllReferencesDto();

        allReferences.setResults(refRepository.findAllByDomain(domain).stream().map(program -> {
            return convertToPair(program);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    @Override
    public AllReferencesDto findAllProgramsByTableOrderByMeaning(String domain){
        LOGGER.info("Finding the References for " + domain + " ordered by meaning");

        AllReferencesDto allReferences = new AllReferencesDto();

        allReferences.setResults(refRepository.findAllByDomainOrderByMeaningAsc(domain).stream().map(program -> {
            return convertToPair(program);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    public AllReferencesDto findAllProgramsByTableOrderByCode(String domain){
        LOGGER.info("Finding the References for " + domain + " ordered by value");

        AllReferencesDto allReferences = new AllReferencesDto();

        allReferences.setResults(refRepository.findAllByDomainOrderByValueAsc(domain).stream().map(program -> {
            return convertToPair(program);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    @Override
    public AllReferencesDto findAllProgramsByTableOrderByLowValue(String domain) {

        LOGGER.info("Finding the References for " + domain + " ordered by low value");

        AllReferencesDto allReferences = new AllReferencesDto();

        allReferences.setResults(refRepository.findAllByDomainOrderByValueAsc(domain).stream().map(program -> {
            return convertToPair(program);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    @Override
    public AllReferencesDto findDistinctContactType() {
        LOGGER.info("Find list of contact (customer) types");

        AllReferencesDto allReferences = new AllReferencesDto();
        allReferences.setResults(customerTypesRepository.findAllCustomerTypes().stream().map(types -> {
            return convertToPair(types);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    @Override
    public AllReferencesDto findElectronicContactTypes(String domain) {
        AllReferencesDto allReferences = new AllReferencesDto();

        allReferences.setResults(refRepository.findAllByDomain(domain).stream().map(type -> {
            return convertToPair(type);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    @Override
    public AllReferencesDto findElementType() {
        LOGGER.info("Find list of related SharedElement types");

        AllReferencesDto allReferences = new AllReferencesDto();
        allReferences.setResults(elementTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).stream().map(types -> {
            return convertToPair(types);
        }).collect(Collectors.toList()));

        return allReferences;
    }

    @Override
    public AllReferencesDto findAllClimaticAreas() {

        LOGGER.info("Find list of climatic areas");
        AllReferencesDto allReferences = new AllReferencesDto();
        allReferences.setResults(climaticAreaRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).stream().map(types -> {
            return convertToPair(types);
        }).collect(Collectors.toList()));
        return allReferences;

    }

    @Override
    public AllReferencesDto findAllPurposeTypes() {

        LOGGER.info("Find list of purpose types");
        AllReferencesDto allReferences = new AllReferencesDto();
        allReferences.setResults(purposeTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).stream().map(types -> {
            return convertToPair(types);
        }).collect(Collectors.toList()));
        return allReferences;

    }

    @Override
    public AllReferencesDto findAllIrrigationTypes() {

        LOGGER.info("Find list of irrigation types");
        AllReferencesDto allReferences = new AllReferencesDto();
        allReferences.setResults(irrigationTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).stream().map(types -> {
            return convertToPair(types);
        }).collect(Collectors.toList()));
        return allReferences;

    }

    public CaseAllReferencesDto findCaseTypes(Integer supported) {

        LOGGER.info("Find list of case types");
        CaseAllReferencesDto allReferences = new CaseAllReferencesDto();
        if (supported==0) {
            /* return all case types */
            List<String> unsupportedCodes = Arrays.asList("SCHD");
            allReferences.setResults(caseTypeRepository.findAllByCodeNotInOrderByDescriptionAsc(unsupportedCodes).stream().map(types -> {
                return convertToPair(types);
            }).collect(Collectors.toList()));
        } else {
            /* return only supported case types */
            List<String> unsupportedCodes = Arrays.asList("OBJL", "ISRO", "MOTA", "OACT", "SPLT", "DCC", "AWC", "EXPN", "SCHD");
            allReferences.setResults(caseTypeRepository.findAllByCodeNotInOrderByDescriptionAsc(unsupportedCodes).stream()
                    .map(types -> { return convertToPair(types); })
                    .collect(Collectors.toList()));
        }
        return allReferences;

    }

    public AllReferencesDto findObjectionTypes(Integer supported) {

        LOGGER.info("Find list of objection types");
        AllReferencesDto allReferences = new AllReferencesDto();
        if (supported != null && supported==0) {
            /* return all objection types */
            allReferences.setResults(refRepository.findAllByDomainOrderByMeaningAsc(Constants.OBJECTIONS_TYPE_DOMAIN).stream().map(types -> {
                return convertToPair(types);
            }).collect(Collectors.toList()));
        } else {
            /* return only supported objection types */
            List<String> unsupportedTypes = Arrays.asList("CET", "IPY", "INT");
            allReferences.setResults(refRepository.findAllByDomainAndValueNotInOrderByMeaningAsc(Constants.OBJECTIONS_TYPE_DOMAIN, unsupportedTypes).stream()
                    .map(types -> { return convertToPair(types); })
                    .collect(Collectors.toList()));
        }
        return allReferences;

    }

    private ReferenceDto convertToPair(Reference model) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setDescription(model.getMeaning());
        newReference.setValue(model.getValue());
        return newReference;
    }

    private ReferenceDto convertToPair(CustomerTypes model) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setValue(model.getCode());
        newReference.setDescription(model.getDescription());
        return newReference;
    }

    private ReferenceDto convertToPair(ElementType model) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setValue(model.getCode());
        newReference.setDescription(model.getDescription());
        return newReference;
    }

    private ReferenceDto convertToPair(ClimaticArea model) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setValue(model.getCode());
        newReference.setDescription(model.getDescription());
        return newReference;
    }

    private ReferenceDto convertToPair(PurposeType model) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setValue(model.getCode());
        newReference.setDescription(model.getDescription());
        return newReference;
    }

    private ReferenceDto convertToPair(IrrigationType model) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setValue(model.getCode());
        newReference.setDescription(model.getDescription());
        return newReference;
    }

    private ReferenceDto convertToPair(String value, String description) {
        ReferenceDto newReference = new ReferenceDto();
        newReference.setValue(value);
        newReference.setDescription(description);
        return newReference;

    }
    private CaseReferenceDto convertToPair(CaseType model) {
        CaseReferenceDto newReference = new CaseReferenceDto();
        newReference.setValue(model.getCode());
        newReference.setDescription(model.getDescription());
        newReference.setProgram(model.getProgram());
        return newReference;
    }

    @Override
    public AllReferencesDto getReportUrl(String env) {
        LOGGER.info("Finding Report url");

        AllReferencesDto response = new AllReferencesDto();
        String domain = "WEB_URL";
        String regularLowValue = "SSRS_REPORT_PREFIX";
        String scannedLowValue = "FILENET_PREFIX";

        Reference regularReportRef = refRepository.findReportUrlWithAbbreviation(domain, regularLowValue, env);
        Reference scannedReportRef = refRepository.findReportUrl(domain, scannedLowValue);;

        response.addResultsItem(convertToPair(regularReportRef));
        response.addResultsItem(convertToPair(scannedReportRef));

        return response;
    }


    public AllReferencesDto findAllMaxVolumeDescriptions() {
        LOGGER.info("Finding all Max Volume Descriptions");

        AllReferencesDto response = new AllReferencesDto();

        response.setResults(
                Arrays.asList(
                        convertToPair("VF009", "THE TOTAL VOLUME OF THIS WATER RIGHT SHALL NOT EXCEED THE AMOUNT PUT TO HISTORICAL AND BENEFICIAL USE."),
                        convertToPair("VF010", "THIS WATER RIGHT INCLUDES THE AMOUNT OF WATER CONSUMPTIVELY USED FOR STOCK WATERING PURPOSES AT THE RATE OF 30 GALLONS PER DAY PER ANIMAL UNIT. ANIMAL UNITS SHALL BE BASED ON REASONABLE CARRYING CAPACITY AND HISTORICAL USE OF THE AREA SERVICED BY THIS WATER SOURCE."),
                        convertToPair("VF011", "THIS WATER RIGHT IS LIMITED TO THE VOLUME OF WATER HISTORICALLY USED FOR MINING PURPOSES."),
                        convertToPair("VF012", "THE FLOW RATE AND VOLUME ARE LIMITED TO THE MINIMUM AMOUNT NECESSARY TO SUSTAIN THIS PURPOSE. THIS RIGHT SHALL CONTINUE TO BE UTILIZED IN ACCORDANCE WITH HISTORICAL PRACTICES."),
                        convertToPair("VF013", "THIS FLOW RATE AND VOLUME OF THIS WATER RIGHT ARE LIMITED TO THE MINIMUM AMOUNTS NECESSARY FOR FIRE PROTECTION PURPOSES."),
                        convertToPair("VF014", "THE VOLUME OF THIS WATER RIGHT IS LIMITED TO THE MINIMUM AMOUNTS NECESSARY FOR FIRE PROTECTION PURPOSES."),
                        convertToPair("VF015", "THIS RIGHT INCLUDES THE AMOUNT OF WATER CONSUMPTIVELY USED FOR STOCKWATERING PURPOSES AT THE RATE OF 30 GALLONS PER DAY PER ANIMAL UNIT. ANIMAL UNITS SHALL BE BASED ON REASONABLE CARRYING CAPACITY AND HISTORICAL USE OF THE AREA SERVICED BY THIS WATER SOURCE."),
                        convertToPair("VF016", "THIS RIGHT IS LIMITED TO THE VOLUME OF WATER HISTORICALLY USED FOR MINING PURPOSES."),
                        convertToPair("V9", "PRIMARILY A DIRECT FLOW SYSTEM; VOLUME NOT DECREED.")
                )
        );

        return response;
    }

}
