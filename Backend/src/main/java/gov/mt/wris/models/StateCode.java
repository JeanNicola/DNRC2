package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name=Constants.STATE_TABLE)
@Getter
@Setter
public class StateCode {
    @Id
    @Column(name=Constants.STATE_CODE)
    public String code;

    @Column(name=Constants.STATE_NAME)
    public String name;
}
