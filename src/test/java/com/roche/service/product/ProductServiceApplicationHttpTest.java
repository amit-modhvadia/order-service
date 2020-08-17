package com.roche.service.product;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Verify the API for the Product Service.
 * 
 * @author amit modhvadia
 *
 */
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(properties = "spring.profiles.active=test", webEnvironment = WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationHttpTest {

	private static final String URL_PREFIX = "http://";
	private static final String SERVER_HOST = "localhost";
	private static final String PATH_SEPARATOR = "/";
	private static final String SERVER_PORT_SEPARATOR = ":";

	private static final String PRODUCTS_PATH = "products";

	private static final String WRONG_PATH = "product";

	private static final int OK = 200;
	private static final int CREATED = 201;
	private static final int NO_CONTENT = 204;
	private static final int NOT_FOUND = 404;

	private static final String PARACETAMOL_500_MG = "{\"name\": \"Paracetamol 500mg\",\"price\": \"5.62\"}";
	private static final String PANADOL_500_MG = "{\"name\": \"Panadol 500mg\",\"price\": \"8.29\"}";

	private static final String UPDATED_PARACETAMOL_500_MG = "{\"name\": \"Paracetamol 500mg\",\"price\": \"7.54\"}";

	private static final int NON_EXISTING_PRODUCT_ID = 99999;

	@LocalServerPort
	private int serverPort;

	@Test
	public void testCreateNewProduct() {

		Response response = createProduct(PARACETAMOL_500_MG);

		// tests
		response.then().body("stockKeepingUnitID", Matchers.any(Integer.class));
		response.then().body("name", Matchers.is("Paracetamol 500mg"));
		response.then().body("price", Matchers.equalTo(5.62f));

		response.then().body("_links.self.href", Matchers.endsWith(
				"products" + PATH_SEPARATOR + Integer.toString(response.getBody().path("stockKeepingUnitID"))));
		response.then().body("_links.products.href", Matchers.endsWith(PRODUCTS_PATH));

		response.then().statusCode(Matchers.equalTo(CREATED));
	}

	@Test
	public void testRetrieveProduct() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Integer paracetamol500MGSKUID = paracetamol500MGResponse.getBody().path("stockKeepingUnitID");

		Response retrievedProductResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(paracetamol500MGSKUID));

		retrievedProductResponse.then().body("stockKeepingUnitID",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedProductResponse.then().body("name", Matchers.equalTo(paracetamol500MGResponse.getBody().path("name")));
		retrievedProductResponse.then().body("price",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("price")));

		retrievedProductResponse.then().body("_links.self.href", Matchers.endsWith("products" + PATH_SEPARATOR
				+ Integer.toString(paracetamol500MGResponse.getBody().path("stockKeepingUnitID"))));
		retrievedProductResponse.then().body("_links.products.href", Matchers.endsWith(PRODUCTS_PATH));

		retrievedProductResponse.then().statusCode(Matchers.equalTo(OK));
	}

	@Test
	public void testRetrieveNonExistingProduct() {

		Response retrievedNonExistingProductResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_PRODUCT_ID));

		retrievedNonExistingProductResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));

	}

	@Test
	public void testRetrieveProducts() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Response panadol500MGResponse = createProduct(PANADOL_500_MG);

		Response retrievedProductsResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH);

		retrievedProductsResponse.then().body("_embedded.productList", Matchers.hasSize(2));

		verifyProductResponse(0, retrievedProductsResponse, paracetamol500MGResponse);
		verifyProductResponse(1, retrievedProductsResponse, panadol500MGResponse);

		retrievedProductsResponse.then().statusCode(Matchers.equalTo(OK));
	}

	@Test
	public void testRetrieveNoProducts() {

		// Create no products.

		Response retrievedNoProductResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH);

		retrievedNoProductResponse.then().statusCode(Matchers.equalTo(OK));

		retrievedNoProductResponse.then().body("_embedded.productList", Matchers.equalTo(null));

	}

	@Test
	public void testReplaceProduct() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Integer paracetamol500MGSKUID = paracetamol500MGResponse.getBody().path("stockKeepingUnitID");

		Response retrievedProductResponse = given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(UPDATED_PARACETAMOL_500_MG).when()
				.put(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(paracetamol500MGSKUID));

		retrievedProductResponse.then().body("stockKeepingUnitID",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedProductResponse.then().body("name", Matchers.equalTo(paracetamol500MGResponse.getBody().path("name")));

		// Only the price has changed.
		retrievedProductResponse.then().body("price", Matchers.equalTo(7.54f));

		retrievedProductResponse.then().body("_links.self.href", Matchers.endsWith("products" + PATH_SEPARATOR
				+ Integer.toString(paracetamol500MGResponse.getBody().path("stockKeepingUnitID"))));
		retrievedProductResponse.then().body("_links.products.href", Matchers.endsWith(PRODUCTS_PATH));

		retrievedProductResponse.then().statusCode(Matchers.equalTo(CREATED));
	}

	@Test
	public void testReplaceNonExistingProduct() {

		Response replaceNonExistingProductResponse = given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(UPDATED_PARACETAMOL_500_MG).when()
				.put(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_PRODUCT_ID));

		replaceNonExistingProductResponse.then().statusCode(Matchers.equalTo(NO_CONTENT));

	}

	@Test
	public void testDeleteProduct() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Integer paracetamol500MGSKUID = paracetamol500MGResponse.getBody().path("stockKeepingUnitID");

		Response deletedProductResponse = given().when()
				.delete(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(paracetamol500MGSKUID));

		deletedProductResponse.then().statusCode(Matchers.equalTo(NO_CONTENT));

		Response retrievedDeletedProductResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(paracetamol500MGSKUID));

		retrievedDeletedProductResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));
	}

	@Test
	public void testDeleteNonExistingProduct() {

		Response deleteNonExistingProductResponse = given().when()
				.delete(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_PRODUCT_ID));

		deleteNonExistingProductResponse.then().statusCode(Matchers.equalTo(NO_CONTENT));

	}

	@Test
	public void testWrongPaths() {

		Response wrongPathResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + WRONG_PATH);

		wrongPathResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));

	}

	private Response createProduct(String productBody) {
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(productBody).when()
				.post(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH);
		return response;
	}

	private void verifyProductResponse(int index, Response retrievedProductResponse, Response createdProductResponse) {

		retrievedProductResponse.then().body("_embedded.productList[" + index + "].stockKeepingUnitID",
				Matchers.equalTo(createdProductResponse.getBody().path("stockKeepingUnitID")));
		retrievedProductResponse.then().body("_embedded.productList[" + index + "].name",
				Matchers.equalTo(createdProductResponse.getBody().path("name")));
		retrievedProductResponse.then().body("_embedded.productList[" + index + "].price",
				Matchers.equalTo(createdProductResponse.getBody().path("price")));

		retrievedProductResponse.then().body("_embedded.productList[" + index + "]._links.self.href",
				Matchers.endsWith("products" + PATH_SEPARATOR
						+ Integer.toString(createdProductResponse.getBody().path("stockKeepingUnitID"))));
		retrievedProductResponse.then().body("_embedded.productList[" + index + "]._links.products.href",
				Matchers.endsWith(PRODUCTS_PATH));
	}

}