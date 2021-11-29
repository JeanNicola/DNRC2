package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.COMPACT_TABLE)
@Getter
@Setter
public class Compact {
    @Id
    @Column(name = Constants.COMPACT_ID)
    public BigDecimal id;

    @Column(name = Constants.COMPACT_NAME)
    public String name;
}
