package gov.mt.wris.repositories;

/**
 * Custom repository for pageable event searches.
 *
 * @author Vannara Houth
 */
public interface CustomEventRepository {


    /**
     * @param applicationId
     * @return
     */
    public int callReinstate(Long applicationId);

    /**
     * @param applicationId
     * @return
     */
    public String callDoesApplWrHaveGeocodeYn(String applicationId);


    /**
     * NEED TO QUESTION CHANGE AUTHORIZATIONS FOR CHANGING WATER RIGHT STATUS
     * GOOD TO GO FOR VERSION 1, BUT ADDITIONAL VERSIONS MAY BE QUESTIONABLE ON CHANGING THE WATER RIGHT STATUS
     * SEE DOCUMENTATION BELOW
     *
     * @param applicationId
     * @return
     */
    public String postInsertUpdateStatusToActive(String applicationId);


}
