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
import gov.mt.wris.models.IdClasses.ApplicationTypeXrefId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.APPLICATION_TYPE_XREF_TABLE)
@IdClass(ApplicationTypeXrefId.class)
@Getter
@Setter
public class ApplicationTypeXref {
    @Id
    @Column(name=Constants.EVENT_TYPE_CODE)
    public String eventCode;

    @Id
    @Column(name=Constants.APPLICATION_TYPE_CODE)
    public String applicationCode;

    @ManyToOne
    @JoinColumn(name=Constants.EVENT_TYPE_CODE, referencedColumnName = Constants.EVENT_TYPE_CODE, nullable = false, insertable = false, updatable = false)
    public EventType eventType;

    @ManyToOne(targetEntity = ApplicationType.class)
    @JoinColumn(name = Constants.APPLICATION_TYPE_CODE, referencedColumnName = Constants.APPLICATION_TYPE_CODE, insertable = false, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private ApplicationType applicationType;
}