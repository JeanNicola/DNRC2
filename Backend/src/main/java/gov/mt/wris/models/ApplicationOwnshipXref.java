package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.ApplicationOwnshipXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name= Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE)
@IdClass(ApplicationOwnshipXrefId.class)
@Getter
@Setter
public class ApplicationOwnshipXref {
    @Id
    @Column(name = Constants.OWNR_UPDT_ID)
    public BigDecimal ownershipUpdateId;

    @Id
    @Column(name = Constants.APPLICATION_ID)
    public BigDecimal applicationId;

    @ManyToOne(targetEntity = OwnershipUpdate.class, fetch= FetchType.LAZY)
    @JoinColumn(name = Constants.OWNR_UPDT_ID, referencedColumnName = Constants.OWNR_UPDT_ID, updatable = false, nullable = false, insertable = false)
    public OwnershipUpdate ownershipUpdate;

    @ManyToOne(targetEntity = Application.class, fetch= FetchType.LAZY)
    @JoinColumn(name = Constants.APPLICATION_ID, referencedColumnName = Constants.APPLICATION_ID, updatable = false, nullable = false, insertable = false)
    public Application application;

    @Column(name= Constants.APPL_XREF_CREATED_BY)
    public String createdBy;

    @Column(name= Constants.APPL_XREF_DTM_CREATED)
    public LocalDate dateCreated;

    @Column(name= Constants.APPL_XREF_MOD_BY)
    public String modBy;

    @Column(name= Constants.APPL_XREF_DTM_MOD)
    public LocalDate dateMod;
}
