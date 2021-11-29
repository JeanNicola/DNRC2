package gov.mt.wris.security;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Cesar.Zamorano
 *
 */
@Getter
@Setter
public class UserData {

	private String firstName;

	private String midInitial;

	private String lastName;

	private String databaseEnv;

	private BigDecimal officeId;
}
