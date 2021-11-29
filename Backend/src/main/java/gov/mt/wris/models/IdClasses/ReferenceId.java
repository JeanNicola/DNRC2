package gov.mt.wris.models.IdClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceId implements Serializable{
    private String domain;
    private String value;
    private static final long serialVersionUID = 71430L;
}
