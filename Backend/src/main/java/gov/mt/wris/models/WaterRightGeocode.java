package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.JoinFormula;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE)
@Getter
@Setter
public class WaterRightGeocode {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "water_geocode_seq"
    )
    @SequenceGenerator(
        name = "water_geocode_seq",
        sequenceName = Constants.WATER_RIGHT_GEOCODE_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.WATER_RIGHT_GEOCODE_XREF_ID)
    public BigDecimal id;

    @Column(name = Constants.GEOCODE_ID)
    public String geocodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false)
    public WaterRight waterRight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.GEOCODE_ID, referencedColumnName = Constants.GEOCODE_ID, insertable = false, updatable = false, nullable = false)
    public Geocode geocode;

    @Column(name = Constants.GEOCODE_BEGIN_DATE)
    public LocalDate beginDate;

    @Column(name = Constants.GEOCODE_END_DATE)
    public LocalDate endDate;

    @Column(name = Constants.GEOCODE_VALID)
    public String valid;

    @Column(name = Constants.GEOCODE_COMMENTS)
    public String comments;

    @Column(name = Constants.GEOCODE_UNRESOLVED)
    public String unresolved;

    @Column(name = Constants.GEOCODE_SEVER)
    public String sever;

    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT m.dnrc_id\n" +
            "FROM WRD_MASTER_STAFF_INDEXES m\n" +
            "WHERE (m.END_DT is null or m.END_DT > DTM_MOD)\n" +
            "AND m.BGN_DT <= DTM_MOD\n" +
            "AND m.C_NO = MOD_BY\n" +
            ")")
    public MasterStaffIndexes modifiedByName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT m.dnrc_id \n" +
            "FROM WRD_MASTER_STAFF_INDEXES m\n" +
            "WHERE (m.END_DT is null or m.END_DT > DTM_CREATED)\n" +
            "AND m.BGN_DT <= DTM_CREATED\n" +
            "AND m.C_NO = CREATED_BY\n" +
            ")")
    public MasterStaffIndexes createdByName;

    @Column(name = Constants.EVENT_MODIFIED_DATE)
    public LocalDate modifiedDate;

    @Column(name = Constants.EVENT_CREATED_DATE)
    public LocalDate createdDate;
}
