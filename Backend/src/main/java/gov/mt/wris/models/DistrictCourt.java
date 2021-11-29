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
@Table(name = Constants.CASE_DISTRICT_COURT_TABLE)
@Getter
@Setter
public class DistrictCourt {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cdct_seq"
    )
    @SequenceGenerator(
            name = "cdct_seq",
            sequenceName = Constants.DISTRICT_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.DISTRICT_ID)
    public BigDecimal districtId;

    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

}
