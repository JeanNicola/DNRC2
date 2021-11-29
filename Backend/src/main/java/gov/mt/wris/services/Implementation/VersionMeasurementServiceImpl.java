package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

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

import gov.mt.wris.dtos.CommentDto;
import gov.mt.wris.dtos.CommentsPageDto;
import gov.mt.wris.dtos.MeasurementDto;
import gov.mt.wris.dtos.MeasurementSortColumn;
import gov.mt.wris.dtos.MeasurementsPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.VersionMeasurementReportDto;
import gov.mt.wris.dtos.VersionMeasurementReportSortColumn;
import gov.mt.wris.dtos.VersionMeasurementReportsPageDto;
import gov.mt.wris.dtos.CommentDto.CommentTypeEnum;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.VersionRemark;
import gov.mt.wris.models.RemarkElement;
import gov.mt.wris.models.Variable;
import gov.mt.wris.models.VersionRemarkMeasurement;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.RemarkElementRepository;
import gov.mt.wris.repositories.VersionRemarkMeasurementRepository;
import gov.mt.wris.repositories.VersionRemarkRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.VersionMeasurementService;

@Service
public class VersionMeasurementServiceImpl implements VersionMeasurementService {
    private static Logger LOGGER = LoggerFactory.getLogger(VersionMeasurementService.class);

    @Autowired
    public VersionRemarkRepository remarkRepository;

    @Autowired
    public RemarkElementRepository elementRepository;

    @Autowired
    public VersionRemarkMeasurementRepository measurementRepository;

    @Autowired
    public WaterRightVersionRepository versionRepository;

    public VersionMeasurementReportsPageDto getMeasurementReports(Long waterRightId,
        Long versionId,
        int pagenumber,
        int pagesize,
        VersionMeasurementReportSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Measurement Reports");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionId);

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getMeasurementReportSortColumn(sortColumn)).and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        Page<VersionRemark> results = remarkRepository.findMeasurementReports(pageable, waterId, version);

        VersionMeasurementReportsPageDto dto = new VersionMeasurementReportsPageDto();

        dto.setResults(results.getContent().stream()
            .map(remark -> getVersionMeasurementDto(remark))
            .collect(Collectors.toList()));
        
        dto.setCurrentPage(results.getNumber());
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private String getMeasurementReportSortColumn(VersionMeasurementReportSortColumn sortColumn) {
        switch(sortColumn) {
            case REMARKCODE:
                return "remarkCode";
            case REPORTTYPEDESCRIPTION:
                return "rt.description";
            case EFFECTIVEDATE:
                return "date";
            case ENDDATE:
                return "endDate";
            default:
                return "remarkCode";
        }
    }

    private VersionMeasurementReportDto getVersionMeasurementDto(VersionRemark remark) {
        return new VersionMeasurementReportDto()
            .remarkId(remark.getId().longValue())
            .remarkCode(remark.getRemarkCode())
            .reportTypeCode(remark.getReportTypeCode())
            .reportTypeDescription(remark.getReportType() != null ? remark.getReportType().getDescription() : null)
            .effectiveDate(remark.getDate())
            .endDate(remark.getEndDate());
    }

    @Transactional
    public VersionMeasurementReportDto createMeasurementReport(Long waterRightId,
        Long versionNumber,
        VersionMeasurementReportDto dto
    ) {
        LOGGER.info("Creating a new Measurement Report");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        validateDto(dto);

        int activeCount = remarkRepository.countByWaterRightIdAndVersionAndRemarkCodeAndTypeIndicatorAndEndDateIsNull(waterId, version, dto.getRemarkCode(), "C");
        if(activeCount > 0 && dto.getEndDate() == null) {
            throw new ValidationException("Must end date all other Measurement Reports with Remark Code '" + dto.getRemarkCode() + "' before adding a new one without an end date");
        }

        VersionRemark report = getVersionRemark(dto, waterId, version);
        report.setTypeIndicator("C");

        try {
            report = remarkRepository.save(report);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("FRWR_RPTP_FK")) {
                    throw new NotFoundException(String.format("This Report Type does not exist: %s", dto.getReportTypeCode()));
                } else if(constraintMessage.contains("FRWR_FRLB_FK")) {
                    throw new NotFoundException(String.format("This Remark Code does not exist: %s", dto.getRemarkCode()));
                } else if(constraintMessage.contains("FRWR_VERS_FK")) {
                    throw new NotFoundException("This Water Right Version does not exist");
                }
            }
            throw e;
        }

        elementRepository.createVariables(report.getId());

        return getVersionMeasurementDto(report);
    }

    private VersionRemark getVersionRemark(VersionMeasurementReportDto dto, BigDecimal waterRightId, BigDecimal version) {
        VersionRemark model = new VersionRemark();
        model.setDate(dto.getEffectiveDate());
        model.setEndDate(dto.getEndDate());
        model.setRemarkCode(dto.getRemarkCode());
        model.setReportTypeCode(dto.getReportTypeCode());
        model.setVersion(version);
        model.setWaterRightId(waterRightId);
        return model;
    }

    public VersionMeasurementReportDto updateMeasurementReport(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        VersionMeasurementReportDto dto
    ) {
        LOGGER.info("Creating a new Measurement Report");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);
        BigDecimal id = BigDecimal.valueOf(remarkId); 

        validateDto(dto);

        Optional<VersionRemark> foundRemark = remarkRepository.findById(id);
        if(!foundRemark.isPresent()) {
            throw new NotFoundException("This Report does not exist");
        }
        VersionRemark report = foundRemark.get();

        int activeCount = remarkRepository.countByWaterRightIdAndVersionAndRemarkCodeAndTypeIndicatorAndEndDateIsNull(waterId, version, dto.getRemarkCode(), "C");
        if(activeCount > 0 && report.getEndDate() != null && dto.getEndDate() == null) {
            throw new ValidationException("Must end date all other Measurement Reports with Remark Code " + dto.getRemarkCode() + " before adding a new one without an end date");
        }

        report.setDate(dto.getEffectiveDate());
        report.setEndDate(dto.getEndDate());
        report.setRemarkCode(dto.getRemarkCode());
        report.setReportTypeCode(dto.getReportTypeCode());

        try {
            report = remarkRepository.save(report);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("FRWR_RPTP_FK")) {
                    throw new NotFoundException(String.format("This Report Type does not exist: %s", dto.getReportTypeCode()));
                } else if(constraintMessage.contains("FRWR_FRLB_FK")) {
                    throw new NotFoundException(String.format("This Remark Code does not exist: %s", dto.getRemarkCode()));
                }
            }
            throw e;
        }
        return getVersionMeasurementDto(report);
    }

    private void validateDto(VersionMeasurementReportDto dto) {
        if(dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getEffectiveDate())) {
            throw new ValidationException("The End Date must be on or after the Effective Date");
        }

        if(dto.getEndDate() != null && dto.getEndDate().isAfter(LocalDate.now())) {
            throw new ValidationException("The End Date cannot be in the future");
        }

    }

    public void deleteMeasurementReport(Long remarkId) {
        try {
            this._deleteMeasurementReport(remarkId);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("CNRP_FRVX_FK")) {
                    throw new DataConflictException("This Report has Measurements attached. Delete them first");
                }
            }
        }
    }


    @Transactional
    private void _deleteMeasurementReport(Long remarkId) {
        LOGGER.info("Deleting a Measurement Report");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        elementRepository.deleteByRemarkId(id);

        remarkRepository.deleteById(id);
    }

    public void deleteMeasurementReportAndDescendants(Long remarkId) {
        BigDecimal id = BigDecimal.valueOf(remarkId);

        remarkRepository.deleteById(id);
    }

    public CommentsPageDto getComments(Long remarkId,
        int pagenumber,
        int pagesize,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Comments");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "value").and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        Page<RemarkElement> results = elementRepository.findFullElement(pageable, id);

        CommentsPageDto dto = new CommentsPageDto();

        dto.setResults(results.getContent().stream()
            .map(comment -> getCommentDto(comment))
            .collect(Collectors.toList()));
        
        dto.setCurrentPage(results.getNumber());
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortDirection(sortDirection);

        return dto;
    }

    private CommentTypeEnum getCommentType(String typeCode) {
        if ("DATE".equals(typeCode)) {
            return CommentTypeEnum.DATE;
        } else if ("NUM".equals(typeCode)) {
            return CommentTypeEnum.NUMERIC;
        } else {
            return CommentTypeEnum.STRING;
        }
    }

    private CommentDto getCommentDto(RemarkElement element) {
        Variable variable = element.getVariable();
        return new CommentDto()
            .comment(element.getValue())
            .commentId(element.getId().longValue())
            .commentType(getCommentType(variable.getTypeCode()))
            .maxLength(variable.getLength() != null ? variable.getLength().longValue() : null);

    }

    public CommentDto updateComment(Long remarkId, Long dataId, CommentDto updateDto) {
        LOGGER.info("Updating a Comment");

        BigDecimal varId = BigDecimal.valueOf(dataId);
        Optional<RemarkElement> foundElement = elementRepository.findFullElementById(varId);
        if(!foundElement.isPresent()) {
            throw new NotFoundException("This Comment does not exist");
        }
        RemarkElement element = foundElement.get();

        Variable variable = element.getVariable();
        if(variable.getTable() != null && variable.getColumn() != null) {
            if(!elementRepository.validateAllowableVariableText(variable.getTable(), variable.getColumn(), updateDto.getComment())) {
                throw new ValidationException(updateDto.getComment() + " was not found in column " + variable.getColumn() + " on table " + variable.getTable() + ". Pleas re-enter.");
            }
        }

        element.setValue(updateDto.getComment());

        element = elementRepository.save(element);

        return getCommentDto(element);
    }

    public MeasurementsPageDto getMeasurements(Long remarkId,
        int pagenumber,
        int pagesize,
        MeasurementSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Measurement Reports");

        BigDecimal id = BigDecimal.valueOf(remarkId);

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getMeasurementSortColumn(sortColumn)).and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        Page<VersionRemarkMeasurement> results = measurementRepository.findByRemarkId(pageable, id);

        MeasurementsPageDto dto = new MeasurementsPageDto();

        dto.setResults(results.getContent().stream()
            .map(measurement -> getMeasurementDto(measurement))
            .collect(Collectors.toList()));
        
        dto.setCurrentPage(results.getNumber());
        dto.setPageSize(results.getSize());

        dto.setTotalElements(results.getTotalElements());
        dto.setTotalPages(results.getTotalPages());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private String getMeasurementSortColumn(MeasurementSortColumn sortColumn) {
        switch(sortColumn) {
            case YEAR:
                return "year";
            case FLOWRATE:
                return "flowRate";
            case UNIT:
                return "unit";
            case VOLUME:
                return "volume";
            default:
                return "id";
        }
    }

    private MeasurementDto getMeasurementDto(VersionRemarkMeasurement measurement) {
        return new MeasurementDto()
            .year(measurement.getYear().intValue())
            .flowRate(measurement.getAmount() != null ? measurement.getAmount().doubleValue() : null)
            .unit(measurement.getUnit())
            .volume(measurement.getVolume() != null ? measurement.getVolume().doubleValue() : null)
            .id(measurement.getId().longValue());
    }

    public MeasurementDto createMeasurement(Long waterRightId,
        Long versionNumber,
        Long reportId,
        MeasurementDto dto
    ) {
        LOGGER.info("Creating a new Measurement");

        validateMeasurementDto(dto, waterRightId, versionNumber);

        BigDecimal remarkId = BigDecimal.valueOf(reportId);

        VersionRemarkMeasurement measurement = getMeasurement(dto, remarkId);

        try {
            measurement = measurementRepository.save(measurement);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("CNRP_MEAS_UNT_000")) {
                    throw new NotFoundException(String.format("This Unit does not exist: %s", dto.getUnit()));
                } else if(constraintMessage.contains("CNRP_FRVX_FK")) {
                    throw new NotFoundException("This Report does not exist");
                }
            }
            throw e;
        }

        return getMeasurementDto(measurement);
    }

    private VersionRemarkMeasurement getMeasurement(MeasurementDto dto, BigDecimal remarkId) {
        VersionRemarkMeasurement measurement = new VersionRemarkMeasurement();
        if(dto.getFlowRate() != null) measurement.setAmount(BigDecimal.valueOf(dto.getFlowRate()));
        measurement.setRemarkId(remarkId);
        measurement.setUnit(dto.getUnit());
        measurement.setYear(BigDecimal.valueOf(dto.getYear()));
        if(dto.getVolume() != null) measurement.setVolume(BigDecimal.valueOf(dto.getVolume()));
        return measurement;
    }

    public MeasurementDto updateMeasurement(Long waterRightId,
        Long versionNumber,
        Long reportId,
        Long measurementId,
        MeasurementDto dto
    ) {
        LOGGER.info("Creating a new Measurement Report");

        BigDecimal id = BigDecimal.valueOf(measurementId);

        validateMeasurementDto(dto, waterRightId, versionNumber);

        Optional<VersionRemarkMeasurement> foundMeasurement = measurementRepository.findById(id);
        if(!foundMeasurement.isPresent()) {
            throw new NotFoundException("This Measurement does not exist");
        }
        VersionRemarkMeasurement measurement = foundMeasurement.get();

        measurement.setAmount(dto.getFlowRate() != null ? BigDecimal.valueOf(dto.getFlowRate()) : null);
        measurement.setUnit(dto.getUnit());
        measurement.setVolume(dto.getVolume() != null ? BigDecimal.valueOf(dto.getVolume()) : null);
        measurement.setYear(BigDecimal.valueOf(dto.getYear()));

        try {
            measurement = measurementRepository.save(measurement);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("CNRP_MEAS_UNT_000")) {
                    throw new NotFoundException(String.format("This Unit does not exist: %s", dto.getUnit()));
                } else if(constraintMessage.contains("CNRP_FRVX_FK")) {
                    throw new NotFoundException("This Report does not exist");
                }
            }
            throw e;
        }

        return getMeasurementDto(measurement);
    }

    private void validateMeasurementDto(MeasurementDto dto, Long waterRightId, Long versionNumber) {
        if(dto.getYear() > LocalDate.now().getYear()) {
            throw new ValidationException("The Year cannot be in the future");
        }
        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.findById(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }
        WaterRightVersion version = foundVersion.get();
        
        if(dto.getYear() < version.getOperatingAuthority().getYear()) {
            throw new ValidationException("The Year cannot be before the Operating Authority Date");
        }
    }

    public void deleteMeasurement(Long measurementId) {
        LOGGER.info("Deleting a Measurement");

        BigDecimal id = BigDecimal.valueOf(measurementId);

        measurementRepository.deleteById(id);
    }
}
