package com.roche.service.order.exception;

/**
 * Order Not Found Exception.
 * 
 * @author amit modhvadia
 *
 */
public class OrderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1503305273438000817L;

	/**
	 * Order not found message.
	 */
	private static final String ORDER_NOT_FOUND_MESSAGE = "Could not find order ";

	/**
	 * Constructor for Order Not Found Exception.
	 * 
	 * @param orderID Long - Order ID of the order that was not found.
	 */
	public OrderNotFoundException(Long orderID) {
		super(ORDER_NOT_FOUND_MESSAGE + orderID);
	}
}
