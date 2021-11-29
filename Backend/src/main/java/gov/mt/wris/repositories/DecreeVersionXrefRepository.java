package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.DecreeVersion;
import gov.mt.wris.models.IdClasses.DecreeVersionId;

public interface DecreeVersionXrefRepository
    extends JpaRepository<DecreeVersion, DecreeVersionId> {
    @Query(
        value =
            "SELECT ref\n" +
            "FROM DecreeVersion ref\n" +
            "JOIN FETCH ref.decree decree\n" +
            "JOIN FETCH decree.decreeType type\n" +
            "WHERE ref.waterRightId = :id\n" +
            "AND ref.versionNumber = :version\n",
        countQuery =
            "SELECT COUNT(ref)\n" +
            "FROM DecreeVersion ref\n" +
            "WHERE ref.waterRightId = :id\n" +
            "AND ref.versionNumber = :version\n"
    )
    public Page<DecreeVersion> findByWaterRightIdAndVersionId(
        Pageable pageable,
        @Param("id") Long waterRightId,
        @Param("version") Long versionId
    );
}
