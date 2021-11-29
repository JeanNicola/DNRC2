package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name=Constants.APPLICATION_TYPE_TABLE)
@Getter
@Setter
public class ApplicationType {
    @Id
    @Column(name=Constants.APPLICATION_TYPE_CODE)
    public String code;

    @Column(name=Constants.APPLICATION_TYPE_DESCR)
    public String description;

    @Column(name=Constants.AUTO_COMPLETE_TYPE_CODE)
    public String autoCompleteType;

    @Column(name = Constants.APPLICATION_TYPE_FILING_FEE)
    public BigDecimal filingFee;

    @Column(name = Constants.APPLICATION_TYPE_FEE_OTHER)
    public BigDecimal feeOther;

    @Column(name = Constants.APPLICATION_TYPE_FEE_DISCOUNT)
    public BigDecimal feeDiscount;

    @Column(name = Constants.APPLICATION_TYPE_FEE_CGWA)
    public BigDecimal feeCGWA;

    @Column(name = Constants.APPLICATION_TYPE_CASE_REPORT)
    public String caseReport;


    @OneToMany(mappedBy = "applicationType", fetch = FetchType.LAZY)
    public List<ApplicationTypeXref> eventTypes;

    @Column(name = Constants.APPLICATION_TYPE_OBJECTIONS_ALLOWED)
    String objectionsAllowed;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.APPLICATION_TYPE_OBJECTIONS_ALLOWED, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.YES_NO_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference objectionsAllowedReference;

}
