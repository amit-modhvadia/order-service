package com.roche.service.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.roche.service.product.exception.ProductNotFoundException;

/**
 * Handler to deal with scenarios when a product is not found.
 * 
 * @author amit modhvadia
 *
 */
@ControllerAdvice
public class ProductNotFoundAdvice {

	/**
	 * Return a HTTP Status of Not Found (code - 404) when a product is not found.
	 * 
	 * @param productNotFoundException ProductNotFoundException - Exception for
	 *                                 product not found.
	 * @return String - Message for product not found exception.
	 */
	@ResponseBody
	@ExceptionHandler(ProductNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String productNotFoundHandler(ProductNotFoundException productNotFoundException) {
		return productNotFoundException.getMessage();
	}
}
