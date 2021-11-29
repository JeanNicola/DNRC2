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
@Table(name = Constants.CASE_VEHICLE_TABLE)
@Getter
@Setter
public class CaseVehicle {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cveh_seq"
    )
    @SequenceGenerator(
            name = "cveh_seq",
            sequenceName = Constants.CASE_VEHICLE_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.CASE_VEHICLE_ID)
    public BigDecimal vehicleId;

    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

}
