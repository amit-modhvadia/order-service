package com.roche.service.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import com.roche.service.product.Product;

/**
 * Entity implementation class for Entity: Order.
 * 
 * @author amit modhvadia
 *
 */
@Entity
@Table(name = "ROCHE_ORDER")
public class Order extends RepresentationModel<Order> implements Serializable {

	private static final long serialVersionUID = 9196730150595301723L;

	/**
	 * Order ID (unique).
	 */
	@Id
	@GeneratedValue
	@Column(name = "ORDER_ID")
	private Long orderID;

	/**
	 * Products for this order.
	 */
	@ManyToMany(mappedBy = "orders", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<Product>();

	/**
	 * Email address of the buyer for this order.
	 */
	@Column(name = "BUYER_EMAIL")
	private String buyerEmail;

	/**
	 * Time the order was placed.
	 */
	@Column(name = "ORDER_PLACED_TIME")
	private Date orderPlacedTime = new Date();

	/**
	 * Empty constructor for Order.
	 */
	public Order() {
		super();
	}

	/**
	 * Constructor for Order.
	 * 
	 * @param buyerEmail String - Email of the buyer.
	 */
	public Order(String buyerEmail) {
		super();
		this.buyerEmail = buyerEmail;
	}

	/**
	 * Get the Order ID for this order.
	 * 
	 * @return Long - Order ID
	 */
	public Long getOrderID() {
		return this.orderID;
	}

	/**
	 * Get the products for this order.
	 * 
	 * @return List<Product> - Products for this order.
	 */
	public List<Product> getProducts() {

		return this.products;
	}

	/**
	 * Get the email address of the buyer for this order.
	 * 
	 * @return String - Email address of the buyer.
	 */
	public String getBuyerEmail() {
		return this.buyerEmail;
	}

	/**
	 * Set the email address of the buyer for this order.
	 * 
	 * @param buyerEmail String - Email address of the buyer.
	 */
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	/**
	 * Get the time of the order placed.
	 * 
	 * @return
	 */
	public Date getOrderPlacedTime() {
		return this.orderPlacedTime;
	}

	/**
	 * Add a product to this order.
	 * 
	 * @param product Product - Product to be added.
	 */
	public void addProduct(Product product) {

		product.addOrder(this);

		products.add(product);
	}

	/**
	 * Hash code method for Order.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.buyerEmail, this.orderID, this.orderPlacedTime, this.products);
	}

	/**
	 * Verify whether this order is equal to the order provided.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Order)) {
			return false;
		}
		Order other = (Order) obj;
		return Objects.equals(buyerEmail, other.buyerEmail) && Objects.equals(orderID, other.orderID)
				&& Objects.equals(orderPlacedTime, other.orderPlacedTime) && Objects.equals(products, other.products);
	}

	/**
	 * Convert this order into a textual representation.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Order [orderID=").append(orderID).append(", products=")
				.append(products.stream().map(Product::getStockKeepingUnitID).collect(Collectors.toList()))
				.append(", buyerEmail=").append(buyerEmail).append(", orderPlacedTime=").append(orderPlacedTime)
				.append("]");
		return builder.toString();
	}

}
