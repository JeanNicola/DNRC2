package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name=Constants.CASE_ASSIGNMENT_TYPES_TABLE)
@Getter
@Setter
public class CaseAssignmentType {
    @Id
    @Column(name=Constants.ASSIGNMENT_CODE)
    public String code;

    @Column(name=Constants.ASSIGNMENT_TYPE)
    public String description;

    @Column(name=Constants.PROGRAM)
    public String program;

    @ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.PROGRAM, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'CASE_ASSIGNMENT_TYPE_PROGRAMS'", referencedColumnName = Constants.DOMAIN))
    })
    @Fetch(FetchMode.JOIN)
    private Reference programReference;
}
