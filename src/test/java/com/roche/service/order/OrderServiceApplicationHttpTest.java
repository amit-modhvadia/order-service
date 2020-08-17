package com.roche.service.order;

import static io.restassured.RestAssured.given;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
 * Verify the API for the Order Service.
 * 
 * @author amit modhvadia
 *
 */
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(properties = "spring.profiles.active=test", webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationHttpTest {

	private static final String URL_PREFIX = "http://";
	private static final String SERVER_HOST = "localhost";
	private static final String PATH_SEPARATOR = "/";
	private static final String SERVER_PORT_SEPARATOR = ":";

	private static final String ORDERS_PATH = "orders";
	private static final String PRODUCTS_PATH = "products";
	private static final String TOTAL_AMOUNT_PATH = "calculatetotalamount";

	private static final String WRONG_PATH = "order";
	private static final String TOTAL_AMOUNT_WRONG_PATH = "calculateamount";

	private static final int OK = 200;
	private static final int CREATED = 201;
	private static final int NO_CONTENT = 204;
	private static final int BAD_REQUEST = 400;
	private static final int NOT_FOUND = 404;

	private static final String JOHN_TURNER_ORDER = "{\"buyerEmail\":\"john.turner@testgmail.com\",\"products\":[ {\"stockKeepingUnitID\":1}, {\"stockKeepingUnitID\":2} ]}";
	private static final String STEVE_SMITH_ORDER = "{\"buyerEmail\":\"steve.smith@testgmail.com\",\"products\":[ {\"stockKeepingUnitID\":2} ]}";

	private static final String PARACETAMOL_500_MG = "{\"name\": \"Paracetamol 500mg\",\"price\": \"5.62\"}";
	private static final String PANADOL_500_MG = "{\"name\": \"Panadol 500mg\",\"price\": \"8.29\"}";

	private static final int NON_EXISTING_ORDER_ID = 99999;

	private static final String UPDATED_JOHN_TURNER_ORDER = "{\"buyerEmail\":\"john.turner@testoutlook.com\"}";

	private static final float TOTAL_ORDER_AMOUNT_EXPECTED = 5.62f + 8.29f;

	@LocalServerPort
	private int serverPort;

	@Test
	public void testPlaceNewOrder() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Response panadol500MGResponse = createProduct(PANADOL_500_MG);

		Response johnTunerOrderResponse = createOrder(JOHN_TURNER_ORDER);

		johnTunerOrderResponse.then().body("buyerEmail", Matchers.equalTo("john.turner@testgmail.com"));

		johnTunerOrderResponse.then().body("products[0].stockKeepingUnitID",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("stockKeepingUnitID")));
		johnTunerOrderResponse.then().body("products[0].name", Matchers.equalTo("Paracetamol 500mg"));
		johnTunerOrderResponse.then().body("products[0].price", Matchers.equalTo(5.62f));

		johnTunerOrderResponse.then().body("products[1].stockKeepingUnitID",
				Matchers.equalTo(panadol500MGResponse.getBody().path("stockKeepingUnitID")));
		johnTunerOrderResponse.then().body("products[1].name", Matchers.equalTo("Panadol 500mg"));
		johnTunerOrderResponse.then().body("products[1].price", Matchers.equalTo(8.29f));

		johnTunerOrderResponse.then().statusCode(Matchers.equalTo(CREATED));
	}

	@Test
	public void testRetrieveOrder() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Response panadol500MGResponse = createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);
		Integer johnTurnerOrderID = johnTurnerOrderResponse.getBody().path("orderID");

		Response retrievedOrderResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(johnTurnerOrderID));

		retrievedOrderResponse.then().body("buyerEmail", Matchers.equalTo("john.turner@testgmail.com"));

		retrievedOrderResponse.then().body("products[0].stockKeepingUnitID",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrderResponse.then().body("products[0].name", Matchers.equalTo("Paracetamol 500mg"));
		retrievedOrderResponse.then().body("products[0].price", Matchers.equalTo(5.62f));

		retrievedOrderResponse.then().body("products[1].stockKeepingUnitID",
				Matchers.equalTo(panadol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrderResponse.then().body("products[1].name", Matchers.equalTo("Panadol 500mg"));
		retrievedOrderResponse.then().body("products[1].price", Matchers.equalTo(8.29f));

		retrievedOrderResponse.then().statusCode(Matchers.equalTo(OK));

	}

	@Test
	public void testRetrieveNonExistingOrder() {

		Response retrievedNonExistingOrderResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_ORDER_ID));

		retrievedNonExistingOrderResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));

	}

	@Test
	public void testRetrieveOrders() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Response panadol500MGResponse = createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);
		Response steveSmithOrderResponse = createOrder(STEVE_SMITH_ORDER);

		Response retrievedOrdersResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH);

		retrievedOrdersResponse.then().body("_embedded.orderList[0].orderID",
				Matchers.equalTo(johnTurnerOrderResponse.getBody().path("orderID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].buyerEmail",
				Matchers.equalTo("john.turner@testgmail.com"));

		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[0].stockKeepingUnitID",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[0].name",
				Matchers.equalTo("Paracetamol 500mg"));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[0].price", Matchers.equalTo(5.62f));

		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[1].stockKeepingUnitID",
				Matchers.equalTo(panadol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[1].name",
				Matchers.equalTo("Panadol 500mg"));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[1].price", Matchers.equalTo(8.29f));

		retrievedOrdersResponse.then().body("_embedded.orderList[1].orderID",
				Matchers.equalTo(steveSmithOrderResponse.getBody().path("orderID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[1].buyerEmail",
				Matchers.equalTo("steve.smith@testgmail.com"));

		retrievedOrdersResponse.then().body("_embedded.orderList[1].products[0].stockKeepingUnitID",
				Matchers.equalTo(panadol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[1].products[0].name",
				Matchers.equalTo("Panadol 500mg"));
		retrievedOrdersResponse.then().body("_embedded.orderList[1].products[0].price", Matchers.equalTo(8.29f));

		retrievedOrdersResponse.then().statusCode(Matchers.equalTo(OK));

	}

	@Test
	public void testRetrieveNoOrders() {

		// Create no orders.

		Response retrievedNoOrdersResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH);

		retrievedNoOrdersResponse.then().statusCode(Matchers.equalTo(OK));

		retrievedNoOrdersResponse.then().body("_embedded.orderList", Matchers.equalTo(null));

	}

	@Test
	public void testReplaceOrder() {

		createProduct(PARACETAMOL_500_MG);
		createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);

		Response retrievedOrderResponse = given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(UPDATED_JOHN_TURNER_ORDER).when()
				.put(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(johnTurnerOrderResponse.getBody().path("orderID")));

		retrievedOrderResponse.then().body("orderID",
				Matchers.equalTo(johnTurnerOrderResponse.getBody().path("orderID")));
		retrievedOrderResponse.then().body("buyerEmail", Matchers.equalTo("john.turner@testoutlook.com"));

		retrievedOrderResponse.then().statusCode(Matchers.equalTo(CREATED));
	}

	@Test
	public void testReplaceNonExistingOrder() {

		Response retrievedNonExistingOrderResponse = given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(UPDATED_JOHN_TURNER_ORDER).when()
				.put(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_ORDER_ID));

		retrievedNonExistingOrderResponse.then().statusCode(Matchers.equalTo(NO_CONTENT));
	}

	@Test
	public void testRetrieveOrdersWithinTimePeriod() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Response panadol500MGResponse = createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);
		Response steveSmithOrderResponse = createOrder(STEVE_SMITH_ORDER);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		GregorianCalendar startDateCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		startDateCalendar.add(Calendar.DATE, -1);
		Date startDate = startDateCalendar.getTime();

		String startDateString = dateFormat.format(startDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		GregorianCalendar endDateDalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		endDateDalendar.add(Calendar.DATE, 1);
		Date endDate = endDateDalendar.getTime();

		String endDateString = dateFormat.format(endDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		Response retrievedOrdersResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + startDateString + PATH_SEPARATOR + endDateString);

		retrievedOrdersResponse.then().body("_embedded.orderList[0].orderID",
				Matchers.equalTo(johnTurnerOrderResponse.getBody().path("orderID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].buyerEmail",
				Matchers.equalTo("john.turner@testgmail.com"));

		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[0].stockKeepingUnitID",
				Matchers.equalTo(paracetamol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[0].name",
				Matchers.equalTo("Paracetamol 500mg"));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[0].price", Matchers.equalTo(5.62f));

		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[1].stockKeepingUnitID",
				Matchers.equalTo(panadol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[1].name",
				Matchers.equalTo("Panadol 500mg"));
		retrievedOrdersResponse.then().body("_embedded.orderList[0].products[1].price", Matchers.equalTo(8.29f));

		retrievedOrdersResponse.then().body("_embedded.orderList[1].orderID",
				Matchers.equalTo(steveSmithOrderResponse.getBody().path("orderID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[1].buyerEmail",
				Matchers.equalTo("steve.smith@testgmail.com"));

		retrievedOrdersResponse.then().body("_embedded.orderList[1].products[0].stockKeepingUnitID",
				Matchers.equalTo(panadol500MGResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrdersResponse.then().body("_embedded.orderList[1].products[0].name",
				Matchers.equalTo("Panadol 500mg"));
		retrievedOrdersResponse.then().body("_embedded.orderList[1].products[0].price", Matchers.equalTo(8.29f));

		retrievedOrdersResponse.then().statusCode(Matchers.equalTo(OK));
	}

	@Test
	public void testRetrieveNoOrdersWithinTimePeriod() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		GregorianCalendar startDateCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		startDateCalendar.add(Calendar.DATE, -1);
		Date startDate = startDateCalendar.getTime();

		String startDateString = dateFormat.format(startDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		GregorianCalendar endDateDalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		endDateDalendar.add(Calendar.DATE, 1);
		Date endDate = endDateDalendar.getTime();

		String endDateString = dateFormat.format(endDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		Response retrievedNoOrdersResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + startDateString + PATH_SEPARATOR + endDateString);

		retrievedNoOrdersResponse.then().statusCode(Matchers.equalTo(OK));

		retrievedNoOrdersResponse.then().body("_embedded.orderList", Matchers.equalTo(null));
	}

	@Test
	public void testRetrieveOrdersWithinTimePeriodWithBadlyFormedStartDate() {

		createProduct(PARACETAMOL_500_MG);
		createProduct(PANADOL_500_MG);

		createOrder(JOHN_TURNER_ORDER);
		createOrder(STEVE_SMITH_ORDER);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		GregorianCalendar startDateCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		startDateCalendar.add(Calendar.DATE, -1);
		Date startDate = startDateCalendar.getTime();

		String startDateString = dateFormat.format(startDate).replaceFirst(" ", "T").replaceFirst(":", "A");
		String startDateBadlyFormedString = startDateString.replaceFirst("T", "");

		GregorianCalendar endDateDalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		endDateDalendar.add(Calendar.DATE, 1);
		Date endDate = endDateDalendar.getTime();

		String endDateString = dateFormat.format(endDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		Response retrievedOrdersWithBadlyFormedStartDateResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + startDateBadlyFormedString + PATH_SEPARATOR + endDateString);

		retrievedOrdersWithBadlyFormedStartDateResponse.then().statusCode(Matchers.equalTo(BAD_REQUEST));
	}

	@Test
	public void testRetrieveOrdersWithinTimePeriodWithBadlyFormedEndDate() {

		createProduct(PARACETAMOL_500_MG);
		createProduct(PANADOL_500_MG);

		createOrder(JOHN_TURNER_ORDER);
		createOrder(STEVE_SMITH_ORDER);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		GregorianCalendar startDateCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		startDateCalendar.add(Calendar.DATE, -1);
		Date startDate = startDateCalendar.getTime();

		String startDateString = dateFormat.format(startDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		GregorianCalendar endDateDalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		endDateDalendar.add(Calendar.DATE, 1);
		Date endDate = endDateDalendar.getTime();

		String endDateString = dateFormat.format(endDate).replaceFirst(" ", "T").replaceFirst(":", "A");
		String endDateBadlyFormedString = endDateString.replaceFirst("T", "");

		Response retrievedOrdersWithBadlyFormedEndDateResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + startDateString + PATH_SEPARATOR + endDateBadlyFormedString);

		retrievedOrdersWithBadlyFormedEndDateResponse.then().statusCode(Matchers.equalTo(BAD_REQUEST));
	}

	@Test
	public void testRetrieveOrdersWithinTimePeriodWithNoEndDate() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		GregorianCalendar startDateCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		startDateCalendar.add(Calendar.DATE, -1);
		Date startDate = startDateCalendar.getTime();

		String startDateString = dateFormat.format(startDate).replaceFirst(" ", "T").replaceFirst(":", "A");

		Response retrievedOrdersWithNoEndDateResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + startDateString);

		retrievedOrdersWithNoEndDateResponse.then().statusCode(Matchers.equalTo(BAD_REQUEST));
	}

	@Test
	public void testRetrieveAllOrderProducts() {

		Response paracetamol500MGResponse = createProduct(PARACETAMOL_500_MG);
		Response panadol500MGResponse = createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);

		Response retrievedOrderResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(johnTurnerOrderResponse.getBody().path("orderID"))
						+ PATH_SEPARATOR + PRODUCTS_PATH);

		verifyProductResponse(0, retrievedOrderResponse, paracetamol500MGResponse);
		verifyProductResponse(1, retrievedOrderResponse, panadol500MGResponse);

		retrievedOrderResponse.then().statusCode(Matchers.equalTo(OK));

	}

	@Test
	public void testRetrieveNonExistingOrderProducts() {

		Response retrievedNonExistingOrderResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_ORDER_ID) + PATH_SEPARATOR + PRODUCTS_PATH);

		retrievedNonExistingOrderResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));
	}

	@Test
	public void testTotalOrderAmount() {

		createProduct(PARACETAMOL_500_MG);
		createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);

		Response retrievedOrderAmountResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(johnTurnerOrderResponse.getBody().path("orderID"))
						+ PATH_SEPARATOR + TOTAL_AMOUNT_PATH);

		retrievedOrderAmountResponse.then().body("totalAmount", Matchers.equalTo(TOTAL_ORDER_AMOUNT_EXPECTED));

		retrievedOrderAmountResponse.then().statusCode(Matchers.equalTo(OK));
	}

	@Test
	public void testTotalAmountForNonExistingOrder() {

		Response retrievedNonExistingOrderAmountResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(NON_EXISTING_ORDER_ID) + PATH_SEPARATOR
						+ TOTAL_AMOUNT_PATH);

		retrievedNonExistingOrderAmountResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));
	}

	@Test
	public void testTotalAmountWithWrongPath() {

		createProduct(PARACETAMOL_500_MG);
		createProduct(PANADOL_500_MG);

		Response johnTurnerOrderResponse = createOrder(JOHN_TURNER_ORDER);

		Response retrievedOrderAmountWrongPathResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH
						+ PATH_SEPARATOR + Integer.toString(johnTurnerOrderResponse.getBody().path("orderID"))
						+ PATH_SEPARATOR + TOTAL_AMOUNT_WRONG_PATH);

		retrievedOrderAmountWrongPathResponse.then().statusCode(Matchers.equalTo(BAD_REQUEST));
	}

	@Test
	public void testWrongPaths() {

		Response wrongPathResponse = given().accept(ContentType.JSON).when()
				.get(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + WRONG_PATH);

		wrongPathResponse.then().statusCode(Matchers.equalTo(NOT_FOUND));

	}

	private void verifyProductResponse(int index, Response retrievedOrderResponse, Response createdProductResponse) {

		retrievedOrderResponse.then().body("_embedded.productList[" + index + "].stockKeepingUnitID",
				Matchers.equalTo(createdProductResponse.getBody().path("stockKeepingUnitID")));
		retrievedOrderResponse.then().body("_embedded.productList[" + index + "].name",
				Matchers.equalTo(createdProductResponse.getBody().path("name")));
		retrievedOrderResponse.then().body("_embedded.productList[" + index + "].price",
				Matchers.equalTo(createdProductResponse.getBody().path("price")));

		retrievedOrderResponse.then().body("_embedded.productList[" + index + "]._links.self.href",
				Matchers.endsWith("products" + PATH_SEPARATOR
						+ Integer.toString(createdProductResponse.getBody().path("stockKeepingUnitID"))));
		retrievedOrderResponse.then().body("_embedded.productList[" + index + "]._links.products.href",
				Matchers.endsWith(PRODUCTS_PATH));
	}

	private Response createProduct(String productBody) {
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(productBody).when()
				.post(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + PRODUCTS_PATH);
		return response;
	}

	private Response createOrder(String orderBody) {
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderBody).when()
				.post(URL_PREFIX + SERVER_HOST + SERVER_PORT_SEPARATOR + serverPort + PATH_SEPARATOR + ORDERS_PATH);
		return response;
	}
}
