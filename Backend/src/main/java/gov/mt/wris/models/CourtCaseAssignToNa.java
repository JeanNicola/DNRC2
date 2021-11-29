package gov.mt.wris.models;

import java.math.BigDecimal;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

@Entity
@Table(name = Constants.WATER_COURT_CASE_TYPES_TABLE)
@Getter
@Setter
public class CourtCaseAssignToNa {

    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(value = "( \n" +
            " SELECT MAX(MSI.DNRC_ID) \n" +
            " FROM WRD_CASE_ASSIGNMENTS CA \n" +
            "    , WRD_MASTER_STAFF_INDEXES MSI \n" +
            " WHERE CA.DNRC_ID = MSI.DNRC_ID \n" +
            "    AND (CA.ASST_CD = 'HREX' OR CA.ASST_CD = 'OAHE')  \n" +
            "    AND CA.CASE_ID_SEQ = case_id_seq \n" +
            "    AND CA.ASSN_END_DT IS NULL \n" +
            "    AND CA.ASSN_BGN_DT = (SELECT MAX(CA2.ASSN_BGN_DT) \n" +
            "                          FROM WRD_CASE_ASSIGNMENTS CA2 \n" +
            "                          WHERE CA2.CASE_ID_SEQ = CA.CASE_ID_SEQ \n" +
            "                             AND (CA2.ASST_CD = 'HREX' OR CA2.ASST_CD = 'OAHE') \n" +
            "                             AND CA2.ASSN_END_DT IS NULL) )\n")
    public MasterStaffIndexes assignedTo;

}
