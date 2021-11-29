package gov.mt.wris.models.IdClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DecreeTypeXrefId implements Serializable {
    private String eventCode;
    private String decreeCode;
    private static final long serialVersionUID = 112234L;
}
