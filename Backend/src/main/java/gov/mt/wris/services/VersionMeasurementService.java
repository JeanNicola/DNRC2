package gov.mt.wris.services;

import gov.mt.wris.dtos.CommentDto;
import gov.mt.wris.dtos.CommentsPageDto;
import gov.mt.wris.dtos.MeasurementDto;
import gov.mt.wris.dtos.MeasurementSortColumn;
import gov.mt.wris.dtos.MeasurementsPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.VersionMeasurementReportDto;
import gov.mt.wris.dtos.VersionMeasurementReportSortColumn;
import gov.mt.wris.dtos.VersionMeasurementReportsPageDto;

public interface VersionMeasurementService {
    public VersionMeasurementReportsPageDto getMeasurementReports(Long waterRightId,
        Long versionId,
        int pagenumber,
        int pagesize,
        VersionMeasurementReportSortColumn sortColumn,
        SortDirection sortDirection);

    public VersionMeasurementReportDto createMeasurementReport(Long waterRightId,
        Long versionNumber,
        VersionMeasurementReportDto dto);

    public VersionMeasurementReportDto updateMeasurementReport(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        VersionMeasurementReportDto dto);

    public void deleteMeasurementReport(Long remarkId);

    public CommentsPageDto getComments(Long remarkId,
        int pagenumber,
        int pagesize,
        SortDirection sortDirection);

    public CommentDto updateComment(Long remarkId, Long dataId, CommentDto updateDto);

    public MeasurementsPageDto getMeasurements(Long remarkId,
        int pagenumber,
        int pagesize,
        MeasurementSortColumn sortColumn,
        SortDirection sortDirection);

    public MeasurementDto createMeasurement(Long waterRightId,
        Long versionNumber,
        Long reportId,
        MeasurementDto dto);

    public MeasurementDto updateMeasurement(Long waterRightId,
        Long versionNumber,
        Long reportId,
        Long measurementId,
        MeasurementDto dto);

    public void deleteMeasurement(Long measurementId);

    public void deleteMeasurementReportAndDescendants(Long remarkId);
}
