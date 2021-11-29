package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.LEGAL_LAND_DESCRIPTION_TABLE)
@Getter
@Setter
public class LegalLandDescription {
    @Id
    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_ID)
    public BigDecimal id;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_GOVERNMENT_LOT)
    public BigDecimal governmentLot;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_320)
    public String description320;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_160)
    public String description160;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_80)
    public String description80;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_40)
    public String description40;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.TRS_LOCATION_ID, referencedColumnName = Constants.TRS_LOCATION_ID)
    public TRS trs;
}
