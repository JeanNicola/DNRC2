package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.CaseApplicationXrefId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.CASE_APPLICATION_XREF_TABLE)
@IdClass(CaseApplicationXrefId.class)
@Getter
@Setter
public class CaseApplicationXref {
    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

    @Id
    @Column(name = Constants.APPLICATION_ID)
    public BigDecimal applicationId;

    @ManyToOne(targetEntity = CourtCase.class)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, nullable = false, insertable = false, updatable = false)
    public CourtCase courtCase;

    @ManyToOne(targetEntity = Application.class)
    @JoinColumn(name = Constants.APPLICATION_ID, referencedColumnName = Constants.APPLICATION_ID, nullable = false, insertable = false, updatable = false)
    public Application application;

}
