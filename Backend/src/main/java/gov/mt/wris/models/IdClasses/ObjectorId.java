package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Cesar.Zamorano
 *
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ObjectorId implements Serializable {

	private static final long serialVersionUID = 512635L;
	private BigDecimal objectionId;
	private BigDecimal customerId;

}
