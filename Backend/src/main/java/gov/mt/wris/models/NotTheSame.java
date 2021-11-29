package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table(name = Constants.NOT_THE_SAMES_TABLE)
@Getter
@Setter
public class NotTheSame {

    @Id
    @Column(name = Constants.NOT_THE_SAME_ID)
    private BigInteger notthesameId;


}
