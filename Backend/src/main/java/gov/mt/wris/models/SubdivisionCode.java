package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for SubdivisionCodes table.
 *
 * @author Cesar.Zamorano
 */
@Entity
@Table(name = Constants.SUBDIVISION_CODES_TABLE)
@Getter
@Setter
public class SubdivisionCode {

	@Id
	@Column(name = Constants.SUBDIVISION_CODES_CODE)
	private String code;

	@Column(name = Constants.SUBDIVISION_CODES_COUNTY_ID)
	private BigDecimal countyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Constants.SUBDIVISION_CODES_COUNTY_ID, referencedColumnName = Constants.COUNTIES_ID, nullable = false, insertable = false, updatable = false)
	private County county;

	@Column(name = Constants.SUBDIVISION_CODES_DNRC_NAME)
	private String dnrcName;

	@Column(name = Constants.SUBDIVISION_CODES_DOR_NAME)
	private String dorName;
}
