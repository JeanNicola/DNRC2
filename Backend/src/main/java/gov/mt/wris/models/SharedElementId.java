package gov.mt.wris.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SharedElementId implements Serializable {
    private BigDecimal relatedRightId;
    private String typeCode;
    private static final long serialVersionUID = 8675309L;
}

