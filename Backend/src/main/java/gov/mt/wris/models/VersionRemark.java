package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.VERSION_REMARKS_TABLE)
@Getter
@Setter
public class VersionRemark {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "version_remark_sequence"
    )
    @SequenceGenerator(
        name = "version_remark_sequence",
        sequenceName = Constants.VERSION_REMARKS_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.REMARKS_ID)
    public BigDecimal id;

    @Column(name = Constants.VERSION_REMARK_CODE)
    public String remarkCode;

    @Column(name = Constants.REPORT_TYPE_CODE)
    public String reportTypeCode;

    @Column(name = Constants.VERSION_REMARK_DATE)
    public LocalDate date;

    @Column(name = Constants.VERSION_REMARK_END_DATE)
    public LocalDate endDate;

    @Column(name = Constants.VERSION_REMARK_TYPE_INDICATOR)
    public String typeIndicator;

    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Column(name = Constants.VERSION_ID)
    public BigDecimal version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.VERSION_REMARK_CODE, referencedColumnName = Constants.VERSION_REMARK_CODE, insertable = false, updatable = false, nullable = false)
    public RemarkCode remarkCodeLibrary;

    @OneToMany(mappedBy = "remark")
    public List<RemarkElement> elements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.REPORT_TYPE_CODE, referencedColumnName = Constants.REPORT_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    public ReportType reportType;
}
