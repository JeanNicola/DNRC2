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
@Table(name = Constants.ALSO_KNOWN_TABLE)
@Getter
@Setter
public class AlsoKnown {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "also_seq"
    )
    @SequenceGenerator(
        name = "also_seq",
        sequenceName = Constants.ALSO_KNOWN_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.ALSO_KNOWN_ID)
    public BigDecimal id;

    @Column(name = Constants.ALSO_KNOWN_NAME)
    public String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SOURCE_ID)
    public Source source;
}
