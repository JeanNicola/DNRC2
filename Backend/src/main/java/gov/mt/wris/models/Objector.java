package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.ObjectorId;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Cesar.Zamorano
 *
 */
@Entity
@Table(name = Constants.OBJECTORS_TABLE)
@IdClass(ObjectorId.class)
@Getter
@Setter
public class Objector {

	@Id
	@Column(name = Constants.OBJECTORS_OBJECTIONS_ID)
	private BigDecimal objectionId;

	@Id
	@Column(name = Constants.OBJECTORS_CUSTOMER_ID)
	private BigDecimal customerId;

	@ManyToOne(targetEntity = Objection.class, fetch = FetchType.LAZY)
	@JoinColumn(name = Constants.OBJECTORS_OBJECTIONS_ID, referencedColumnName = Constants.OBJECTIONS_ID, insertable = false, nullable = false, updatable = false)
	private Objection objection;
	
	@ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
	@JoinColumn(name = Constants.OBJECTORS_CUSTOMER_ID, referencedColumnName = Constants.OWNER_CUSTOMER_ID, insertable = false, nullable = false, updatable = false)
	private Customer customer;

	@Column(name = Constants.OBJECTORS_END_DATE)
	private LocalDate endDate;

	@Formula(value = "(\n" +
		"SELECT COUNT(*)\n" +
		"FROM WRD_REPRESENTATIVES r\n" +
		"WHERE r.CUST_ID_SEQ_THR = CUST_ID_SEQ\n" +
		"AND r.OITA_ID_SEQ = OITA_ID_SEQ\n" +
		")"
	)
	public Long representativeCount;

    @OneToMany(targetEntity=Representative.class, mappedBy = "objector", cascade = CascadeType.ALL)
    public List<Representative> representatives;
}
