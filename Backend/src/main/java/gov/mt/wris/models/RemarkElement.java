package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = Constants.REMARK_ELEMENTS_TABLE)
@Getter
@Setter
public class RemarkElement {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "remark_element_sequence"
    )
    @SequenceGenerator(
        name = "remark_element_sequence",
        sequenceName = Constants.REMARK_ELEMENTS_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.REMARK_ELEMENTS_ID)
    public BigDecimal id;

    @Column(name = Constants.REMARK_ELEMENTS_VALUE)
    public String value;

    @Column(name = Constants.REMARKS_ID)
    public BigDecimal remarkId;

    @Column(name = Constants.REMARK_VARIABLE_ID)
    public BigDecimal variableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.REMARK_VARIABLE_ID, referencedColumnName = Constants.REMARK_VARIABLE_ID, insertable = false, nullable = false, updatable = false)
    public Variable variable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.REMARKS_ID, referencedColumnName = Constants.REMARKS_ID, insertable = false, updatable = false, nullable = false)
    public VersionRemark remark;
}
