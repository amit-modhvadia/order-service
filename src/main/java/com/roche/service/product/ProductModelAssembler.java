package com.roche.service.product;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * 
 * Utility class for generating referential links that point back to the
 * selected product and also point back to the collection of the products.
 * 
 * @author amit modhvadia
 *
 */
@Component
public class ProductModelAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {

	/**
	 * Add links to a product pointing back to itself and also point back to the
	 * collection of the products.
	 */
	@Override
	public EntityModel<Product> toModel(Product product) {

		return EntityModel.of(product,
				linkTo(methodOn(ProductController.class).one(product.getStockKeepingUnitID())).withSelfRel(),
				linkTo(methodOn(ProductController.class).all()).withRel("products"));
	}

}
