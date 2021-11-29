package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=Constants.WATER_RIGHT_TYPE_TABLE)
@Getter
@Setter
public class WaterRightType {
    @Id
    @Column(name=Constants.WATER_RIGHT_TYPE_CODE)
    public String code;

    @Column(name=Constants.WATER_RIGHT_TYPE_DESCR)
    public String description;

    @Column(name = Constants.WATER_RIGHT_TYPE_PROGRAM)
    public String program;
}
