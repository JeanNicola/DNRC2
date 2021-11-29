package gov.mt.wris.models;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for Master Staff Indexes table.
 * 
 * @author Cesar.Zamorano
 */
@Entity
@Table(name = Constants.MASTER_STAFF_INDEXES_TABLE)
@Getter
@Setter
public class MasterStaffIndexes {

	@Id
	@Column(name = Constants.MASTER_STAFF_INDEXES_DNRC_ID)
	private BigDecimal id;

	@Column(name = Constants.MASTER_STAFF_INDEXES_DIRECTORY_USER)
	private String directoryUser;

	@Column(name = Constants.MASTER_STAFF_INDEXES_FIRST_NAME)
	private String firstName;

	@Column(name = Constants.MASTER_STAFF_INDEXES_MID_INITIAL)
	private String midInitial;

	@Column(name = Constants.MASTER_STAFF_INDEXES_LAST_NAME)
	private String lastName;
	
	@Column(name = Constants.MASTER_STAFF_INDEXES_OFFICE_ID)
	private BigDecimal officeId;

	@Column(name = Constants.EVENT_MODIFIED_BY)
	private String modBy;

	@Column(name = Constants.MASTER_STAFF_INDEXES_END_DATE)
	private LocalDate endDate;

	@Column(name = Constants.MASTER_STAFF_INDEXES_POSITION_CODE)
	private String positionCode;

	@Column(name = Constants.DISTRICT_COURT)
	private Integer districtCourt;

}
