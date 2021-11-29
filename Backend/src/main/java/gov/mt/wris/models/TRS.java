package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.TRS_LOCATION_TABLE)
@Getter
@Setter
public class TRS {
    @Id
    @Column(name = Constants.TRS_LOCATION_ID)
    public BigDecimal id;

    @Column(name = Constants.TRS_LOCATION_SECTION)
    public BigDecimal section;

    @Column(name = Constants.TRS_LOCATION_TOWNSHIP)
    public BigDecimal township;

    @Column(name = Constants.TRS_LOCATION_RANGE)
    public BigDecimal range;

    @Column(name = Constants.TRS_LOCATION_TOWNSHIP_DIRECTION)
    public String townshipDirection;

    @Column(name = Constants.TRS_LOCATION_RANGE_DIRECTION)
    public String rangeDirection;
}
