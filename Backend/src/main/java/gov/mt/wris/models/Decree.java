package gov.mt.wris.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = Constants.DECREE_TABLE)
@Getter
@Setter
public class Decree {
    @Id
    @Column(name = Constants.DECREE_ID)
    public String id;

    @Column(name = Constants.DECREE_TYPE_CODE)
    public String decreeTypeCode;

    @Formula("(SELECT max(e.dt_of_evnt) FROM WRD_EVENT_DATES e WHERE e.EVTP_CD = 'DISS' AND e.decr_id_seq = DECR_ID_SEQ)")
    public LocalDate issuedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = Constants.DECREE_TYPE_CODE,
        referencedColumnName = Constants.DECREE_TYPE_CODE,
        insertable = false,
        updatable = false,
        nullable = false
    )
    private DecreeType decreeType;

    @Column(name = Constants.BASIN)
    public String basin;

    @OneToMany(mappedBy = "decree", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Event> events; 

    @Column(name = Constants.LOV_ITEM)
    public String lovItem;

    @Formula(value = "( \n" +
            " SELECT e.DT_OF_EVNT \n" +
            " FROM WRD_DECREES d \n" +
            " LEFT OUTER JOIN WRD_EVENT_DATES e \n" +
            "     ON e.DECR_ID_SEQ=d.DECR_ID_SEQ and e.EVTP_CD='DISS' \n" +
            " where d.DECR_ID_SEQ = DECR_ID_SEQ \n " +
            ")")
    public LocalDateTime decreeIssuedDate;

}
