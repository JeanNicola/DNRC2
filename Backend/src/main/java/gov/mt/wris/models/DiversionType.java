package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.DIVERSION_TYPE_TABLE)
@Getter
@Setter
public class DiversionType {
    @Id
    @Column(name = Constants.DIVERSION_TYPE_CODE)
    public String code;

    @Column(name = Constants.DIVERSION_TYPE_DESCRIPTION)
    public String description;
}
