package gov.mt.wris.models;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name=Constants.ZIP_CODE_TABLE)
@Getter
@Setter
public class ZipCode {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "zip_seq"
    )
    @SequenceGenerator(
        name = "zip_seq",
        sequenceName = Constants.ZIP_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.ZIP_CODE_ID)
    private BigInteger zipCodeId;

    @Column(name = Constants.ZIP_CODE)
    public String zipCode;

    @Column(name = Constants.CITY_ID)
    public BigInteger cityIdSeq;

    // Since there is a reference to the CITY_ID_SEQ in WRD_ZIP_CODES,
    // there can only be one City per record
    @ManyToOne(targetEntity = City.class)
    @JoinColumn(name = Constants.CITY_ID, referencedColumnName = Constants.CITY_ID, insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private City city;
}