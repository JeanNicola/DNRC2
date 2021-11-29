package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.REMARK_VARIABLE_TABLE)
@Getter
@Setter
public class Variable {
    @Id
    @Column(name = Constants.REMARK_VARIABLE_ID)
    public BigDecimal variableId;

    @Column(name = Constants.REMARK_CODE)
    public String remarkCode;

    @Column(name = Constants.REMARK_VARIABLE_NUMBER)
    public BigDecimal variableNumber;

    @Column(name = Constants.REMARK_VARIABLE_PRECEDING_TEXT)
    public String precedingText;

    @Column(name = Constants.REMARK_VARIABLE_TRAILING_TEXT)
    public String trailingText;

    @Column(name = Constants.REMARK_VARIABLE_TYPE)
    public String typeCode;

    @Column(name = Constants.REMARK_VARIABLE_TABLE_NAME)
    public String table;

    @Column(name = Constants.REMARK_VARIABLE_COLUMN_NAME)
    public String column;

    @Column(name = Constants.REMARK_VARIABLE_LENGTH)
    public BigDecimal length;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.REMARK_CODE, referencedColumnName = Constants.REMARK_CODE, insertable = false, updatable = false, nullable = false)
    public RemarkCode remarkCodeLibrary;
}
