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
@Table(name = Constants.SYSTEM_VARIABLE_TABLE)
@Getter
@Setter
public class SystemVariables {
    @Id
    @Column(name = Constants.SYSTEM_VARIABLE_ID)
    public BigDecimal id;

    @Column(name = Constants.SYSTEM_VARIABLE_NAME)
    public String name;

    @Column(name = Constants.SYSTEM_VARIABLE_VALUE)
    public String value;
}
