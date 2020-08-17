package com.roche.service.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.roche.service.order.exception.OrderNotFoundException;

/**
 * Handler to deal with scenarios when an order is not found.
 * 
 * @author amit modhvadia
 *
 */
@ControllerAdvice
public class OrderNotFoundAdvice {

	/**
	 * Return a HTTP Status of Not Found (code - 404) when an order is not found.
	 * 
	 * @param orderNotFoundException OrderNotFoundException - Exception for order
	 *                               not found.
	 * @return String - Message for order not found exception.
	 */
	@ResponseBody
	@ExceptionHandler(OrderNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String orderNotFoundHandler(OrderNotFoundException orderNotFoundException) {
		return orderNotFoundException.getMessage();
	}

}
