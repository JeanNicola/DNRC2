package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.GEOCODE_TABLE)
@Getter
@Setter
public class Geocode {
    @Id
    @Column(name = Constants.GEOCODE_ID)
    public String geocodeId;

    @Formula(value = "WRD_COMMON_FUNCTIONS.FORMAT_GEOCODE(GOCD_ID_SEQ)")
    public String formattedGeocode;

    @Column(name = Constants.GEOCODE_STATUS)
    public String status;
}
