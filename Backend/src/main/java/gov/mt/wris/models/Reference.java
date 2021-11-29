package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.ReferenceId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = Constants.REFERENCE_TABLE)
@IdClass(ReferenceId.class)
@Getter
@Setter
public class Reference {
    @Id
    @Column(name = Constants.DOMAIN)
    public String domain;

    @Id
    @Column(name = Constants.LOW_VALUE)
    public String value;

    @Column(name = Constants.MEANING)
    public String meaning;

    @Column(name = Constants.ABBREVIATION)
    public String abbreviation;

}
