package com.roche.service.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for orders so that they can be created, fetched and updated.
 * 
 * @author amit modhvadia
 *
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

	/**
	 * Get all the order place within the time period provided.
	 * 
	 * @param startDate Date - Start date of the time period.
	 * @param endDate   Date - End date of the time period.
	 * @return List<Order> - All orders placed between the start date and end date.
	 */
	List<Order> findByOrderPlacedTimeBetween(Date startDate, Date endDate);
}
