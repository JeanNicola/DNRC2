package gov.mt.wris.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.REPORT_TYPE_TABLE)
@Getter
@Setter
public class ReportType {
    @Id
    @Column(name = Constants.REPORT_TYPE_CODE)
    public String reportTypeCode;

    @Column(name = Constants.REPORT_TYPE_DESCRIPTION)
    public String description;
}
