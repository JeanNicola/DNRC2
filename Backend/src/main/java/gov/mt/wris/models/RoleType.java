package gov.mt.wris.models;

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
@Table(name = Constants.ROLE_TYPES_TABLE)
@Getter
@Setter
public class RoleType {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roletypes_seq")
	@SequenceGenerator(name = "roletypes_seq", sequenceName = Constants.ROLE_TYPES_SEQUENCE, allocationSize = 1)
	@Column(name = Constants.ROLE_TYPES_CODE)
	private String code;

	@Column(name = Constants.ROLE_TYPES_DESCR)
	private String description;
}
