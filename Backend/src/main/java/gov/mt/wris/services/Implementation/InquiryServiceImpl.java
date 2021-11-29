package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.InquiryService;

@Service
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    private MasterStaffIndexesRepository masterStaffIndexesRepository;

    @Autowired
    private WaterRightVersionRepository waterRightVersionRepository;

    @Autowired
    private WaterRightRepository waterRepo;

    /***
     *     returns 5 booleans
     *     1. is the version locked
     *     2. is the water right decreed (this is only used to display a warning message on the frontend)
     *     3. can the user edit a decreed water right
     *     4. can the user re-examine a decreed
    *      5. can the user modify a split decree
     */
    @Override
    public Map<String, Boolean> isUneditable(BigDecimal waterRightId, BigDecimal versionId, String versionType) {

        boolean isVersionLocked = waterRightVersionRepository.isWaterRightVersionLocked(waterRightId, versionId) > 0;
        boolean isDecreed = waterRepo.needsDecreePermission(waterRightId);
        boolean isEditableIfDecreed = masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.DECREE_MODIFY_ROLE)) > 0;
        boolean canReexamineDecree = masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.POST_DECREE_MODIFY_VERSION_ROLE)) > 0;
        boolean canModifySplitDecree = masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.SPLIT_DECREE_MODIFY_VERSION_ROLE)) > 0;
        
        Map<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("isVersionLocked", isVersionLocked);
        flags.put("isDecreed", isDecreed);
        flags.put("isEditableIfDecreed", isEditableIfDecreed);
        flags.put("canReexamineDecree", canReexamineDecree);
        flags.put("canModifySplitDecree", canModifySplitDecree);

        return flags;
    }

}
