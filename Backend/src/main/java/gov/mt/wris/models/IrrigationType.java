package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Constants.IRRIGATION_TYPES_TABLE)
@Getter
@Setter
public class IrrigationType {

    @Id
    @Column(name = Constants.IRRIGATION_TYPE_CODE)
    private String code;

    @Column(name = Constants.IRRIGATION_TYPE_DESCRIPTION)
    String description;

    @Column(name = Constants.IRRIGATION_HISTORICAL)
    String historical;

}
