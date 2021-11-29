package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.MEANS_OF_DIVERSION_TABLE)
@Getter
@Setter
public class MeansOfDiversion {
    @Id
    @Column(name = Constants.MEANS_OF_DIVERSION_CODE)
    public String meansCode;

    @Column(name = Constants.MEANS_OF_DIVERSION_DESCRIPTION)
    public String description;
}
