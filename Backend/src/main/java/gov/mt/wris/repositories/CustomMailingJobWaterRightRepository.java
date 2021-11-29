package gov.mt.wris.repositories;

import java.util.HashMap;
import java.util.List;

public interface CustomMailingJobWaterRightRepository {
    public int addMailingJobWaterRights(Long mailingJobId, HashMap<String, HashMap<String, List<String>>> waterRights);
}
