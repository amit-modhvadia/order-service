package com.roche.service.product.exception;

/**
 * Product Not Found Exception.
 * 
 * @author amit modhvadia
 *
 */
public class ProductNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1674758377021049817L;

	/**
	 * Product not found message.
	 */
	private static final String PRODUCT_NOT_FOUND_MESSAGE = "Could not find product ";

	/**
	 * Constructor for Product Not Found Exception.
	 * 
	 * @param stockKeepingUnitID Long - Stock Keeping Unit ID of the product that
	 *                           was not found.
	 */
	public ProductNotFoundException(Long stockKeepingUnitID) {
		super(PRODUCT_NOT_FOUND_MESSAGE + stockKeepingUnitID);
	}

}
