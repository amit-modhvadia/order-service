package com.roche.service.order;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.roche.service.order.exception.OrderNotFoundException;
import com.roche.service.product.Product;
import com.roche.service.product.ProductController;
import com.roche.service.product.ProductModelAssembler;
import com.roche.service.product.ProductRepository;

/**
 * Maps allowed URIs for Orders to methods that support the corresponding URIs.
 * 
 * @author amit modhvadia
 *
 */
@RestController
public class OrderController {

	/**
	 * Date format used for retrieving orders within a time period.
	 */
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	/**
	 * Time Zone Code.
	 */
	private static final String UTC_TIMEZONE_CODE = "UTC";

	/**
	 * Separates the Date and Time components for a date.
	 */
	private static final String TIME_COMPONENT_SEPARATOR = " ";

	/**
	 * Separates the Date and Time components for the date provided.
	 */
	private static final String REQUEST_TIME_COMPONENT_SEPARATOR = "T";

	/**
	 * Separates the hours and minutes for a date.
	 */
	private static final String TIME_SEPARATOR = ":";

	/**
	 * Separates the hours and minutes for the date provided.
	 */
	private static final String REQUEST_TIME_SEPARATOR = "A";

	/**
	 * Repository for creating, retrieving and updating orders.
	 */
	private final OrderRepository orderRepository;

	/**
	 * Assembler for annotating orders with referential links.
	 */
	private final OrderModelAssembler orderModelAssembler;

	/**
	 * Repository for creating, retrieving, updating and deleting products.
	 */
	private final ProductRepository productRepository;

	/**
	 * Assembler for annotating products with referential links.
	 */
	private final ProductModelAssembler productModelAssembler;

	/**
	 * Constructor for Order Controller.
	 * 
	 * @param orderRepository       OrderRepository - Repository for creating,
	 *                              retrieving and updating orders.
	 * @param orderModelAssembler   OrderModelAssembler - Assembler for annotating
	 *                              orders with referential links.
	 * @param productRepository     ProductRepository - Repository for creating,
	 *                              retrieving, updating and deleting products.
	 * @param productModelAssembler ProductModelAssembler - Assembler for annotating
	 *                              products associated for orders with referential
	 *                              links.
	 */
	public OrderController(OrderRepository orderRepository, OrderModelAssembler orderModelAssembler,
			ProductRepository productRepository, ProductModelAssembler productModelAssembler) {

		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		this.orderModelAssembler = orderModelAssembler;
		this.productModelAssembler = productModelAssembler;
	}

	/**
	 * Get all the orders.
	 * 
	 * @return Order - All orders
	 */
	@GetMapping("/orders")
	public CollectionModel<Order> all() {

		// Retrieve all the orders.
		List<Order> orders = orderRepository.findAll();

		for (Order order : orders) {

			// Add referential links that point back to the order and also point back to the
			// collection of orders.
			addOrderLinks(order);

			// Add referential links that point back to all the product themselves and also
			// point back to the collection of products.
			addLinksToProductsForAnOrder(order);

		}

		// Return the collection of orders annotated with referential links.
		return CollectionModel.of(orders, linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}

	/**
	 * Gets all the orders placed within the time period provided.
	 * 
	 * Example path - /orders/2020-08-16T00A10/2020-08-16T13A47
	 * 
	 * @param startDate String - Start date of the time period. The format of the
	 *                  start date in the path is yyyy-MM-ddTHHAmm. For example,
	 *                  2020-08-16T00A10 would be provided in the path for a start
	 *                  date of '2020-08-16 00:10'. In the provided date in the
	 *                  path, 'T' separates the date and time components, and 'A'
	 *                  separates the hour and minutes. Letters 'T' and 'A' were
	 *                  chosen because they are URL friendly.
	 * @param endDate   String - End date of the time period. The format of the end
	 *                  date in the path is yyyy-MM-ddTHHAmm. For example,
	 *                  2020-08-16T00A10 would be provided in the path for a end
	 *                  date of '2020-08-16 00:10'. In the provided date in the
	 *                  path, 'T' separates the date and time components, and 'A'
	 *                  separates the hour and minutes. Letters 'T' and 'A' were
	 *                  chosen because they are URL friendly.
	 * @return Order - Orders within the specified time period.
	 */
	@GetMapping("/orders/{startDate}/{endDate}")
	public ResponseEntity<?> filteredOrdersByTimePeriod(@PathVariable String startDate, @PathVariable String endDate) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_CODE));

		// Convert String startDate and endDate into valid date objects.
		Date startDateObject = null;
		Date endDateObject = null;

		try {
			;
			startDateObject = dateFormat
					.parse(startDate.replaceFirst(REQUEST_TIME_COMPONENT_SEPARATOR, TIME_COMPONENT_SEPARATOR)
							.replaceFirst(REQUEST_TIME_SEPARATOR, TIME_SEPARATOR));
		} catch (ParseException pe) {

			// Return a bad request response.
			return ResponseEntity.badRequest().build();
		}

		try {
			endDateObject = dateFormat
					.parse(endDate.replaceFirst(REQUEST_TIME_COMPONENT_SEPARATOR, TIME_COMPONENT_SEPARATOR)
							.replaceFirst(REQUEST_TIME_SEPARATOR, TIME_SEPARATOR));
		} catch (ParseException pe) {

			// Return a bad request response.
			return ResponseEntity.badRequest().build();
		}

		// Get orders placed between the start date and the end date.
		List<Order> orders = orderRepository.findByOrderPlacedTimeBetween(startDateObject, endDateObject);

		for (Order order : orders) {

			// Add referential links that point back to the order and also point back to the
			// collection of orders.
			addOrderLinks(order);

			// Add referential links that point back to all the product themselves and also
			// point back to the collection of products.
			addLinksToProductsForAnOrder(order);

		}

		// Return the collection of orders found within the time period, annotated with
		// referential links with an OK response.
		return ResponseEntity.ok()
				.body(CollectionModel.of(orders, linkTo(methodOn(OrderController.class).all()).withSelfRel()));

	}

	/**
	 * Place an order.
	 * 
	 * @param newOrder Order - New order to be placed.
	 * @return Order - order placed.
	 */
	@PostMapping("/orders")
	public ResponseEntity<?> newOrder(@RequestBody Order newOrder) {

		List<Product> newProducts = new ArrayList<Product>();
		newProducts.addAll(newOrder.getProducts());

		newOrder.getProducts().clear();

		for (Product product : newProducts) {

			// Retrieve a product
			Product retrievedProduct = productRepository.findById(product.getStockKeepingUnitID()).orElseThrow();

			// Add and associate this product with the new order.
			newOrder.addProduct(retrievedProduct);
		}

		// Save the order.
		Order savedOrder = orderRepository.save(newOrder);

		// Add referential links that point back to all the product themselves and also
		// point back to the collection of products.
		addLinksToProductsForAnOrder(savedOrder);

		// Add referential links that point back to the order and also point back to the
		// collection of orders.
		EntityModel<Order> entityModel = orderModelAssembler.toModel(savedOrder);

		// Return the order placed annotated with referential links with a created
		// response.
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	/**
	 * Get an order for the order ID provided.
	 * 
	 * @param orderID Long - Order ID of the order to returned.
	 * @return Order - Order for the order ID provided.
	 */
	@GetMapping("/orders/{orderID}")
	public ResponseEntity<?> one(@PathVariable Long orderID) {

		Order order;

		try {
			// Retrieve an order.
			order = orderRepository.findById(orderID).orElseThrow(() -> new OrderNotFoundException(orderID));
		} catch (OrderNotFoundException onfe) {

			// Retrieve a not found response.
			return ResponseEntity.notFound().build();
		}

		// Add referential links that point back to the order and also point back to the
		// collection of orders.
		addOrderLinks(order);

		// Add referential links that point back to all the product themselves and also
		// point back to the collection of products.
		addLinksToProductsForAnOrder(order);

		// Return the order found annotated with referential links with an OK response.
		return ResponseEntity.ok().body(EntityModel.of(order));

	}

	/**
	 * Get the total price of an order.
	 * 
	 * @param orderID Long - Order ID of the order for which total amount of its
	 *                products will be calculated.
	 * @return Float - Total amount of the products for the requested order.
	 */
	@GetMapping("/orders/{orderID}/calculatetotalamount")
	public ResponseEntity<?> totalAmount(@PathVariable Long orderID) {

		Order order;

		try {
			// Retrieve an order.
			order = orderRepository.findById(orderID).orElseThrow(() -> new OrderNotFoundException(orderID));
		} catch (OrderNotFoundException onfe) {

			// Return a not found response.
			return ResponseEntity.notFound().build();
		}

		// Calculate and return the total price amount for the order with an OK
		// response.
		return ResponseEntity.ok().body(new Amount(calculateTotalOrderAmount(order)));

	}

	/**
	 * Get all the products for an order.
	 * 
	 * @param orderID Long - Order ID of the requested order.
	 * @return Product - All products for the requested order.
	 */
	@GetMapping("/orders/{orderID}/products")
	public ResponseEntity<?> allOrderProducts(@PathVariable Long orderID) {

		Order order;

		// Retrieve an order.
		try {
			order = orderRepository.findById(orderID).orElseThrow(() -> new OrderNotFoundException(orderID));
		} catch (OrderNotFoundException onfe) {
			return ResponseEntity.notFound().build();
		}

		// Add referential links that point back to all the product themselves and also
		// point back to the collection of products.
		List<EntityModel<Product>> products = order.getProducts().stream().map(productModelAssembler::toModel)
				.collect(Collectors.toList());

		// Return the products found for an order annotated with referential links.
		return ResponseEntity.ok()
				.body(CollectionModel.of(products, linkTo(methodOn(OrderController.class).all()).withSelfRel()));
	}

	/**
	 * Replace an order with a new order.
	 * 
	 * @param newOrder Order - Order used for replacing the existing order.
	 * @param orderID  Long - Order ID of the order that needs to be replaced.
	 * @return Order - Replaced order.
	 */
	@PutMapping("/orders/{orderID}")
	public ResponseEntity<?> replaceOrder(@RequestBody Order newOrder, @PathVariable Long orderID) {

		Order updatedOrder;

		try {
			// Retrieve and update the order.
			updatedOrder = orderRepository.findById(orderID) //
					.map(order -> {
						order.setBuyerEmail(newOrder.getBuyerEmail());
						return orderRepository.save(order);
					}).orElseThrow(() -> new OrderNotFoundException(orderID));
		} catch (OrderNotFoundException onfe) {

			// Return no content response as no order was found for replacement.
			return ResponseEntity.noContent().build();
		}

		// Add referential links that point back to all the product themselves and also
		// point back to the collection of products.
		addLinksToProductsForAnOrder(updatedOrder);

		// Add referential links that point back to the order and also point back to the
		// collection of orders.
		EntityModel<Order> entityModel = orderModelAssembler.toModel(updatedOrder);

		// Return the order replaced annotated with referential links with a created
		// response.
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}

	/**
	 * Add referential links that point back to the order and also point back to the
	 * collection of orders.
	 * 
	 * @param order Order - Order to be annotated with links
	 */
	private void addOrderLinks(Order order) {
		order.add(linkTo(methodOn(OrderController.class).one(order.getOrderID())).withSelfRel(),
				linkTo(methodOn(OrderController.class).all()).withRel("orders"));
	}

	/**
	 * Add referential links that point back to all the product themselves and also
	 * point back to the collection of products.
	 * 
	 * @param order Order - Products of this order to be annotated with links.
	 */
	private void addLinksToProductsForAnOrder(Order order) {
		for (Product product : order.getProducts()) {

			addProductLinks(product);
		}
	}

	/**
	 * Add referential links that point back to the product itself and also point
	 * back to collection of products.
	 * 
	 * @param product Product - Product to be annotated with links.
	 */
	private void addProductLinks(Product product) {
		if (product.hasLinks())
			return;

		product.add(linkTo(methodOn(ProductController.class).one(product.getStockKeepingUnitID())).withSelfRel(),
				linkTo(methodOn(ProductController.class).all()).withRel("products"));
	}

	/**
	 * Calculate the total price amount for all of the products for the order
	 * provided.
	 * 
	 * @param order Order - Order for which the total price amount for all its
	 *              products needs to be calculated.
	 * @return Float - Total price amount for the order provided.
	 */
	private Float calculateTotalOrderAmount(Order order) {
		float totalOrderAmount = 0.0f;

		for (Product product : order.getProducts()) {

			totalOrderAmount = totalOrderAmount + product.getPrice();
		}

		// Return the total price amount for an order.
		return Float.valueOf(totalOrderAmount);
	}
}
