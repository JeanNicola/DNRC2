package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for Counties table.
 * 
 * @author Cesar.Zamorano
 */
@Entity
@Table(name = Constants.COUNTIES_TABLE)
@Getter
@Setter
public class County {

	@Id
	@Column(name = Constants.COUNTIES_ID)
	private BigDecimal id;

	@Column(name = Constants.COUNTIES_NAME)
	private String name;

	@Column(name = Constants.COUNTIES_FIPS_CODE)
	private String fipsCode;

	@Column(name = Constants.COUNTIES_STATE_CODE)
	private String stateCode;

	@Column(name = Constants.COUNTIES_STATE_CODE_ID)
	private String stateCountyNumber;

	@Column(name = Constants.DISTRICT_COURT)
	private Integer districtCourt;

}
