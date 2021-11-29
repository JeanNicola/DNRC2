package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name=Constants.WATER_RIGHT_STATUS_TABLE)
@Getter
@Setter
public class WaterRightStatus {
    @Id
    @Column(name=Constants.WATER_RIGHT_STATUS_CODE)
    public String code;

    @Column(name=Constants.WATER_RIGHT_STATUS_DESCR)
    public String description;

    @OneToMany(mappedBy = "status")
    public List<StatusTypeXref> typeXrefs;
}
