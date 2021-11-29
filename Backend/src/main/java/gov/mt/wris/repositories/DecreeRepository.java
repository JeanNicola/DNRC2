package gov.mt.wris.repositories;

import gov.mt.wris.models.Decree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DecreeRepository  extends JpaRepository<Decree, String> {

    @Query(value = " SELECT COUNT(msi) \n" +
            " FROM MasterStaffIndexes msi \n" +
            " WHERE (msi.positionCode IN ('ADPM','ISSP','WCAM') OR msi.directoryUser = 'CN3535') \n" +
            " AND msi.endDate IS NULL \n" +
            " AND msi.directoryUser = :user \n"
    )
    public int isWaterCourtAdministrator(@Param("user") String user);

    @Query(value = " SELECT d\n" +
            " FROM Decree d\n" +
            " JOIN FETCH d.decreeType dt \n" +
            " WHERE ((:basin is null)OR(d.basin LIKE :basin))\n" +
            " AND (COALESCE(d.lovItem, 'N') = :lovOnly OR d.lovItem = 'Y') \n",
            countQuery = " SELECT count(d) FROM Decree d WHERE ((:basin is null)OR(d.basin LIKE :basin)) AND (COALESCE(d.lovItem, 'N') = :lovOnly OR d.lovItem = 'Y') \n"
    )
    public Page<Decree> searchDecreeBasins(Pageable pageable, @Param("basin") String basin, @Param("lovOnly") String lovOnly);

}
