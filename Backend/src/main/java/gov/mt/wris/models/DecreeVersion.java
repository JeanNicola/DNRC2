package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.DecreeVersionId;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.DECREE_VERSION_TABLE)
@IdClass(DecreeVersionId.class)
@Getter
@Setter
public class DecreeVersion {
    @Id
    @Column(name = Constants.DECREE_ID)
    public Long decreeId;

    @Id
    @Column(name = Constants.WATER_RIGHT_ID)
    public Long waterRightId;

    @Id
    @Column(name = Constants.VERSION_ID)
    public Long versionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = Constants.DECREE_ID,
        referencedColumnName = Constants.DECREE_ID,
        insertable = false,
        updatable = false,
        nullable = false
    )
    private Decree decree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = Constants.VERSION_ID, referencedColumnName = Constants.VERSION_ID, insertable = false, updatable = false, nullable = false),
        @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false)
    })
    private WaterRightVersion version;

    @Column(name = Constants.DECREE_VERSION_MISSED_IN_DECREE)
    public String missedInDecree;
}
