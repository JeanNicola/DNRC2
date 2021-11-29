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
@Table(name = Constants.OFFICE_APPL_XREFS_TABLE)
@Getter
@Setter
public class OfficeApplicationXref {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oaxr_seq")
	@SequenceGenerator(name = "oaxr_seq", sequenceName = Constants.OFFICE_APPL_XREFS_SEQUENCE, allocationSize = 1)
	@Column(name = Constants.OFFICE_APPL_XREFS_ID)
	private BigDecimal id;

	@ManyToOne(targetEntity = Office.class)
	@JoinColumn(name = Constants.OFFICE_APPL_XREFS_OFFICE_ID, referencedColumnName = Constants.OFFICES_ID, nullable = false, insertable = false, updatable = false)
	private Office office;
	
	@Column(name = Constants.OFFICE_APPL_XREFS_OFFICE_ID)
	private BigDecimal officeId;

	@ManyToOne(targetEntity = Application.class)
	@JoinColumn(name = Constants.OFFICE_APPL_XREFS_APPL_ID, referencedColumnName = Constants.APPLICATION_ID, nullable = false, insertable = false, updatable = false)
	private Application application;
	
	@Column(name = Constants.OFFICE_APPL_XREFS_APPL_ID)
	private BigDecimal applicationId;

	@Column(name = Constants.OFFICE_APPL_XREFS_RECEIVED_DATE)
	public LocalDate receivedDate;

	@Column(name = Constants.OFFICE_APPL_XREFS_SENT_DATE)
	public LocalDate sentDate;

    @Column(name = Constants.EVENT_CREATED_DATE)
    public LocalDateTime createdDate;
}
