package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = Constants.STAFF_APPL_XREFS_TABLE)
@Getter
@Setter
public class StaffApplicationXref {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saxr_seq")
	@SequenceGenerator(name = "saxr_seq", sequenceName = Constants.STAFF_APPL_XREFS_SEQUENCE, allocationSize = 1)
	@Column(name = Constants.STAFF_APPL_XREFS_ID)
	private BigDecimal id;

	@ManyToOne(targetEntity = MasterStaffIndexes.class)
	@JoinColumn(name = Constants.STAFF_APPL_XREFS_DNRC_ID, referencedColumnName = Constants.MASTER_STAFF_INDEXES_DNRC_ID, nullable = false, insertable = false, updatable = false)
	private MasterStaffIndexes masterStaffIndex;

	@ManyToOne(targetEntity = Application.class)
	@JoinColumn(name = Constants.STAFF_APPL_XREFS_APPL_ID, referencedColumnName = Constants.APPLICATION_ID, nullable = false, insertable = false, updatable = false)
	private Application application;

	@Column(name = Constants.STAFF_APPL_XREFS_DNRC_ID)
	private BigDecimal masterStaffIndexId;

	@Column(name = Constants.STAFF_APPL_XREFS_APPL_ID)
	private BigDecimal applicationId;
	
	@Column(name = Constants.STAFF_APPL_XREFS_BEGIN_DATE)
	public LocalDate beginDate;

	@Column(name = Constants.STAFF_APPL_XREFS_END_DATE)
	public LocalDate endDate;

    @Column(name = Constants.EVENT_CREATED_DATE)
    public LocalDateTime createdDate;
}
