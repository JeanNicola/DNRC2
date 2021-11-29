package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.REPRESENTATIVE_TABLE)
@Getter
@Setter
public class Representative {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rep_seq")
	@SequenceGenerator(name = "rep_seq", sequenceName = Constants.REPRESENTATIVE_SEQUENCE, allocationSize = 1)
	@Column(name = Constants.REPRESENTATIVE_ID)
	private BigDecimal representativeId;

	@ManyToOne(targetEntity = Owner.class)
	@JoinColumns({
			@JoinColumn(name = Constants.REPRESENTATIVE_OWNER_CUSTOMER_ID, referencedColumnName = Constants.OWNER_CUSTOMER_ID, nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = Constants.REPRESENTATIVE_OWNER_ID, referencedColumnName = Constants.OWNER_ID, nullable = false, insertable = false, updatable = false) })
	private Owner owner;

	@Column(name = Constants.REPRESENTATIVE_OWNER_ID)
	private BigDecimal ownerId;

	@Column(name = Constants.REPRESENTATIVE_BEGIN_DATE)
	private LocalDate beginDate;

	@Column(name = Constants.REPRESENTATIVE_END_DATE)
	private LocalDate endDate;

	@ManyToOne(targetEntity = RoleType.class)
	@JoinColumn(name = Constants.REPRESENTATIVE_ROLE_TYPE, referencedColumnName = Constants.ROLE_TYPES_CODE, nullable = false, insertable = false, updatable = false)
	@Fetch(value = FetchMode.JOIN)
	private RoleType roleType;

	@Column(name = Constants.REPRESENTATIVE_ROLE_TYPE)
	private String roleTypeCode;

	@ManyToOne(targetEntity = Customer.class)
	@JoinColumn(name = Constants.REPRESENTATIVE_CUSTOMER_ID, referencedColumnName = Constants.REPRESENTATIVE_CUSTOMER_ID, nullable = false, insertable = false, updatable = false)
	@Fetch(value = FetchMode.JOIN)
	private Customer customer;

	@Column(name = Constants.REPRESENTATIVE_CUSTOMER_ID)
	private BigDecimal customerId;

	@Column(name = Constants.REPRESENTATIVE_OWNER_CUSTOMER_ID)
	private BigDecimal secondaryCustomerId;

	@Column(name = Constants.REPRESENTATIVE_OBJECTOR_CUSTOMER_ID)
	private BigDecimal thirdCustomerId;

	@Column(name = Constants.OBJECTIONS_ID)
	private BigDecimal objectionId;
	
	@ManyToOne(targetEntity = Objector.class)
	@JoinColumns({
		@JoinColumn(name = Constants.REPRESENTATIVE_OBJECTOR_CUSTOMER_ID, referencedColumnName = Constants.CUSTOMER_ID, nullable = false, insertable = false, updatable = false),
		@JoinColumn(name = Constants.OBJECTIONS_ID, referencedColumnName = Constants.OBJECTIONS_ID, nullable = false, insertable = false, updatable = false)
	})
	private Objector objector;
}
