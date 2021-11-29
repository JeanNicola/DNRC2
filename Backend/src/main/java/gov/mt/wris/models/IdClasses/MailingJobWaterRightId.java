package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MailingJobWaterRightId implements Serializable{
    private BigDecimal mailingJobId;
    private BigDecimal waterRightId;
    private static final long serialVersionUID = 124346L;
}
