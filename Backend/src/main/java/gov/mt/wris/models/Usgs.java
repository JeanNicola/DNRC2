package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = Constants.USGS_TABLE)
@Getter
@Setter
public class Usgs {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "utmp_id_seq"
    )
    @SequenceGenerator(
            name = "utmp_id_seq",
            sequenceName = Constants.USGS_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.USGS_ID)
    public BigDecimal utmpId;

    @Column(name = Constants.USGS_NAME)
    public String name;

    @Column(name = Constants.USGS_DATE)
    public LocalDate date;

    @OneToMany(mappedBy = "usgs", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PouExamUsgsMapXref> usgsXrefs;
}
