package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name=Constants.CUSTOMER_TYPES_TABLE)
@Getter
@Setter
public class CustomerTypes {
    @Id
    @Column(name=Constants.CUSTOMER_TYPE_CODE)
    public String code;

    @Column(name=Constants.CUSTOMER_TYPE_DESCR)
    public String description;
}