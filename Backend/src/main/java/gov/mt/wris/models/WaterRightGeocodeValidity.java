package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = Constants.WATER_RIGHT_TABLE)
public class WaterRightGeocodeValidity {
    @Id
    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Formula(value = "( \n" +
        " SELECT DISTINCT DECODE(X.END_DT, NULL, (DECODE(X.VALID,'Y','1',NULL))) \n" +
        "   FROM WRD_GEOCODE_WATER_RIGHT_XREFS X \n" +
        "   WHERE  X.WRGT_ID_SEQ = WRGT_ID_SEQ \n" +
        " MINUS \n" +
        " SELECT DISTINCT DECODE(X.VALID,'N','1',NULL) \n" +
        "   FROM WRD_GEOCODE_WATER_RIGHT_XREFS X \n" +
        "   WHERE X.WRGT_ID_SEQ = WRGT_ID_SEQ \n" +
        "   AND   X.END_DT IS NULL \n" +
    ")")
    public Integer validGeocode;

    @OneToOne(mappedBy = "validity", fetch = FetchType.LAZY, optional = false)
    public WaterRight waterRight;
}
