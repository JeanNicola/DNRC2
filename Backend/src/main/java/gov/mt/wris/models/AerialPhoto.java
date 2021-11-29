package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = Constants.AERIAL_TABLE)
@Getter
@Setter
public class AerialPhoto implements Serializable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "apto_id_seq"
    )
    @SequenceGenerator(
            name = "apto_id_seq",
            sequenceName = Constants.AERIAL_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.AERIAL_ID)
    private BigDecimal aerialId;

    @Column(name = Constants.POU_EXAMINATION_ID)
    private BigDecimal pexmId;

    @ManyToOne(targetEntity = PouExamination.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false)
    public PouExamination pouExamination;

    @Column(name = Constants.AERIAL_TYPE)
    public String typeCode;

    @Column(name = Constants.AERIAL_NUMBER)

    public String number;
    @Column(name = Constants.AERIAL_DATE)
    public String date;

    @OneToMany(mappedBy = "aerialPhoto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PlaceOfUseExaminationXref> placesOfUse;

}
