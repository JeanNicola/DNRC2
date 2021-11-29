package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = Constants.PAYMENT_TABLE)
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "payment_seq"
    )
    @SequenceGenerator(
        name = "payment_seq",
        sequenceName = Constants.PAYMENT_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.PAYMENT_ID)
    BigDecimal id;

    @Column(name = Constants.PAYMENT_TRACKING_NO)
    String trackingNo;

    @Column(name = Constants.PAYMENT_AMOUNT)
    BigDecimal amount;

    @Column(name = Constants.PAYMENT_DATE)
    LocalDate date;

    @Column(name = Constants.PAYMENT_ORIGIN)
    String origin;

    @ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.PAYMENT_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.PAYMENT_APPLICATION_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference originReference;

    @Column(name = Constants.APPLICATION_ID)
    BigDecimal applicationId;

    @Column(name = Constants.OWNR_UPDT_ID)
    BigDecimal ownershipUpdateId;
}
