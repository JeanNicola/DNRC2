package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RelatedRightVersionId implements Serializable {
    private BigDecimal relatedRightId;
    private BigDecimal waterRightId;
    private BigDecimal versionId;
}