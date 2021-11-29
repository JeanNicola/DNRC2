package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

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
import java.math.BigDecimal;

@Entity
@Table(name= Constants.ELEMENT_OBJECTION_TABLE)
@Getter
@Setter
public class ElementObjection {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "elem_obj_seq"
    )
    @SequenceGenerator(
            name = "elem_obj_seq",
            sequenceName = Constants.ELEMENT_OBJECTION_SEQUENCE,
            allocationSize = 1
    )
    @Column(name=Constants.ELEMENT_OBJECTION_ID)
    public BigDecimal id;

    @Column(name=Constants.OBJECTIONS_ID)
    public BigDecimal objectionId;

    @Column(name=Constants.ELEMENT_TYPE_CODE)
    public String type;

    @ManyToOne(targetEntity = ElementType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ELEMENT_TYPE_CODE, referencedColumnName = Constants.ELEMENT_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    private ElementType typeReference;

    @Column(name=Constants.ELEMENT_OBJECTION_COMMENT)
    public String comment;

}
