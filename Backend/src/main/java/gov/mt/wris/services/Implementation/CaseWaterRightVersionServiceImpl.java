package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CourtCaseVersionXref;
import gov.mt.wris.models.Objection;
import gov.mt.wris.repositories.CourtCaseRepository;
import gov.mt.wris.repositories.CourtCaseVersionXrefRepository;
import gov.mt.wris.repositories.ObjectionsRepository;
import gov.mt.wris.services.CaseWaterRightVersionService;
import gov.mt.wris.utils.Helpers;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaseWaterRightVersionServiceImpl implements CaseWaterRightVersionService {

    private static Logger LOGGER = LoggerFactory.getLogger(CaseWaterRightVersionServiceImpl.class);

    @Autowired
    private CourtCaseVersionXrefRepository courtCaseVersionXrefRepository;

    @Autowired
    private ObjectionsRepository objectionsRepository;

    @Autowired
    private CourtCaseRepository courtCaseRepository;

    public CaseWaterRightVersionsPageDto getCaseWaterRightVersions(int pageNumber, int pageSize, CaseWaterRightVersionsSortColumn sortColumn, SortDirection sortDirection, Long caseId) {

        LOGGER.info("Get Case Water Right Versions");

        Sort sortDtoColumn = getCaseWaterRightVersionsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<CourtCaseVersionXref> results = courtCaseVersionXrefRepository.getCaseWaterRightVersions(request, new BigDecimal(caseId));

        CaseWaterRightVersionsPageDto page = new CaseWaterRightVersionsPageDto();
        page.setResults(results.getContent().stream().map(c -> {
            return getCaseWaterRightVersionDetailDto(c);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getCaseWaterRightVersionsSortColumn(CaseWaterRightVersionsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case COMPLETEWATERRIGHTNUMBER:
                orders.add(new Sort.Order(direction, "wr.basin"));
                orders.add(new Sort.Order(direction, "wr.waterRightNumber"));
                orders.add(new Sort.Order(direction, "wr.ext"));
                break;
            case WATERRIGHTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "wrt.description"));
                break;
            case WATERRIGHTSTATUSDESCRIPTION:
                orders.add(new Sort.Order(direction, "wrs.description"));
                break;
            case COMPLETEVERSION:
                orders.add(new Sort.Order(direction, "vt.meaning"));
                orders.add(new Sort.Order(direction, "wrv.version"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "wr.basin"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "wr.waterRightNumber"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "wr.ext"));
        return Sort.by(orders);

    }

    private CaseWaterRightVersionDetailDto getCaseWaterRightVersionDetailDto(CourtCaseVersionXref model) {

        CaseWaterRightVersionDetailDto dto = new CaseWaterRightVersionDetailDto();
        dto.setWaterRightId(model.getWaterRightId().longValue());
        dto.setVersionId(model.getVersionId().longValue());
        if (model.getWaterRightVersion().getWaterRight()!=null)
            dto.setCompleteWaterRightNumber(Helpers.buildCompleteWaterRightNumber(
                model.getWaterRightVersion().getWaterRight().getBasin(),
                model.getWaterRightVersion().getWaterRight().getWaterRightNumber().toString(),
                model.getWaterRightVersion().getWaterRight().getExt())
            );
        if (model.getWaterRightVersion().getWaterRight().getWaterRightType()!=null) {
            dto.setWaterRightType(model.getWaterRightVersion().getWaterRight().getWaterRightType().getCode());
            dto.setWaterRightTypeDescription(model.getWaterRightVersion().getWaterRight().getWaterRightType().getDescription());
        }
        if (model.getWaterRightVersion().getWaterRight().getWaterRightStatus()!=null) {
            dto.setWaterRightStatus(model.getWaterRightVersion().getWaterRight().getWaterRightStatus().getCode());
            dto.setWaterRightStatusDescription(model.getWaterRightVersion().getWaterRight().getWaterRightStatus().getDescription());
        }
        if (model.getWaterRightVersion().getTypeReference()!=null)
            dto.setCompleteVersion(String.format("%s %s",
                model.getWaterRightVersion().getTypeReference().getMeaning(),
                model.getWaterRightVersion().getVersion()
            ));
        return dto;

    }

    public CaseWaterRightVersionObjectionsPageDto getCaseWaterRightVersionObjections(int pageNumber, int pageSize, CaseWaterRightVersionObjectionsSortColumn sortColumn, SortDirection sortDirection, Long caseId, Long waterRightId, Long versionId) {

        LOGGER.info("Get Case Water Right Version Objections");

        Sort sortDtoColumn = getCaseWaterRightVersionObjectionsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<Objection> results = objectionsRepository.findObjectionsByCaseAndWaterRightVersion(request, new BigDecimal(waterRightId), new BigDecimal(versionId));

        CaseWaterRightVersionObjectionsPageDto page = new CaseWaterRightVersionObjectionsPageDto();
        page.setResults(results.getContent().stream().map(c -> {
            return getCaseWaterRightVersionDetailDto(c);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getCaseWaterRightVersionObjectionsSortColumn(CaseWaterRightVersionObjectionsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case ID:
                orders.add(new Sort.Order(direction, "id"));
                break;
            case OBJECTIONTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "t.meaning"));
                break;
            case DATERECEIVED:
                orders.add(new Sort.Order(direction, "dateReceived"));
                break;
            case LATE:
                orders.add(new Sort.Order(direction, "l.meaning"));
                break;
            case OBJECTIONSTATUSDESCRIPTION:
                orders.add(new Sort.Order(direction, "s.meaning"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
        return Sort.by(orders);

    }

    private WaterRightVersionObjectionsDto getCaseWaterRightVersionDetailDto(Objection model) {

        WaterRightVersionObjectionsDto dto = new WaterRightVersionObjectionsDto();
        dto.setId(model.getId().longValue());
        dto.setObjectionType(model.getType());
        if (model.getTypeReference()!=null)
            dto.setObjectionTypeDescription(model.getTypeReference().getMeaning());
        dto.setStatus(model.getStatus());
        if (model.getStatusReference()!=null)
            dto.setObjectionStatusDescription(model.getStatusReference().getMeaning());
        if (model.getLateReference()!=null)
            dto.setLate(model.getLateReference().getMeaning());
        dto.setDateReceived(model.getDateReceived());
        return dto;

    }

    public CaseWaterRightVersionReferenceDto createCaseWaterRightVersionReference(Long caseId, CaseWaterRightVersionReferenceDto createDto) {

        try {
            return createCaseWaterRightVersionReferenceTransaction(caseId, createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.CVXR_CASE_FK")) {
                    throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
                } else if (constraintMessage.contains("WRD.CVXR_VERS_FK")) {
                    throw new DataIntegrityViolationException(String.format("A water right version does not exist"));
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

    }

    @Transactional
    private CaseWaterRightVersionReferenceDto createCaseWaterRightVersionReferenceTransaction(Long caseId, CaseWaterRightVersionReferenceDto createDto) {

        LOGGER.info("Create Court Case Water Right Version Reference");

        if (courtCaseRepository.countCourtCaseById(new BigDecimal(caseId))==0)
            throw new NotFoundException(String.format("Case or Hearing %s not found",caseId));

        List<CourtCaseVersionXref> xrefs = new ArrayList<>();
        for(WaterRightsReferenceDto dto : createDto.getWaterRightVersions()) {
            List<BigDecimal> caseIds = courtCaseVersionXrefRepository.getWaterRightCaseId(new BigDecimal(dto.getWaterRightId()), new BigDecimal(caseId));
            if (caseIds.size()>0)
                throw new DataIntegrityViolationException(
                    String.format(
                        "Water right number is already in use for case id(s) %s",
                        caseIds.stream().map(n->String.valueOf(n)).collect(Collectors.joining(", "))
                    )
                );
            CourtCaseVersionXref x = new CourtCaseVersionXref();
            x.setCaseId(new BigDecimal(caseId));
            x.setWaterRightId(new BigDecimal(dto.getWaterRightId()));
            x.setVersionId(new BigDecimal(dto.getVersionId()));
            xrefs.add(x);
        }
        return caseWaterRightVersionReferenceDtoLoader(courtCaseVersionXrefRepository.saveAllAndFlush(xrefs));

    }

    private CaseWaterRightVersionReferenceDto caseWaterRightVersionReferenceDtoLoader(List<CourtCaseVersionXref> in) {

        List<WaterRightsReferenceDto> out = in.stream().map(o -> {
            WaterRightsReferenceDto dto = new WaterRightsReferenceDto();
            dto.setWaterRightId(o.getWaterRightId().longValue());
            dto.setVersionId(o.getVersionId().longValue());
            return dto;
        }).collect(Collectors.toList());
        return new CaseWaterRightVersionReferenceDto().waterRightVersions(out);

    }

    public void deleteCaseWaterRightVersionReference(Long caseId, Long waterRightId, Long versionId) {

        LOGGER.info("Delete Court Case Water Right Version Reference");
        courtCaseVersionXrefRepository
            .deleteCourtCaseVersionXrefByCaseIdAndWaterRightIdAndVersionId(
                new BigDecimal(caseId), new BigDecimal(waterRightId), new BigDecimal(versionId)
            );

    }

    public EligibleWaterRightsPageDto getEligibleWaterRights(int pageNumber, int pageSize, EligibleWaterRightsSortColumn sortColumn, SortDirection sortDirection, String waterNumber, String decreeId, String basin) {

        LOGGER.info("Get list of eligible Water Rights for Court Case or Hearing");

        Sort sortDtoColumn = getEligibleWaterRightsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<Object[]> results = courtCaseRepository.getEligibleWaterRights(request, waterNumber, decreeId, basin);

        EligibleWaterRightsPageDto page = new EligibleWaterRightsPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return eligibleWaterRightsPageDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getEligibleWaterRightsSortColumn(EligibleWaterRightsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case COMPLETEWATERRIGHTNUMBER:
                orders.add(new Sort.Order(direction, "WATERRIGHTNUMBER"));
                break;
            case EXT:
                orders.add(new Sort.Order(direction, "EXT"));
                break;
            case WATERRIGHTSTATUSDESCRIPTION:
                orders.add(new Sort.Order(direction, "WATERRIGHTSTATUSDESCRIPTION"));
                break;
            case COMPLETEVERSION:
                orders.add(new Sort.Order(direction, "VERSION"));
                break;
            case BASIN:
                orders.add(new Sort.Order(direction, "BASIN"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "WATERRIGHTNUMBER"));
        return Sort.by(orders);

    }

    private EligibleWaterRightsDto eligibleWaterRightsPageDtoLoader(Object[] model) {

        EligibleWaterRightsDto dto = new EligibleWaterRightsDto();
        dto.setWaterRightNumber(model[0]!=null?((BigDecimal) model[0]).longValue():null);
        dto.setExt(model[1]!=null?(String)model[1]:null);
        dto.setVersionId(model[2]!=null?((BigDecimal) model[2]).longValue():null);
        dto.setBasin(model[3]!=null?(String)model[3]:null);
        dto.setWaterRightId(model[4]!=null?((BigDecimal) model[4]).longValue():null);
        dto.setWaterRightStatusDescription(model[6]!=null?(String)model[6]:null);
        if (model[0]!=null && model[1]!=null && model[3]!=null)
            dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                    (String)model[3],
                    ((BigDecimal) model[0]).toString(),
                    (String)model[1])
            );
        if (model[2]!=null && model[7]!=null)
            dto.setCompleteVersion(
                String.format(
                    "%s - %s",
                    (String)model[7],
                    ((BigDecimal) model[2]).toString())
            );
        return dto;

    }

}
