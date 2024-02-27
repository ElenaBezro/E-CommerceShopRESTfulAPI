

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
    * [Built With](#built-with)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Clone the Repository](#clone-the-repository)
    * [Set Up MySQL Database](#setup-mysql-database)
    * [Build and Run Project](#build-and-run-project)
* [Documentation](#documentation)
* [Features](#features)
    * [Authentication](#authentication)
    * [Product Management](#product-management)
    * [Cart Management](#cart-management)
    * [Order Management](#order-management)


<!-- ABOUT THE PROJECT -->
## About The Project


The project will be an online shop platform. This platform will help customers to explore products, manage products in the cart, create orders and see orders history.


<b>This platform consists of 4 main modules:</b>
* Authentication management
* Products management
* Cart items management
* Orders management


### Built With
* Java 17
* Spring Boot 3
* MySQL
* Maven


<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running, follow these simple example steps.

### Prerequisites

Ensure that you have Java installed on your machine.
Install MySQL on your machine.
Use an IDE like IntelliJ IDEA, Eclipse or Spring Tool Suite.

### Clone the Repository

1. Open a terminal or command prompt.
2. Navigate to the folder where you want to place the project.
3. Run the following command to clone the repository:
```sh
git clone https://github.com/ElenaBezro/E-CommerceShopRESTfulAPI
```
### Set Up MySQL Database

Configure your Spring Boot application properties to specify the database connection details, such as the database URL, username, and password.
1. Change the spring.profiles.active property in the application.properties file to dev.
2. Rename secrets-dev-example.properties to secrets-dev.properties and secrets-test-example.properties to secrets-test.properties.
3. Fill in secrets-dev.properties and secrets-test.properties with your database URL, username, and password. Make sure to use different names for the dev and test databases.
4. Fill in secrets-dev.properties and secrets-test.properties with the username, password, and email for your superuser.
5. Ensure that you have specified your test database name in the src/test/resources/db/dropDB.sql file:
```sh
DROP DATABASE IF EXISTS <your_test_database_name>;
```

### Build and Run Project

1. Navigate to the root folder of your project.
2. Run the following command to build the project:
```sh
mvn clean install
```
3. Run the following command to run the project:
```sh
mvn spring-boot:run
```
4. Navigate to http://localhost:8080 to access the application.

<!-- DOCUMENTATION -->
## Documentation

You can access Swagger UI by navigating to http://localhost:8080/swagger-ui/index.html after starting the application. Alternatively, you can access the Swagger UI for the deployed application at https://zalandorestfulapi.ew.r.appspot.com/swagger-ui/index.html.

<!-- FEATURES -->
## Features

### Authentication

1. '/auth/login' POST: Allows users to log in to the application.
2. '/auth/register' POST: Allows users to register for a new account.

### Product Management

1. '/products' POST: Allows users to add new products.
2. '/products?pageNumber&pageSize' GET: Allows users to view the available products and their details with pagination.
3. '/products?pageNumber&pageSize&sort' GET: Allows users to view the available products and their details with pagination and sorting.
4. '/products/{id}' DELETE: Allows users to delete products.
5. '/products/{id}' PUT: Allows users to update product details.

### Cart Management

1. '/cart-items' POST: Allows users to add items to their cart.
2. '/cart-items/{id}' PUT: Allows users to update the quantity of items in their cart.
3. '/cart-items/{id}' DELETE: Allows users to remove items from their cart.
4. '/cart-items' GET: Allows users to view the items in their cart.

### Order Management

1. '/orders' GET: Allows users to view their order history and current orders.
2. '/orders' POST: Allows users to create a new order.
3. '/orders/{orderId}' PATCH: Allows users to update the status of an order.
