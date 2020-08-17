package com.roche.service.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

/**
 * Product Service Application Test.
 * 
 * Primarily ensures the configuration for the product service application loads
 * up correctly.
 * 
 * @author amit modhvadia
 *
 */
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(properties = "spring.profiles.active=test")
class ProductServiceApplicationTest {

	@Autowired
	private ProductController productController;

	/**
	 * Ensure the configuration for product service application loads up correctly.
	 */
	@Test
	public void contextLoads() {
		assertThat(productController).isNotNull();
	}

}
