package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.CaseTypeXrefId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.CASE_TYPE_XREF_TABLE)
@IdClass(CaseTypeXrefId.class)
@Getter
@Setter
public class CaseTypeXref {
    @Id
    @Column(name = Constants.EVENT_TYPE_CODE)
    public String eventCode;

    @Id
    @Column(name = Constants.CASE_TYPE_CODE)
    public String caseCode;

    @ManyToOne
    @JoinColumn(name=Constants.EVENT_TYPE_CODE, referencedColumnName = Constants.EVENT_TYPE_CODE, nullable = false, insertable = false, updatable = false)
    public EventType eventType;

    @ManyToOne(targetEntity = CaseType.class)
    @JoinColumn(name = Constants.CASE_TYPE_CODE, referencedColumnName = Constants.CASE_TYPE_CODE, insertable = false, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private CaseType caseType;
}
