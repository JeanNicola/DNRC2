package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.VersionApplicationXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = Constants.VERSION_APPLICATION_XREFS_TABLE)
@IdClass(VersionApplicationXrefId.class)
@Getter
@Setter
public class VersionApplicationXref {

    @Id
    @Column(name=Constants.APPLICATION_ID)
    public BigDecimal applicationId;

    @Id
    @Column(name=Constants.VERSIONS_ID)
    public BigDecimal versionId;

    @Id
    @Column(name=Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @ManyToOne(targetEntity = Application.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.APPLICATION_ID, referencedColumnName = Constants.APPLICATION_ID, nullable = false, insertable = false, updatable = false)
    private Application application;

    @Column(name = Constants.CREATED_BY)
    public String createdBy;

    @Column(name = Constants.DTM_CREATED)
    public LocalDate dtmCreated;

    @Column(name = Constants.MOD_BY)
    public String modBy;

    @Column(name = Constants.DTM_MOD)
    public LocalDate dtmMod;

}
