package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Cesar.Zamorano
 *
 */
@Entity
@Table(name = Constants.CORRECT_COMPLETES_TABLE)
@Getter
@Setter
public class CorrectComplete {

	@Id
	@Column(name = Constants.CORRECT_COMPLETES_ID)
	private BigDecimal id;

	@ManyToOne(targetEntity = CorrectType.class, fetch = FetchType.LAZY)
	@JoinColumn(name = Constants.CORRECT_COMPLETES_TYPE, referencedColumnName = Constants.CORRECT_TYPES_CODE, insertable = false, nullable = false, updatable = false)
	private CorrectType correctCompleteType;

	@Column(name = Constants.OBJECTIONS_ID)
	private BigDecimal objectionId;

	@Column(name = Constants.CORRECT_COMPLETE_DATE)
	private LocalDate correctCompleteDate;

}
