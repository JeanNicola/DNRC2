package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.SOURCE_NAME_TABLE)
@Getter
@Setter
public class SourceName {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "source_name_seq"
    )
    @SequenceGenerator(
        name = "source_name_seq",
        sequenceName = Constants.SOURCE_NAME_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.SOURCE_NAME_ID)
    public BigDecimal id;

    @Column(name = Constants.SOURCE_NAME)
    public String name;
}
