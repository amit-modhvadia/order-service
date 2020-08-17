package com.roche.service.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

/**
 * Order Service Application Test.
 * 
 * Primarily ensures the configuration for the order service application loads
 * up correctly.
 * 
 * @author amit modhvadia
 *
 */
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(properties = "spring.profiles.active=test")
class OrderServiceApplicationTest {

	@Autowired
	private OrderController orderController;

	/**
	 * Ensure the configuration for order service application loads up correctly.
	 */
	@Test
	void contextLoads() {

		assertThat(orderController).isNotNull();
	}

}
