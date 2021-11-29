package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = Constants.REMARK_CATEGORIES_TABLE)
@Getter
@Setter
public class RemarkCategory {

    @Id
    @Column(name = Constants.REMARK_CATEGORY_CODE)
    private String category;

    @Column(name = Constants.CATEGORY_DESCRIPTION)
    private String description;

    @Column(name = Constants.ELEMENT_TYPE_CODE)
    private String elementType;

    @ManyToOne(targetEntity = ElementType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ELEMENT_TYPE_CODE, referencedColumnName = Constants.ELEMENT_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    private ElementType elementTypeReference;

}
