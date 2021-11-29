package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Cesar.Zamorano
 *
 */
@Entity
@Table(name = Constants.OFFICES_TABLE)
@Getter
@Setter
public class Office {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offc_seq")
	@SequenceGenerator(name = "offc_seq", sequenceName = Constants.OFFICES_SEQUENCE, allocationSize = 1)
	@Column(name = Constants.OFFICES_ID)
	private BigDecimal id;

	@Column(name = Constants.OFFICES_DESCR)
	private String description;

	@Column(name = Constants.OFFICES_NOTES)
	private String notes;
}
