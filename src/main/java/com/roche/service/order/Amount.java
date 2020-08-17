package com.roche.service.order;

/**
 * Total Amount.
 * 
 * @author amit modhvadia
 *
 */
public class Amount {

	/**
	 * Total Amount value.
	 */
	private Float totalAmount;

	/**
	 * Empty constructor for Amount.
	 */
	public Amount() {
		super();
	}

	/**
	 * Constructor for Amount.
	 * 
	 * @param totalAmount Float - Total Amount.
	 */
	public Amount(Float totalAmount) {
		super();
		this.totalAmount = totalAmount;
	}

	/**
	 * Get Total Amount.
	 * 
	 * @return Float - Total Amount.
	 */
	public Float getTotalAmount() {
		return totalAmount;
	}

}
