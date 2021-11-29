package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Constants.CLIMATIC_AREAS_TABLE)
@Getter
@Setter
public class ClimaticArea {

    @Id
    @Column(name = Constants.CLIMATIC_AREA_CODE)
    private String code;

    @Column(name = Constants.CLIMATIC_AREA_DESCRIPTION)
    String description;

}
