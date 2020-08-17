package com.roche.service.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Order service application.
 * 
 * Use this spring boot application to run the whole application.
 * 
 * Please do not use the product service application.
 * 
 * @author amit modhvada
 *
 */
@SpringBootApplication(scanBasePackages = "com.roche.service")
public class OrderServiceApplication {

	/**
	 * Main method for the Order Service spring boot application.
	 * 
	 * @param args String [] - arguments provided to this application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
