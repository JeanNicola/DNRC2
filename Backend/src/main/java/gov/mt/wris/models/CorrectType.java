package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Cesar.Zamorano
 *
 */
@Entity
@Table(name = Constants.CORRECT_TYPES_TABLE)
@Getter
@Setter
public class CorrectType {

	@Id
	@Column(name = Constants.CORRECT_TYPES_CODE)
	private String code;

	@Column(name = Constants.CORRECT_TYPES_DESCRIPTION)
	private String description;
}
