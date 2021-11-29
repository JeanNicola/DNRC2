package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name= Constants.SHARED_ELEMENT_TABLE)
@IdClass(SharedElementId.class)
@Getter
@Setter
public class SharedElement {

    @Id
    @Column(name=Constants.SHARED_ELEMENT_RELATED_RIGHT_ID)
    public BigDecimal relatedRightId;

    @Id
    @Column(name=Constants.ELEMENT_TYPE_CODE)
    public String typeCode;

    @ManyToOne(targetEntity = ElementType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ELEMENT_TYPE_CODE, referencedColumnName = Constants.ELEMENT_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    private ElementType typeCodeValue;

    @Column(name = Constants.SHARED_ELEMENT_CREATED_BY)
    public String createdBy;

    @Column(name = Constants.SHARED_ELEMENT_CREATED_DATE)
    public LocalDate createdDate;

    @Column(name = Constants.SHARED_ELEMENT_MODIFIED_BY)
    public String modifiedBy;

    @Column(name = Constants.SHARED_ELEMENT_MODIFIED_DATE)
    public LocalDate modifiedDate;

    @ManyToOne(targetEntity = RelatedRight.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.RELATED_RIGHT_ID, referencedColumnName = Constants.RELATED_RIGHT_ID, insertable = false, updatable = false, nullable = false)
    public RelatedRight relatedRight;

}