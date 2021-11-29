package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for Case Status table.
 *
 * @author Cesar.Zamorano
 */
@Entity
@Table(name = Constants.CASE_STATUS_TABLE)
@Getter
@Setter
public class CaseStatus {
	@Id
	@Column(name = Constants.CASE_STATUS_CODE)
	public String code;

	@Column(name = Constants.CASE_STATUS_DESCRIPTION)
	public String description;
}
