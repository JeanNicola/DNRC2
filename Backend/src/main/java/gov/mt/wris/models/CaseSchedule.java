package gov.mt.wris.models;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.CaseScheduleId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.CASE_SCHEDULE_TABLE)
@IdClass(CaseScheduleId.class)
@Getter
@Setter
public class CaseSchedule {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "case_sch_seq"
    )
    @SequenceGenerator(
            name = "case_sch_seq",
            sequenceName = Constants.CASE_SCHEDULE_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.SCHEDULE_ID)
    public BigDecimal scheduleId;

    @Id
    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

    @Column(name = Constants.EVENT_TYPE_CODE)
    public String eventTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.EVENT_TYPE_CODE, referencedColumnName = Constants.EVENT_TYPE_CODE, nullable = false, updatable = false, insertable = false)
    public EventType eventType;

    @Column(name = Constants.SCHEDULE_STATUS)
    public String scheduleStatus;

    @Column(name = Constants.SCHEDULE_DATE)
    public LocalDateTime scheduleDate;

    @Column(name = Constants.SCHEDULE_BEGIN_TIME)
    public LocalDateTime scheduleBeginTime;

    @Column(name = Constants.SCHEDULE_COMMENTS)
    public String scheduleComments;

}
