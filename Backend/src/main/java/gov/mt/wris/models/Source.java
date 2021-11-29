package gov.mt.wris.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.SOURCE_TABLE)
@Getter
@Setter
public class Source {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "source_seq"
    )
    @SequenceGenerator(
        name = "source_seq",
        sequenceName = Constants.SOURCE_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.SOURCE_ID)
    public BigDecimal id;

    @Column(name = Constants.SOURCE_FORK_NAME)
    public String forkName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = Constants.SOURCE_NAME_ID)
    public SourceName sourceName;

    @OneToMany(mappedBy = "source", cascade = CascadeType.PERSIST)
    public List<AlsoKnown> alsoKnowns;

    public void setAlsoKnowns(List<AlsoKnown> newAlsoKnowns) {
        this.alsoKnowns = new ArrayList<AlsoKnown>(newAlsoKnowns);
        for(AlsoKnown also : newAlsoKnowns) {
            also.setSource(this);
        }
    }
}
