package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.VERSION_REMARK_MEASUREMENT_TABLE)
@Getter
@Setter
public class VersionRemarkMeasurement {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "version_measurement_sequence"
    )
    @SequenceGenerator(
        name = "version_measurement_sequence",
        sequenceName = Constants.VERSION_REMARK_MEASUREMENT_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.VERSION_REMARK_MEASUREMENT_ID)
    public BigDecimal id;

    @Column(name = Constants.VERSION_REMARK_MEASUREMENT_YEAR)
    public BigDecimal year;

    @Column(name = Constants.VERSION_REMARK_MEASUREMENT_AMOUNT)
    public BigDecimal amount;

    @Column(name = Constants.VERSION_REMARK_MEASUREMENT_UNIT)
    public String unit;

    @Column(name = Constants.VERSION_REMARK_MEASUREMENT_VOLUME)
    public BigDecimal volume;

    @Column(name = Constants.REMARKS_ID)
    public BigDecimal remarkId;
}
