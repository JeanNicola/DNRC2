package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = Constants.ENFORCEMENT_AREA_TABLE)
@Getter
@Setter
public class EnforcementArea {

    @Id
    @Column(name = Constants.ENFORCEMENT_AREA_ID)
    public String id;

    @Column(name = Constants.ENFORCEMENT_AREA_NAME)
    public String name;

}
