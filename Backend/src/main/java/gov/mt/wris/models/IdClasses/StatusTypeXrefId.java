package gov.mt.wris.models.IdClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StatusTypeXrefId implements Serializable{
    public String typeCode;
    public String statusCode;
    private static final long serialVersionUID = 134323L;
}
