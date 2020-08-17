package com.roche.service.product;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.roche.service.product.exception.ProductNotFoundException;

/**
 * Maps allowed URIs for Products to methods that support the corresponding
 * URIs.
 * 
 * @author amit modhvadia
 *
 */
@RestController
public class ProductController {

	/**
	 * Repository for creating, retrieving, updating and deleting products.
	 */
	private final ProductRepository productRepository;

	/**
	 * Assembler for annotating products with referential links.
	 */
	private final ProductModelAssembler productModelAssembler;

	/**
	 * Constructor for Product Controller.
	 * 
	 * @param productRepository     ProductRepository - Repository for creating,
	 *                              retrieving, updating and deleting products.
	 * @param productModelAssembler ProductModelAssembler - Assembler for annotating
	 *                              products associated for orders with referential
	 *                              links.
	 */
	public ProductController(ProductRepository productRepository, ProductModelAssembler productModelAssembler) {

		this.productRepository = productRepository;
		this.productModelAssembler = productModelAssembler;
	}

	/**
	 * Get all the products.
	 * 
	 * Excludes products which are marked for deletion.
	 * 
	 * @return Product - All products
	 */
	@GetMapping("/products")
	public CollectionModel<EntityModel<Product>> all() {

		// Retrieve all the products that are not marked for deletion, and
		// add referential links that point back to all the product themselves and also
		// point back to the collection of products.
		List<EntityModel<Product>> products = productRepository.findByDeletionFlagFalse().stream()
				.map(productModelAssembler::toModel).collect(Collectors.toList());

		// Return a collection of products annotated with referential links
		return CollectionModel.of(products, linkTo(methodOn(ProductController.class).all()).withSelfRel());
	}

	/**
	 * Create a new product.
	 * 
	 * @param newProduct Product - New product to be created.
	 * @return Product - Product created.
	 */
	@PostMapping("/products")
	public ResponseEntity<?> newProduct(@RequestBody Product newProduct) {

		// Save a new product. Then annotate this product (to be returned) with
		// referential links that point back to the product and also point back to the
		// collection products.
		EntityModel<Product> entityModel = productModelAssembler.toModel(productRepository.save(newProduct));

		// Return the saved product annotated with referential links with a created
		// response.
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}

	/**
	 * Get a product for the Stock Keeping Unit ID provided if it is not marked for
	 * deletion.
	 * 
	 * @param stockKeepingUnitID Long - Stock Keeping Unit ID of the product to be
	 *                           returned.
	 * @return Product - Product for the Stock Keeping Unit ID provided.
	 */
	@GetMapping("/products/{stockKeepingUnitID}")
	public ResponseEntity<?> one(@PathVariable Long stockKeepingUnitID) {

		Product product;

		try {
			// Retrieve a product if it is not marked for deletion.
			product = productRepository.findById(stockKeepingUnitID)
					.filter(currentProduct -> !currentProduct.getDeletionFlag().booleanValue())
					.orElseThrow(() -> new ProductNotFoundException(stockKeepingUnitID));
		} catch (ProductNotFoundException pnfe) {

			// Return a not found response.
			return ResponseEntity.notFound().build();
		}

		// Return the product annotated with referential links with an OK response.
		return ResponseEntity.ok().body(productModelAssembler.toModel(product));
	}

	/**
	 * Replace a product with a new product.
	 * 
	 * @param newProduct         Product - Product used for replacing the existing
	 *                           product.
	 * @param stockKeepingUnitID Long - Stock Keeping Unit ID of the product that
	 *                           needs to be replaced.
	 * @return Product - Replaced product.
	 */
	@PutMapping("/products/{stockKeepingUnitID}")
	public ResponseEntity<?> replaceProduct(@RequestBody Product newProduct, @PathVariable Long stockKeepingUnitID) {

		Product updatedProduct;

		try {
			// Retrieve and replace product.
			updatedProduct = productRepository.findById(stockKeepingUnitID) //
					.map(product -> {
						product.setName(newProduct.getName());
						product.setPrice(newProduct.getPrice());
						return productRepository.save(product);
					}).orElseThrow(() -> new ProductNotFoundException(stockKeepingUnitID));
		} catch (ProductNotFoundException pnfe) {

			// Return a no content response.
			return ResponseEntity.noContent().build();
		}

		// Add referential links that point back to the product and also point back to
		// the collection products.
		EntityModel<Product> entityModel = productModelAssembler.toModel(updatedProduct);

		// Return the replaced product annotated with referential links with a created
		// response.
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	/**
	 * Delete a product for the Stock Keeping Unit ID provided by marking the
	 * product for deletion.
	 * 
	 * @param stockKeepingUnitID Long - Stock Keeping Unit ID of the product that
	 *                           needs to be deleted (i.e., a soft delete).
	 * @return No content.
	 */
	@DeleteMapping("/products/{stockKeepingUnitID}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long stockKeepingUnitID) {

		// Retrieve a product and mark it for deletion.
		productRepository.findById(stockKeepingUnitID).map(product -> {
			product.setDeletionFlag(true);
			return productRepository.save(product);
		});

		// Return a no content response.
		return ResponseEntity.noContent().build();
	}

}
