package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = Constants.REMARK_CODES_TABLE)
@Getter
@Setter
public class RemarkCode {

    @Id
    @Column(name = Constants.REMARK_CODE)
    private String code;

    @Column(name = Constants.REMARK_CATEGORY_CODE)
    private String category;

    @ManyToOne(targetEntity = RemarkCategory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.REMARK_CATEGORY_CODE, referencedColumnName = Constants.REMARK_CATEGORY_CODE, insertable = false, updatable = false, nullable = false)
    private RemarkCategory categoryReference;

    @Column(name = Constants.REMARK_CODE_STATUS)
    private String status;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.REMARK_CODE_STATUS, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.REMARK_STATUS_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference statusReference;

    @Column(name = Constants.REMARK_TYPE)
    private String type;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.REMARK_TYPE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.REMARK_TYPE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference typeReference;

    @OneToMany(mappedBy = "remarkCodeLibrary")
    private List<Variable> variables;

    @Column(name = Constants.BASIN)
    public String basin;

    @Column(name = Constants.CUSTOMER_ID)
    public BigDecimal customerId;

    @Column(name = Constants.CLOSURE_ID)
    public BigDecimal closureId;
}
