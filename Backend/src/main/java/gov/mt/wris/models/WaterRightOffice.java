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
@Table(name = Constants.WATER_RIGHT_OFFICE_TABLE)
@Getter
@Setter
public class WaterRightOffice {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "owrx_sequence"
    )
    @SequenceGenerator(
        name = "owrx_sequence",
        sequenceName = Constants.WATER_RIGHT_OFFICE_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.WATER_RIGHT_OFFICE_ID)
    private BigDecimal id;

    @Column(name = Constants.WATER_RIGHT_ID)
    private BigDecimal waterRightId;

    @Column(name = Constants.OFFICE_ID)
    private BigDecimal officeId;

    @ManyToOne(targetEntity = Office.class)
    @JoinColumn(name = Constants.OFFICE_ID, referencedColumnName = Constants.OFFICE_ID, insertable = false, updatable = false, nullable = false)
    private Office office;

    @Column(name = Constants.WATER_RIGHT_OFFICE_RECEIVED_DATE)
    public LocalDate receivedDate;

    @Column(name = Constants.WATER_RIGHT_OFFICE_SENT_DATE)
    public LocalDate sentDate;

    @Column(name = Constants.WATER_RIGHT_OFFICE_CREATED_DATE)
    public LocalDateTime createdDate;
}