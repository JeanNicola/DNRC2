package gov.mt.wris.models.IdClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CaseTypeXrefId implements Serializable{
    private String eventCode;
    private String caseCode;
    private static final long serialVersionUID = 124345L;
}
