package gov.mt.wris.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RepresentativeCount {
    private BigDecimal ownerId;
    private BigDecimal secondCustomerId;
    private Long total;

    public RepresentativeCount(BigDecimal ownerId, BigDecimal secondCustomerId, Long total){
        this.ownerId=ownerId;
        this.secondCustomerId=secondCustomerId;
        this.total=total;
    }
}
