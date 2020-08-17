package com.roche.service.product;

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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import com.roche.service.order.Order;

/**
 * Entity implementation class for Entity: Product
 * 
 * @author amit modhvadia
 *
 */
@Entity
@Table(name = "PRODUCT")
public class Product extends RepresentationModel<Product> implements Serializable {

	private static final long serialVersionUID = 7837135508578081547L;

	/**
	 * Stock Keeping Unit ID (unique). Equivalent to ID for this product.
	 */
	@Id
	@GeneratedValue
	@Column(name = "STOCK_KEEPING_UNIT_ID")
	private Long stockKeepingUnitID;

	/**
	 * Name for the product.
	 */
	@Column(name = "PRODUCT_NAME")
	private String name;

	/**
	 * Price for the product.
	 */
	@Column(name = "PRICE")
	private Float price;

	/**
	 * Date when the product was created.
	 */
	@Column(name = "CREATION_DATE")
	private Date creationDate = new Date();

	/**
	 * Orders associated with this product.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinTable(name = "PRODUCT_ORDER", joinColumns = {
			@JoinColumn(name = "STOCK_KEEPING_UNIT_ID", referencedColumnName = "STOCK_KEEPING_UNIT_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID") })
	private List<Order> orders = new ArrayList<Order>();

	/**
	 * Flag that marks this product for deletion. Equivalent to a soft delete.
	 */
	@Column(name = "DELETION_FLAG")
	private Boolean deletionFlag = Boolean.valueOf(false);

	/**
	 * Empty constructor for Product.
	 */
	public Product() {
		super();
	}

	/**
	 * Constructor for Product.
	 * 
	 * @param name  String - Name for this product.
	 * @param price Float - Price for this product.
	 */
	public Product(String name, Float price) {
		super();
		this.name = name;
		this.price = price;
	}

	/**
	 * Get the stock keeping unit ID for this product.
	 * 
	 * @return Long Stock Keeping Unit ID
	 */
	public Long getStockKeepingUnitID() {
		return this.stockKeepingUnitID;
	}

	/**
	 * Get the name for this product.
	 * 
	 * @return String Name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name for this product.
	 * 
	 * @param name String - Name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the price for this product.
	 * 
	 * @return Float - Price.
	 */
	public Float getPrice() {
		return this.price;
	}

	/**
	 * Set the price for this product.
	 * 
	 * @param price String - Price.
	 */
	public void setPrice(Float price) {
		this.price = price;
	}

	/**
	 * Date when the product was created.
	 * 
	 * @return Date - Product creation date.
	 */
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Get the flag marking the product for deletion.
	 * 
	 * @return Boolean - Deletion flag.
	 */
	public Boolean getDeletionFlag() {
		return deletionFlag;
	}

	/**
	 * Set the flag marking the product for deletion.
	 * 
	 * @param deletionFlag Boolean - Deletion flag.
	 */
	public void setDeletionFlag(Boolean deletionFlag) {
		this.deletionFlag = deletionFlag;
	}

	/**
	 * Associate an order with this product.
	 * 
	 * @param order Order - Order to be associated with this product.
	 */
	public void addOrder(Order order) {

		orders.add(order);
	}

	/**
	 * Hash code method for Product.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.creationDate, this.name, this.price, this.stockKeepingUnitID, this.deletionFlag,
				this.orders);
	}

	/**
	 * Verify whether this product is equal to the product provided.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Product)) {
			return false;
		}
		Product other = (Product) obj;
		return Objects.equals(this.creationDate, other.creationDate) && Objects.equals(this.name, other.name)
				&& Objects.equals(this.price, other.price)
				&& Objects.equals(this.stockKeepingUnitID, other.stockKeepingUnitID)
				&& Objects.equals(this.deletionFlag, other.deletionFlag) && Objects.equals(this.orders, other.orders);
	}

	/**
	 * Convert this product into a textual representation.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Product [stockKeepingUnitID=").append(stockKeepingUnitID).append(", name=").append(name)
				.append(", price=").append(price).append(", creationDate=").append(creationDate)
				.append(", deletionFlag=").append(deletionFlag).append(", productOrders=")
				.append(orders.stream().map(Order::getOrderID).collect(Collectors.toList())).append("]");
		return builder.toString();
	}

}
