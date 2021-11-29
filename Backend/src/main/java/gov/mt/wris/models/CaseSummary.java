package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = Constants.CASE_SUMMARY_TABLE)
@Getter
@Setter
public class CaseSummary {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "csum_seq"
    )
    @SequenceGenerator(
            name = "csum_seq",
            sequenceName = Constants.CASE_SUMMARY_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.CASE_SUMMARY_ID)
    public BigDecimal summaryId;

    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;
}
