package gov.mt.wris.models.IdClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTypeXrefId implements Serializable{
    private String eventCode;
    private String applicationCode;
    private static final long serialVersionUID = 111111L;
}
