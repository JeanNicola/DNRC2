package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name=Constants.CASE_TYPE_TABLE)
@Getter
@Setter
public class CaseType {
    @Id
    @Column(name=Constants.CASE_TYPE_CODE)
    public String code;

    @Column(name=Constants.CASE_TYPE_DESCR)
    public String description;

    @Column(name=Constants.CASE_TYPE_PROGRAM)
    public String program;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.PROGRAM, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula= @JoinFormula(value = "'CASE_TYPE_PROGRAMS'", referencedColumnName = Constants.DOMAIN))
    })
    // @Fetch(FetchMode.JOIN)
    private Reference programReference;
}
