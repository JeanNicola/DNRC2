package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = Constants.WATER_COURT_CASE_TYPES_TABLE)
public class CourtCaseDecreeDetail {

    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal id;

    @Column(name = Constants.DECREE_ID)
    public BigDecimal decreeId;

    @Formula(value = "( \n" +
            " SELECT e.DT_OF_EVNT \n" +
            " FROM WRD_DECREES d \n" +
            " LEFT OUTER JOIN WRD_EVENT_DATES e \n" +
            "     ON e.DECR_ID_SEQ=d.DECR_ID_SEQ and e.EVTP_CD='DISS' \n" +
            " where d.DECR_ID_SEQ = DECR_ID_SEQ \n " +
            ")")
    public LocalDateTime decreeIssuedDate;

    @OneToOne(mappedBy = "decreeIssuedDate", fetch = FetchType.LAZY, optional = false)
    public CourtCase courtCase;

}
