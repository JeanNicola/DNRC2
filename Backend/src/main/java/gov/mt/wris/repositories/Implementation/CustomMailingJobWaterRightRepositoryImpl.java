package gov.mt.wris.repositories.Implementation;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.mt.wris.repositories.CustomMailingJobWaterRightRepository;

public class CustomMailingJobWaterRightRepositoryImpl implements CustomMailingJobWaterRightRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomMailingJobWaterRightRepository.class);

    @PersistenceContext
    private EntityManager manager;

    @Transactional
    public int addMailingJobWaterRights(Long mailingJobId, HashMap<String, HashMap<String, List<String>>> waterRights) {
        LOGGER.info("Adding Multiple Water Rights to a Mailing Job");

        String importedWaterRightConditions = waterRights.entrySet().stream()
            .map(basinExtensions -> {
                return String.format("(\nBOCA_CD = '%s' AND %s\n)", basinExtensions.getKey(),
                    String.format(basinExtensions.getValue().size() > 1 ? "(%s)" : "%s", basinExtensions.getValue().entrySet().stream()
                        .map(extWaterRights -> {
                            // use 'is null' or '='
                            String extCondition = String.format("%s", extWaterRights.getKey() == null ? "is null" : String.format("= '%s'", extWaterRights.getKey()));
                            // use '=' or 'in' depending on length
                            String waterRightList = String.join(", ", extWaterRights.getValue());
                            String waterRightCondition = String.format(extWaterRights.getValue().size() > 1 ? "in (%s)" : "= %s", waterRightList);
                            return String.format("\nEXT %s AND WTR_ID %s", extCondition, waterRightCondition);
                        }).collect(Collectors.joining(" OR"))
                    )
                );
            }).collect(Collectors.joining("\nOR "));
        
        String nonDuplicateCondition = "\nAND WRGT_ID_SEQ not in (\n" +
            "\tSELECT WRGT_ID_SEQ\n" +
            "\tFROM WRD_MAILING_LBL_JB_WTRRT_XREFS\n" +
            "\tWHERE MLJB_ID_SEQ = :mailingJobId\n" +
            "\n)";
        
        String query = "INSERT INTO WRD_MAILING_LBL_JB_WTRRT_XREFS (MLJB_ID_SEQ, WRGT_ID_SEQ)\n" +
            "SELECT :mailingJobId, WRGT_ID_SEQ\n" +
            "FROM WRD_WATER_RIGHTS\n" +
            "WHERE (\n" + importedWaterRightConditions + "\n)\n" +
            nonDuplicateCondition;
        
        Query q = manager.createNativeQuery(query);
        q.setParameter("mailingJobId", mailingJobId);

        int result = q.executeUpdate();

        return result;
    }
}
