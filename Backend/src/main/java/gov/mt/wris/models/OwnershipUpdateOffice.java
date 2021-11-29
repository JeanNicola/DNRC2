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

@Entity
@Table(name = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE)
@Getter
@Setter
public class OwnershipUpdateOffice {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "ooxr_sequence"
    )
    @SequenceGenerator(
        name = "ooxr_sequence",
        sequenceName = Constants.OWNERSHIP_UPDATE_OFFICE_XREF_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.OWNERSHIP_UPDATE_OFFICE_XREF_ID)
    private BigDecimal id;

    @Column(name = Constants.OWNR_UPDT_ID)
    private BigDecimal ownershipUpdateId;

    @Column(name = Constants.OWNERSHIP_UPDATE_OFFICE_ID)
    private BigDecimal officeId;

    @ManyToOne(targetEntity = Office.class)
    @JoinColumn(name = Constants.OWNERSHIP_UPDATE_OFFICE_ID, referencedColumnName = Constants.OFFICES_ID, insertable = false, updatable = false, nullable = false)
    private Office office;

	@Column(name = Constants.OWNERSHIP_UPDATE_OFFICE_RECEIVED_DATE)
	public LocalDate receivedDate;

	@Column(name = Constants.OWNERSHIP_UPDATE_OFFICE_SENT_DATE)
	public LocalDate sentDate;

    @Column(name = Constants.OWNERSHIP_UPDATE_OFFICE_CREATED_DATE)
    public LocalDateTime createdDate;
}
