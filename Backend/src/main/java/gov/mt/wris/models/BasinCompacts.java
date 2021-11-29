package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.BASIN_COMPACT_TABLE)
@Getter
@Setter
public class BasinCompacts {
    @Id
    @Column(name = Constants.BASIN_CODE)
    public String code;

    @Column(name = Constants.BASIN_DESCR)
    public String description;

    @Column(name = Constants.BOCA_TYPE)
    public String type;

    @Column(name = Constants.PARENT_BASIN)
    public String parentBasin;
}
