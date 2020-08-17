package com.roche.service.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for products so that they can be created, fetched, updated and
 * marked for deletion.
 * 
 * @author amit modhvadia
 *
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

	/**
	 * Get all the products that are not marked for deletion.
	 * 
	 * @return List<Product> - All products that are not marked for deletion.
	 */
	List<Product> findByDeletionFlagFalse();
}
