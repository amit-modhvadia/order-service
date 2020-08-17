# Product and Order Service

Provides a RESTful API supporting the basic CRUD operations for 

## 1. Product

Create a new product.

Retrieve a list of all products.

Update a product.

Delete a product (soft deletion).


Each product has a Stock Keeping Unit ID (SKU, a unique id), a name, a price and the date it was created.

## 2. Order

Place an order.

Retrieve all orders within a given time period.


Calculate the total order amount, based on the price of the individual products.

Each order has a list of products, a unique id, the buyer’s e-mail, and the time the order was placed.


# Build, test and run the application locally

1.	Pull the code from this repository to your local directory.
2.	Navigate to your local directory.
3.	Run mvn clean install (alternatively to skip tests, run mvn clean install –DskipTests) to build the application. You will see the BUILD SUCCESS output from Maven. Now you are ready to start the application. 
4.	Optionally, run mvn test to run the tests again.
5.	Run mvn clean spring-boot:run (you will see the following on the console  …Started OrderServiceApplication in 5.143 seconds (JVM running for 5.678)). Now the application has started.
6.	Run curl http://localhost:8080/products (you will get back the 200 in the status code with no products initially).
7.	Run curl http://localhost:8080/orders (you will get back the 200 in the status code with orders initially).

These commands were run from a Windows PowerShell (x86) command prompt.

There you have it, we are now up and running.


# API Endpoints

## Product Endpoints

Endpoints for products.

### POST /products

Create a new product.

#### Endpoint URL

http://localhost:8080/products

#### JSON body parameters

name (string) Name of the product.

price (decimal) Price of the product.

#### Example request

Run the following commands in Windows PowerShell (x86) 

$Params = @{
    ContentType = 'application/json' 
    Body = '{"name":"Paracetamol 500mg","price":6.47}'
    Method = 'Post'
    URI = 'http://localhost:8080/products'
}

Invoke-RestMethod @Params

#### Example response

{
	"stockKeepingUnitID":396,
	"name":"Paracetamol 500mg",
	"price":6.47,
	"creationDate":"2020-08-17T16:43:23.907+00:00",
	"deletionFlag":false,
	"_links": {
		"self": {
			"href":"http://localhost:8080/products/396"
		},
		"products":{
			"href":"http://localhost:8080/products"
		}
	}
}

#### Response fields

stockKeepingUnitID (number) Stock Keeping Unit ID (equivalent to Product ID, however it will not be referred as a Product ID).

name (String) Name of the product.

price (decimal) Price of the product.

creationDate (date) Date the product was created.


### GET /products

Retrieve all the products.

#### Endpoint URL

http://localhost:8080/products

#### Example request

Run the following commands in Windows PowerShell (x86) 

curl http://localhost:8080/products

#### Example response

Similar to the response for POST /products, however multiple products are returned instead of one product. Please note that the list of products are found in productList field (JSON field). productList itself is found in _embedded field (JSON field).

Extract of the beginning of the example response 

{
	"_embedded":
	{
		"productList":[
		{
			"stockKeepingUnitID":391,
			"name":"Sudocream"


### PUT /products/396

Update a product.

#### Endpoint URL

http://localhost:8080/products/396

#### JSON body parameters

Similar to the parameters for POST /products.

#### Example request

Run the following commands in Windows PowerShell (x86)

$Params = @{
    ContentType = 'application/json' 
    Body = '{"name":"Paracetamol 500mg","price":7.43}'
    Method = 'Put'
    URI = 'http://localhost:8080/products/396'
}

Invoke-RestMethod @Params

#### Example response

Similar to the response for POST /products, however instead of creating the product the product values are updated.


### DELETE /products/396

Delete a product by marking the product for a soft delete.

#### Endpoint URL

http://localhost:8080/products/396

#### Example request

Run the following commands in Windows PowerShell (x86)

curl -Method DELETE http://localhost:8080/products/396


## Order Endpoints

Endpoints for orders.

### POST /orders

Place an order.

#### Endpoint URL

http://localhost:8080/orders

#### JSON body parameters

buyerEmail (string) Email address of the buyer who is placing the order.

products (array) Products for this order. In case of the placing a new order, only the Stock Keeping Unit IDs of the products are required. 

#### Example request

Run the following commands in Windows PowerShell (x86)

$Params = @{
    ContentType = 'application/json' 
    Body = '{"buyerEmail":"james.smith26@testgmail.com","products":[ {"stockKeepingUnitID":398}, {"stockKeepingUnitID":399} ]}'
    Method = 'Post'
    URI = 'http://localhost:8080/orders'
}

Invoke-RestMethod @Params

#### Example Response

{
	"orderID":401,
	"products": [
	{
		"stockKeepingUnitID":398,
		"name":"Paracetamol 500mg",
		"price":5.63,
		"creationDate":"2020-08-17T16:54:42.706+00:00",
		"deletionFlag":false,
		"_links":
		{
			"self":
			{
				"href":"http://localhost:8080/products/398"
			},
			"products":
			{
				"href":"http://localhost:8080/products"
			}
		}
	},
	{"
		stockKeepingUnitID":399,
		"name":"Panadol 500mg",
		"price":7.38,
		"creationDate":"2020-08-17T17:21:39.451+00:00",
		"deletionFlag":false,
		"_links":
		{
			"self":
			{
				"href":"http://localhost:8080/products/399"
			},
			"products":
			{
				"href":"http://localhost:8080/products"
			}
		}
	}],
	"buyerEmail":"james.smith26@testgmail.com",
	"orderPlacedTime":"2020-08-17T17:25:41.451+00:00",
	"_links":{
		"self":
		{
			"href":"http://localhost:8080/orders/401"
		},
		"orders":
		{
			"href":"http://localhost:8080/orders"
		}
	}
}

#### Response fields

orderID (number) Unique ID of the order.

buyerEmail (string) Email address of the buyer who is placing the order.

products (array) Products for this order with values of each products returned with the response.

orderPlacedTime (date) Time the order was placed.


### GET /orders/{startDate}/{endDate}

Retrieve all orders within a given time period.

#### Endpoint URL

http://localhost:8080/orders/2020-08-16T00A10/2020-08-16T13A47

#### Path parameters

startDate (string) Start date of the time period. Start date of the time period. The format of the start date in the path is yyyy-MM-ddTHHAmm. For example, 2020-08-16T00A10 would be provided in the path for a start date of '2020-08-16 00:10'. In the provided date in the path, 'T' separates the date and time components, and 'A' separates the hour and minutes. Letters 'T' and 'A' were chosen because they were URL friendly.

endDate (string) End date of the time period. End date of the time period. The format of the end date in the path is yyyy-MM-ddTHHAmm. For example, 2020-08-16T13A47 would be provided in the path for an end date of '2020-08-16 13:47'. In the provided date in the path, 'T' separates the date and time components, and 'A' separates the hour and minutes. Letters 'T' and 'A' were chosen because they were URL friendly.

#### Example request

Run the following commands in Windows PowerShell (x86)

curl http://localhost:8080/orders/2020-08-16T00A10/2020-08-16T13A47

#### Example response

Similar to the response for POST /orders, however multiple orders are returned instead of a single order. Please note that the list of orders are found in orderList field (JSON field). orderList itself is found in _embedded field (JSON field).

Extract of the beginning of the example response 

{
	"_embedded":
	{
		"orderList":[
		{
			"orderID":292,
						

### GET /orders/{orderID}/calculatetotalamount

Retrieve all orders within a given time period.

#### Endpoint URL

http://localhost:8080/orders/401/calculatetotalamount

#### Path parameters

orderID (number) Unique ID of the order.

#### Example request

Run the following commands in Windows PowerShell (x86)

curl http://localhost:8080/orders/401/calculatetotalamount

#### Example response

{
	"totalAmount":13.01
}