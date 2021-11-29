package gov.mt.wris.models;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.*;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = Constants.CITY_TABLE)
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "city_seq"
    )
    @SequenceGenerator(
        name = "city_seq",
        sequenceName = Constants.CITY_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.CITY_ID)
    private BigInteger cityId;

    @Column(name = Constants.CITY_NAME)
    public String cityName;

    @Column(name = Constants.STATE_CODE)
    public String stateCode;

    @OneToMany(
        targetEntity = ZipCode.class,
        mappedBy = "city",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    public List<ZipCode> zipCodes;

    @ManyToOne(targetEntity = StateCode.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.STATE_CODE, referencedColumnName = Constants.STATE_CODE, insertable = false, updatable = false)
    public StateCode state;

}
