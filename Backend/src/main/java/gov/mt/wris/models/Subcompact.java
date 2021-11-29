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
@Table(name = Constants.SUBCOMPACT_TABLE)
@Getter
@Setter
public class Subcompact {
    @Id
    @Column(name = Constants.SUBCOMPACT_ID)
    public BigDecimal id;

    @Column(name = Constants.SUBCOMPACT_NAME)
    public String name;

    @ManyToOne(targetEntity = Compact.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.COMPACT_ID, referencedColumnName = Constants.COMPACT_ID, insertable = false, updatable = false, nullable = false)
    public Compact compact;
}
