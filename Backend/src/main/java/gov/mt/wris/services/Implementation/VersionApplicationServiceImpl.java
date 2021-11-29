package gov.mt.wris.services.Implementation;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesPageDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesSortColumn;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.VersionApplicationXref;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.VersionApplicationXrefRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.VersionApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.Optional;

@Service
public class VersionApplicationServiceImpl implements VersionApplicationService {
    private static Logger LOGGER = LoggerFactory.getLogger(VersionApplicationService.class);

    @Autowired
    VersionApplicationXrefRepository versionXrefRepo;

    @Autowired
    WaterRightVersionRepository versionRepo;

    @Autowired
    ApplicationRepository appRepo;

    public WaterRightVersionApplicationReferencesPageDto getWaterRightVersionApplicationReferences(int pagenumber, int pagesize, WaterRightVersionApplicationReferencesSortColumn sortColumn, SortDirection sortDirection, Long waterRightId, Long versionNumber) {

        LOGGER.info("Get Application references for a Water Right Version");

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);

        Page<WaterRightVersionApplicationReferencesDto> results = versionRepo.getWaterRightVersionApplicationReferences(pageable, sortColumn, sortDirection, waterRightId, versionNumber);

        WaterRightVersionApplicationReferencesPageDto page = new WaterRightVersionApplicationReferencesPageDto();
        page.setResults(results.getContent());

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    @Transactional
    public WaterRightVersionApplicationReferencesDto addApplicationReferenceToWaterRightVersion(Long waterRightId, Long versionNumber, Long applicationId) {

        LOGGER.info("Add Application reference to a Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);
        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<VersionApplicationXref> foundXref =
           versionXrefRepo
               .getVersionApplicationXrefByWaterRightIdAndVersionIdAndApplicationId(
                   waterId,
                   version,
                   appId
               );
        if(foundXref.isPresent()) {
            throw new DataIntegrityViolationException(
                String.format("Unable to create application reference, reference with water right id %s, version number %s and application id %s currently exists.",
                        waterRightId, versionNumber, applicationId));
        }

        Optional<Application> foundApp = appRepo.findById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException(String.format("Application with application id %s was not found.", applicationId));
        }
        Application app = foundApp.get();

        if("606".equals(app.getTypeCode()) && versionNumber == 1) {
            throw new DataIntegrityViolationException("Change Applications cannot be associated with the first version");
        }

        if("634".equals(app.getTypeCode())) {
            int majorTypeCount = versionRepo.countPODByMajorType(waterId, version, "G");
            if(majorTypeCount < 1) {
                throw new DataIntegrityViolationException("This Application cannot be associated with a Surface Water Right");
            }
            int wellCount = versionRepo.countPODByWellCode(waterId, version);
            if(wellCount < 1) {
                throw new DataIntegrityViolationException("This Application must be associated with a Water Right that has a Well");
            }
        } else if("644".equals(app.getTypeCode())) {
            int majorTypeCount = versionRepo.countPODByMajorType(waterId, version, "S");
            if(majorTypeCount < 1) {
                throw new DataIntegrityViolationException("This Application cannot be associated with a Groundwater Water Right");
            }
        }

        VersionApplicationXref xref = new VersionApplicationXref();
        xref.setWaterRightId(new BigDecimal(waterRightId));
        xref.setVersionId(new BigDecimal(versionNumber));
        xref.setApplicationId(new BigDecimal(applicationId));

        VersionApplicationXref model;
        try {
            model = versionXrefRepo.save(xref);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof javax.validation.ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("VAXR_APPL_FK")) {
                    throw new NotFoundException(String.format("Application with application id %s was not found.", applicationId));
                } else if(constraintMessage.contains("VAXR_VERS_FK")) {
                    throw new NotFoundException(String.format("Water right id %s and version id %s combination was not found.", waterRightId, versionNumber));
                }
            }
            throw e;
        }

        return getWaterRightVersionApplicationReferencesDto(model);

    }

    private WaterRightVersionApplicationReferencesDto getWaterRightVersionApplicationReferencesDto(VersionApplicationXref model) {
        WaterRightVersionApplicationReferencesDto dto = new WaterRightVersionApplicationReferencesDto();
        dto.setApplicationId(model.getApplicationId().longValue());
        if(model.getApplication() != null) {
            dto.setBasin(model.getApplication().getBasin());
            if(model.getApplication().getDateTimeReceivedEvent() != null) dto.setDateTimeReceived(model.getApplication().getDateTimeReceivedEvent().getEventDate());
        }
        return dto;
    }

    public void deleteApplicationReferenceToWaterRightVersion(Long waterRightId, Long versionNumber, Long applicationId) {

        LOGGER.info("Delete Application reference to a Water Right Version");

        if ((versionXrefRepo.deleteVersionApplicationXrefByWaterRightIdAndVersionIdAndApplicationId(new BigDecimal(waterRightId), new BigDecimal(versionNumber), new BigDecimal(applicationId))) !=1 )
            throw new DataIntegrityViolationException(String.format("Unable to delete application reference %s for water right id %s, version number %s.", applicationId, waterRightId, versionNumber));

    }

}
