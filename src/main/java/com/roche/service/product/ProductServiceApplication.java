package com.roche.service.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Order service application.
 * 
 * Please do not use this application as part of this assignment.
 * 
 * Only use it for reference and testing.
 * 
 * @author amit modhvadia
 *
 */
@SpringBootApplication(scanBasePackages = "com.roche.service")
public class ProductServiceApplication {

	/**
	 * Main method for the Product Service spring boot application.
	 * 
	 * @param args String [] - arguments provided to this application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
