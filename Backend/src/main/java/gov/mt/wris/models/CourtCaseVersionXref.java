package gov.mt.wris.models;


import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.CourtCaseVersionXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.COURT_CASE_VERSION_XREF_TABLE)
@IdClass(CourtCaseVersionXrefId.class)
@Getter
@Setter
public class CourtCaseVersionXref {

    @Id
    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Id
    @Column(name = Constants.VERSION_ID)
    public BigDecimal versionId;

    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

    @Column(name = Constants.ORDER_ADOPTING_DATE)
    public LocalDateTime orderAdoptingDate;

    @ManyToOne(targetEntity = CourtCaseHearing.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, nullable = false, insertable = false, updatable = false)
    public CourtCaseHearing courtCaseHearing;

    @ManyToOne(targetEntity = WaterRightVersion.class, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = Constants.VERSION_WATER_RIGHT_ID, referencedColumnName = Constants.VERSION_WATER_RIGHT_ID, nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = Constants.VERSION_ID, referencedColumnName = Constants.VERSION_ID, nullable = false, insertable = false, updatable = false)
    })
    public WaterRightVersion waterRightVersion;

}
