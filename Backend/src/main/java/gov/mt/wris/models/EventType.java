package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name=Constants.EVENT_TYPE_TABLE)
@Getter
@Setter
public class EventType {
    @Id
    @Column(name=Constants.EVENT_TYPE_CODE)
    public String code;

    @Column(name=Constants.EVENT_TYPE_DESCR)
    public String description;

    @Column(name=Constants.EVENT_TYPE_DUE_DAYS)
    public Integer dueDays;

    @OneToMany(mappedBy = "eventType")
    public List<ApplicationTypeXref> applicationTypes;

    @OneToMany(mappedBy = "eventType")
    public List<CaseTypeXref> caseTypes;

    @OneToMany(mappedBy = "eventType")
    public List<DecreeTypeXref> decreeTypes;
}
