package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class VersionApplicationXrefId implements Serializable {
    private BigDecimal applicationId;
    private BigDecimal versionId;
    private BigDecimal waterRightId;
}
