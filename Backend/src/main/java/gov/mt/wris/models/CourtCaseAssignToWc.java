package gov.mt.wris.models;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;

@Entity
@Table(name = Constants.WATER_COURT_CASE_TYPES_TABLE)
@Getter
@Setter
public class CourtCaseAssignToWc {

    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(value = "( \n" +
            " SELECT MAX(MSI.DNRC_ID) \n" +
            " FROM WRD_CASE_ASSIGNMENTS CA \n" +
            "    , WRD_MASTER_STAFF_INDEXES MSI \n" +
            " WHERE CA.DNRC_ID = MSI.DNRC_ID \n" +
            "    AND CA.ASST_CD = 'WCMR'   \n" +
            "    AND CA.CASE_ID_SEQ = case_id_seq \n" +
            "    AND CA.ASSN_END_DT IS NULL \n" +
            "    AND CA.ASSN_BGN_DT = (SELECT MAX(CA2.ASSN_BGN_DT) \n" +
            "                          FROM WRD_CASE_ASSIGNMENTS CA2 \n" +
            "                          WHERE CA2.CASE_ID_SEQ = CA.CASE_ID_SEQ \n" +
            "                             AND CA2.ASST_CD = 'WCMR'  \n" +
            "                             AND CA2.ASSN_END_DT IS NULL) )\n")
    public MasterStaffIndexes assignedTo;

}
