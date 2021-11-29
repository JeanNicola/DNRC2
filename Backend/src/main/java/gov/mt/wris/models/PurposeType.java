package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Constants.PURPOSE_TYPES_TABLE)
@Getter
@Setter
public class PurposeType {

    @Id
    @Column(name = Constants.PURT_CODE)
    private String code;

    @Column(name = Constants.PURPOSE_TYPE_DESCRIPTION)
    String description;

    @Column(name = Constants.PURPOSE_TYPE_REQUIRED)
    String required;

    @Column(name = Constants.PURPOSE_GROUP_CODE)
    String groupCode;

}
