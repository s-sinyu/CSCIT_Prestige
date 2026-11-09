package com.prestige.renovation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point of the Prestige renovation platform.
 *
 * The code follows the Boundary-Control-Entity (BCE) pattern:
 * <ul>
 *   <li>{@code boundary} - one REST boundary class per user story (HTTP only)</li>
 *   <li>{@code control}  - one controller class per user story (business rules, ownership checks)</li>
 *   <li>{@code entity}   - domain entities holding data and their data-access methods</li>
 * </ul>
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class RenovationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RenovationApplication.class, args);
    }
}
