package com.roche.service.order;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating referential links that point back to the
 * selected order and also point back to all of the collection of orders.
 * 
 * @author amit modhvadia
 *
 */
@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

	/**
	 * Add links to an order pointing back to itself and also point back to the
	 * collection of the orders.
	 */
	@Override
	public EntityModel<Order> toModel(Order order) {

		return EntityModel.of(order, linkTo(methodOn(OrderController.class).one(order.getOrderID())).withSelfRel(),
				linkTo(methodOn(OrderController.class).all()).withRel("orders"));
	}

}
