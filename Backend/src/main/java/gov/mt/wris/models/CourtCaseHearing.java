package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.WATER_COURT_CASE_TYPES_TABLE)
@Getter
@Setter
public class CourtCaseHearing {
    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal id;

    @Column(name = Constants.CASE_NO)
    public String caseNumber;

    @Formula("(SELECT max(e.dt_of_evnt) FROM WRD_EVENT_DATES e WHERE e.EVTP_CD in ('HRHD','HSCH','WHRD') AND e.case_id_seq = CASE_ID_SEQ)")
    public LocalDateTime hearingDate ;

    @Column(name = Constants.CITY_ID)
    public BigDecimal cityId;

    @ManyToOne(targetEntity = CaseStatus.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_STATUS_CODE, referencedColumnName = Constants.CASE_STATUS_CODE, insertable = false, updatable = false, nullable = false)
    public CaseStatus caseStatus;

    @ManyToOne(targetEntity = CaseType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_TYPE_CODE, referencedColumnName = Constants.CASE_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    public CaseType caseType;
}
