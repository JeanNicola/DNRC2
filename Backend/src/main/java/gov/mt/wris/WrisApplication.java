package gov.mt.wris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import gov.mt.wris.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@EnableConfigurationProperties(SecurityProperties.class)
public class WrisApplication {

	public static void main(String[] args) {
		SpringApplication.run(WrisApplication.class, args);
	}
}
