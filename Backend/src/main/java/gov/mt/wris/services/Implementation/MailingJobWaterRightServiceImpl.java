package gov.mt.wris.services.Implementation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ValidationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import gov.mt.wris.dtos.JobWaterRightCreationDto;
import gov.mt.wris.dtos.JobWaterRightDto;
import gov.mt.wris.dtos.JobWaterRightPageDto;
import gov.mt.wris.dtos.JobWaterRightSortColumn;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.MailingJobWaterRight;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightStatus;
import gov.mt.wris.models.WaterRightType;
import gov.mt.wris.repositories.MailingJobWaterRightRepository;
import gov.mt.wris.services.MailingJobWaterRightService;

@Service
public class MailingJobWaterRightServiceImpl implements MailingJobWaterRightService {
    private static Logger LOGGER = LoggerFactory.getLogger(MailingJobWaterRightService.class);

    @Autowired
    private MailingJobWaterRightRepository xrefRepository;

    public JobWaterRightPageDto getJobWaterRights(Long mailingJobId,
        int pagenumber,
        int pagesize,
        JobWaterRightSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Get the Water Rights attached to a Water Right");

        Sort sort = getWaterRightSort(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal id = BigDecimal.valueOf(mailingJobId);

        Page<MailingJobWaterRight> results = xrefRepository.findByMailingJobId(pageable, id);

        JobWaterRightPageDto page = new JobWaterRightPageDto()
            .results(
                results.getContent().stream()
                .map(water -> getJobWaterRightDto(water))
                .collect(Collectors.toList())
            )
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalElements(results.getTotalElements())
            .totalPages(results.getTotalPages())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
        
        return page;
    }

    private static JobWaterRightDto getJobWaterRightDto(MailingJobWaterRight model) {
        WaterRight waterRight = model.getWaterRight();
        WaterRightType type = waterRight.getWaterRightType();
        WaterRightStatus status = waterRight.getWaterRightStatus();
        return new JobWaterRightDto()
            .waterRightTypeDescription(type.getDescription())
            .waterRightStatusDescription(status != null ? status.getDescription() : null)
            .waterRightId(waterRight.getWaterRightId().longValue())
            .completeWaterRightNumber(
                Stream.of(
                    waterRight.getBasin(),
                    waterRight.getWaterRightNumber().toString(),
                    waterRight.getExt()
                ).filter(part -> part != null)
                .collect(Collectors.joining(" "))
            );
    }

    private static Sort getWaterRightSort(JobWaterRightSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        switch(sortColumn) {
            case WATERRIGHTTYPEDESCRIPTION:
                sort = Sort.by(direction, "t.description");
                break;
            case WATERRIGHTSTATUSDESCRIPTION:
                sort = Sort.by(direction, "s.description");
                break;
            default:
                sort = Sort.by(direction, "w.basin")
                    .and(Sort.by(direction, "w.waterRightNumber"))
                    .and(Sort.by(direction, "w.ext"));
                break;
        }
        sort = sort.and(Sort.by(direction, "w.waterRightId"));

        return sort;
    }

    public void addJobWaterRight(Long mailingJobId, JobWaterRightCreationDto creationDto) {
        LOGGER.info("Adding a Water Right to a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);
        BigDecimal waterId = BigDecimal.valueOf(creationDto.getWaterRightId());

        Optional<MailingJobWaterRight> foundWaterRight = xrefRepository.findByMailingJobIdAndWaterRightId(id, waterId);
        if(foundWaterRight.isPresent()) {
            throw new ValidationException("This Water Right is already attached to this Mailing Job");
        }

        addWaterRightToMailingJob(id, waterId);
    }

    public void addWaterRightToMailingJob(BigDecimal id, BigDecimal waterId) {

        MailingJobWaterRight xref = new MailingJobWaterRight();
        xref.setMailingJobId(id);
        xref.setWaterRightId(waterId);

        try {
            xrefRepository.save(xref);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("MLJW_MLJB_FK")) {
                    throw new NotFoundException(String.format("This Mailing Job does not exist: %s", id));
                } else if(constraintMessage.contains("MLJW_WRGT_FK")) {
                    throw new NotFoundException("This Water Right does not exist");
                }
            }
            throw e;
        }
    }

    public void removeJobWaterRight(Long mailingJobId, Long waterRightId) {
        LOGGER.info("Removing a Water Right from a Mailing Job");

        BigDecimal id = BigDecimal.valueOf(mailingJobId);
        BigDecimal waterId = BigDecimal.valueOf(waterRightId);

        xrefRepository.deleteByMailingJobIdAndWaterRightId(id, waterId);
    }

    public Message importJobWaterRights(Long mailingJobId, MultipartFile file) {
        LOGGER.info("Importing Water Rights for a Mailing Job");

        Workbook book;
        try {
            book = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new ValidationException("Corrupted file");
        }
        Sheet sheet = book.getSheetAt(0);

        HashMap<String, HashMap<String, List<String>>> excelRights = new HashMap<String, HashMap<String, List<String>>>();
        String basin, ext, waterRightNumber;
        Cell cell;
        DecimalFormat extFormatter = new DecimalFormat("00");
        DecimalFormat waterFormatter = new DecimalFormat("#########0");

        HashMap<String, List<String>> basinRights;
        for(Row row : sheet) {
            cell = row.getCell(0);
            if(cell == null || cell.getCellTypeEnum() != CellType.STRING) {
                throw new ValidationException("Enter valid Basins in the first column");
            }
            basin = cell.getStringCellValue();

            cell = row.getCell(1);
            if(cell == null || cell.getCellTypeEnum() != CellType.NUMERIC) {
                throw new ValidationException("Enter valid Water Right Numbers in the second column");
            }
            waterRightNumber = waterFormatter.format(cell.getNumericCellValue());

            cell = row.getCell(2);
            if (cell != null) {
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    ext = extFormatter.format(cell.getNumericCellValue());
                } else {
                    try {
                        ext = extFormatter.format(Double.parseDouble(cell.getStringCellValue()));
                    } catch (NumberFormatException e) {
                        throw new ValidationException("Enter a valid extension into the third column");
                    }
                }
            } else {
                ext = null;
            }

            basinRights = excelRights.get(basin);
            List extRights;
            if(basinRights == null) {
                // adds { basin: { ext: [waterRightNumber]}}
                extRights = new ArrayList<>();
                extRights.add(waterRightNumber);
                basinRights = new HashMap<>();
                basinRights.put(ext, extRights);
                excelRights.put(basin, basinRights);
            } else {
                extRights = basinRights.get(ext);
            }

            if(extRights != null && !extRights.contains(waterRightNumber)) {
                extRights.add(waterRightNumber);
            } else if (extRights == null) {
                extRights = new ArrayList<>(Arrays.asList(waterRightNumber));
                basinRights.put(ext, extRights);
            } // do nothing if it's a duplicate
        }

        int addedWaterRights = xrefRepository.addMailingJobWaterRights(mailingJobId, excelRights);
        
        return new Message().userMessage(String.format("%d Water Right%s imported", addedWaterRights, addedWaterRights != 1 ? "s" : ""));
    }
}
