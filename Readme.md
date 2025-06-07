# Car Purchase REST Service

This project develops a RESTful API for managing car purchases, connecting buyers, sellers, and cars. It's built with
Spring Boot and designed to provide basic CRUD operations for all core entities.

---

## Project Overview

The Car Purchase Service facilitates the buying and selling of cars through a set of RESTful endpoints. It aims to
provide a clear and functional API for managing the entire car purchase lifecycle, from listing cars by sellers to
tracking purchases by buyers.

---

## Technologies Used

* **Framework:** Spring Boot
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **API Documentation:** Swagger UI (OpenAPI UI)
* **Testing:** JUnit, Mockito
* **Logging:** SLF4J with Logback
* **Code Quality:** SonarQube, JaCoCo
* **Object Mapping (Optional):** MapStruct or ModelMapper (can be integrated if needed for DTO-DAO conversions)

---

# Car Purchase Service Database Schema

## Core Entities and Their Relationships

### Entities

We've made some meaningful assumptions about the properties for each entity:

* **Buyer**
    * `id` (Long - Primary Key)
    * `firstName` (String)
    * `lastName` (String)
    * `email` (String - Unique)
    * `phoneNumber` (String)

---

* **Seller**
    * `id` (Long - Primary Key)
    * `companyName` (String)
    * `email` (String - Unique)
    * `phoneNumber` (String)

---

* **Car**
    * `id` (Long - Primary Key)
    * `make` (String)
    * `model` (String)
    * `year` (Integer)
    * `price` (Double)
    * `isAvailable` (Boolean - Default: `True`)
    * `sellerId` (Long - Foreign Key to Seller)

---

* **PurchasedCar**
    * `id` (Long - Primary Key)
    * `purchaseDate` (LocalDate)
    * `carId` (Long - Foreign Key to Car - Unique, once purchased, a car cannot be purchased again)
    * `buyerId` (Long - Foreign Key to Buyer)
    * `sellerId` (Long - Foreign Key to Seller)

---

* **Car Image**
    * `id` (Long - Primary Key)
    * `carId` (Long - Foreign Key to Car - Unique, once purchased, a car cannot be purchased again)
        * `ImageName` (String)
        * `ImageUri` (String)

---

* **Review**
    * `id` (Long - Primary Key)
    * `rate` (String)
    * `feedback` (String)
    * `buyerID` (FK - Buyer entity)
    * `CarID` (FK - car entity)

---

* **User**
    * `id` (Long - Primary Key)
    * `userName` (String)
    * `password` (String)
    * `role` (String)

---

### Entity Relationships diagram

![Entity Diagram](Entity_diagram.png)

This repository contains the SQL schema for a simple car sales platform. It models entities like buyers, sellers, cars,
car images, reviews, and user authentication, along with the relationships between them.

---

## Table of Contents

* [Database Overview](#database-overview)
* [Entities and Relationships](#entities-and-relationships)
    * [Buyer](#buyer)
    * [Seller](#seller)
    * [Car](#car)
    * [Car_image](#car_image)
    * [Review](#review)
    * [User](#user)
    * [purchased_car](#purchased_car)
* [Foreign Key Relationships](#foreign-key-relationships)

---

## Database Overview

This schema is designed to support the core functionalities of a car sales application, including:

* **User Management:** Differentiating between buyers and sellers, and handling general user authentication.
* **Car Listings:** Storing details about cars for sale and their associated images.
* **Transactions:** Recording details of purchased cars.
* **Feedback:** Allowing buyers to review cars.

---

## Entities and Relationships

Below is a detailed description of each table (entity) in the database.

### `Buyer`

Represents individual users who purchase cars.

| Column       | Type      | Constraints   | Description                     |
|:-------------|:----------|:--------------|:--------------------------------|
| `id`         | `INTEGER` | `PRIMARY KEY` | Unique identifier for the buyer |
| `first_name` | `VARCHAR` | `NOT NULL`    | Buyer's first name              |
| `last_name`  | `VARCHAR` | `NOT NULL`    | Buyer's last name               |
| `email`      | `VARCHAR` | `NOT NULL`    | Buyer's email address (unique)  |
| `phone`      | `VARCHAR` | `NOT NULL`    | Buyer's phone number            |

### `Seller`

Represents companies or individuals selling cars.

| Column         | Type      | Constraints   | Description                            |
|:---------------|:----------|:--------------|:---------------------------------------|
| `id`           | `INTEGER` | `PRIMARY KEY` | Unique identifier for the seller       |
| `company_name` | `VARCHAR` | `NOT NULL`    | Name of the selling company/individual |
| `email`        | `VARCHAR` | `NOT NULL`    | Seller's email address (unique)        |
| `phone`        | `VARCHAR` | `NOT NULL`    | Seller's phone number                  |

### `Car`

Stores details about cars available for sale.

| Column        | Type           | Constraints   | Description                                          |
|:--------------|:---------------|:--------------|:-----------------------------------------------------|
| `id`          | `INTEGER`      | `PRIMARY KEY` | Unique identifier for the car                        |
| `model`       | `VARCHAR`      | `NOT NULL`    | Car model (e.g., "Civic", "F-150")                   |
| `year`        | `VARCHAR`      | `NOT NULL`    | Manufacturing year of the car                        |
| `price`       | `DECIMAL(8,2)` | `NOT NULL`    | Selling price of the car                             |
| `isAvailable` | `BOOLEAN`      | `NOT NULL`    | Indicates if the car is currently available for sale |
| `seller_id`   | `INTEGER`      | `NOT NULL`    | `FOREIGN KEY` to `Seller` table                      |

### `Car_image`

Stores image URLs and public IDs for each car.

| Column      | Type      | Constraints   | Description                                      |
|:------------|:----------|:--------------|:-------------------------------------------------|
| `id`        | `INTEGER` | `PRIMARY KEY` | Unique identifier for the image                  |
| `car_id`    | `INTEGER` | `NOT NULL`    | `FOREIGN KEY` to `Car` table                     |
| `public_id` | `VARCHAR` | `NOT NULL`    | Public ID of the image (e.g., for cloud storage) |
| `image_url` | `TEXT`    | `NOT NULL`    | URL of the car image                             |

### `Review`

Stores feedback and ratings provided by buyers for cars.

| Column     | Type      | Constraints   | Description                      |
|:-----------|:----------|:--------------|:---------------------------------|
| `id`       | `INTEGER` | `PRIMARY KEY` | Unique identifier for the review |
| `car_id`   | `INTEGER` | `NOT NULL`    | `FOREIGN KEY` to `Car` table     |
| `rate`     | `INTEGER` | `NOT NULL`    | Rating for the car (e.g., 1-5)   |
| `feedback` | `TEXT`    | `NOT NULL`    | Detailed feedback text           |
| `buyer_id` | `INTEGER` | `NOT NULL`    | `FOREIGN KEY` to `Buyer` table   |

### `User`

Handles authentication and roles for users (potentially linking to `Buyer` or `Seller` via email).

| Column      | Type      | Constraints   | Description                                  |
|:------------|:----------|:--------------|:---------------------------------------------|
| `id`        | `INTEGER` | `PRIMARY KEY` | Unique identifier for the user               |
| `user_name` | `VARCHAR` | `NOT NULL`    | User's login username (e.g., email)          |
| `password`  | `VARCHAR` | `NOT NULL`    | Hashed password                              |
| `role`      | `VARCHAR` | `NOT NULL`    | User role (e.g., "Buyer", "Seller", "Admin") |

**Note on `User` foreign keys:** The schema indicates `user_name` can reference both `Seller.email` and `Buyer.email`.
This implies that `user_name` likely stores the email address, allowing a single `User` entry to link to either a
`Buyer` or `Seller` profile based on their role. This design needs careful handling at the application level to ensure
consistency.

### `purchased_car`

Records details of cars that have been bought.

| Column           | Type      | Constraints   | Description                               |
|:-----------------|:----------|:--------------|:------------------------------------------|
| `id`             | `INTEGER` | `PRIMARY KEY` | Unique identifier for the purchase record |
| `buyer_id`       | `INTEGER` | `NOT NULL`    | `FOREIGN KEY` to `Buyer` table            |
| `seller_id`      | `INTEGER` | `NOT NULL`    | `FOREIGN KEY` to `Seller` table           |
| `car_id`         | `INTEGER` | `NOT NULL`    | `FOREIGN KEY` to `Car` table              |
| `purchased_date` | `DATE`    | `NOT NULL`    | Date of the car purchase                  |

---

## Foreign Key Relationships

The following foreign key constraints are defined to maintain data integrity and establish relationships between tables:

* **`Car` to `Seller`**:
    * `Car.seller_id` references `Seller.id` (Many Cars to One Seller)
* **`Car_image` to `Car`**:
    * `Car_image.car_id` references `Car.id` (Many Images to One Car)
* **`Review` to `Car`**:
    * `Review.car_id` references `Car.id` (Many Reviews to One Car)
* **`Review` to `Buyer`**:
    * `Review.buyer_id` references `Buyer.id` (Many Reviews to One Buyer)
* **`purchased_car` to `Buyer`**:
    * `purchased_car.buyer_id` references `Buyer.id`
* **`purchased_car` to `Seller`**:
    * `purchased_car.seller_id` references `Seller.id`
* **`purchased_car` to `Car`**:
    * `purchased_car.car_id` references `Car.id`
* **`User` to `Seller` and `Buyer` (Conditional)**:
    * `User.user_name` references `Seller.email`
    * `User.user_name` references `Buyer.email`
      (This implies `user_name` stores an email, and a user can be either a buyer or a seller, but not both
      simultaneously using these specific FKs as written, or it's designed for a shared email authentication system.)

This schema provides a solid foundation for a car sales application. You can extend it further with features like
payment details, car specifications, or search history.

---

# Functionalities

## Car Purchased service API

This document outlines the API endpoints for the Car Sales Platform. The API provides functionalities for managing
users (buyers and sellers), car listings, car images, purchase records, and reviews.

---

## Base URL

The base URL for all API endpoints is:

`http://your-domain.com/v1/api/`

---

## User Management

This section details the endpoints for managing general user accounts.

### User Functions (Core)

These functions manage the `User` entity directly, which stores username (email), password, and role.

* **Create User**
    * **Description:** Registers a new user account.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/user/create`
    * **Request Body:**
        ```json
        {
          "email": "user@example.com",
          "password": "securepassword123",
          "role": "BUYER" // or "SELLER"
        }
        ```
    * **Response:** Success message or user ID.

* **Update User**
    * **Description:** Updates the password or role for an existing user.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/user/update`
    * **Request Body:**
        ```json
        {
          "password": "newsecurepassword",
          "role": "SELLER", // Optional: Update password
          "username": "user@example.com" // Optional: Update role
        }
        ```
    * **Response:** Success message or updated user details.

* **Delete User**
    * **Description:** Deletes a user account by username.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/user/delete`
    * **Request Body:**
        ```json
        {
          "username": "user@example.com"
        }
        ```
    * **Response:** Success message.

### User Controller (`/admin/`) - (Optional for Admin Access)

These endpoints are typically protected and accessible only by administrators for comprehensive user management.

* **Get All Users**
    * **Description:** Returns a list of all user accounts (username and role).
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/admin/all`
    * **Response:** `[{ "username": "...", "role": "..." }]`

* **Get User by ID**
    * **Description:** Returns a user's details (username and role) by their ID.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/admin/{id}`
    * **Path Variable:** `{id}` - The ID of the user.
    * **Response:** `{ "username": "...", "role": "..." }`

* **Get User by Username**
    * **Description:** Returns a user's details (username and role) by their username (email).
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/admin/{username}`
    * **Path Variable:** `{username}` - The username (email) of the user.
    * **Response:** `{ "username": "...", "role": "..." }`

* **Get Users by Role**
    * **Description:** Returns a list of users filtered by their role.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/admin/role/{role}`
    * **Path Variable:** `{role}` - The role of the users (e.g., "BUYER", "SELLER").
    * **Response:** `[{ "username": "...", "role": "..." }]`

* **Update User Password by ID**
    * **Description:** Updates only the password for a user identified by their ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/admin/password/id/{id}`
    * **Path Variable:** `{id}` - The ID of the user.
    * **Request Body:**
        ```json
        {
          "password": "newsecurepassword"
        }
        ```
    * **Response:** Success message.

* **Update User Password by Username**
    * **Description:** Updates only the password for a user identified by their username.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/admin/password/username/{username}`
    * **Path Variable:** `{username}` - The username (email) of the user.
    * **Request Body:**
        ```json
        {
          "password": "newsecurepassword"
        }
        ```
    * **Response:** Success message.

* **Delete User by ID**
    * **Description:** Deletes a user record by ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/admin/id/{id}`
    * **Path Variable:** `{id}` - The ID of the user.
    * **Response:** Success message.

* **Delete User by Username**
    * **Description:** Deletes a user record by username.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/admin/username/{username}`
    * **Path Variable:** `{username}` - The username (email) of the user.
    * **Response:** Success message.

---

## Buyer Management

### Buyer Controller (`/buyer`)

* **Register Buyer**
    * **Description:** Creates a new buyer profile and associates it with a user account.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/buyer/register`
    * **Request Body:** (Includes all `Buyer` entity info and the user's password)
        ```json
        {
          "first_name": "John",
          "last_name": "Doe",
          "email": "john.doe@example.com",
          "phone": "123-456-7890",
          "password": "buyersecurepass" // This will also create/link a User entity
        }
        ```
    * **Response:** Details of the created buyer.

* **Get All Buyers**
    * **Description:** Returns a list of all buyer profiles.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/buyer/`
    * **Response:** `[{ "id": ..., "first_name": "...", ... }]`

* **Get Buyer by ID**
    * **Description:** Returns a buyer's details by their ID.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/buyer/{id}`
    * **Path Variable:** `{id}` - The ID of the buyer.
    * **Response:** `{ "id": ..., "first_name": "...", ... }`

* **Update Buyer by ID**
    * **Description:** Updates an existing buyer's profile by ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/buyer/{id}`
    * **Path Variable:** `{id}` - The ID of the buyer.
    * **Request Body:** (Partial or full `Buyer` entity info to update)
        ```json
        {
          "phone": "987-654-3210"
        }
        ```
    * **Response:** Updated buyer details.

* **Delete Buyer by ID**
    * **Description:** Deletes a buyer profile by ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/buyer/{id}`
    * **Path Variable:** `{id}` - The ID of the buyer.
    * **Response:** Success message.

---

## Seller Management

### Seller Controller (`/seller`)

* **Register Seller**
    * **Description:** Creates a new seller profile and associates it with a user account.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/seller/register`
    * **Request Body:** (Includes all `Seller` entity information and the user's password)
        ```json
        {
          "company_name": "Auto Traders Inc.",
          "email": "info@autotraders.com",
          "phone": "555-111-2222",
          "password": "sellersecurepass" // This will also create/link a User entity
        }
        ```
    * **Response:** Details of the created seller (without unnecessary fields).

* **Get All Sellers**
    * **Description:** Returns a list of all seller profiles.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/seller/`
    * **Response:** `[{ "id": ..., "company_name": "...", ... }]`

* **Get Seller by ID**
    * **Description:** Returns a seller's details by their ID.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/seller/{id}`
    * **Path Variable:** `{id}` - The ID of the seller.
    * **Response:** `{ "id": ..., "company_name": "...", ... }`

* **Update Seller by ID**
    * **Description:** Updates an existing seller's profile by ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/seller/{id}`
    * **Path Variable:** `{id}` - The ID of the seller.
    * **Request Body:** (Partial or full `Seller` entity info to update)
        ```json
        {
          "phone": "555-333-4444"
        }
        ```
    * **Response:** Updated seller details.

* **Delete Seller by ID**
    * **Description:** Deletes a seller profile by ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/seller/{id}`
    * **Path Variable:** `{id}` - The ID of the seller.
    * **Response:** Success message.

---

## Car Listings

### Car Controller (`/car`)

* **Create Car Listing**
    * **Description:** Adds a new car to the listings.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/car/`
    * **Request Body:** (All `Car` entity info, including `seller_id`)
        ```json
        {
          "model": "Toyota Camry",
          "year": "2023",
          "price": 25000.00,
          "isAvailable": true,
          "seller_id": 1
        }
        ```
    * **Response:** Details of the created car.

* **Get All Cars**
    * **Description:** Returns a list of all car listings.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/car/`
    * **Response:** `[{ "id": ..., "model": "...", ... }]`

* **Get Car by ID**
    * **Description:** Returns details of a specific car by its ID.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/car/{id}`
    * **Path Variable:** `{id}` - The ID of the car.
    * **Response:** `{ "id": ..., "model": "...", ... }`

* **Update Car by ID**
    * **Description:** Updates details of an existing car by ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/car/{id}`
    * **Path Variable:** `{id}` - The ID of the car.
    * **Request Body:** (Partial or full `Car` entity info to update)
        ```json
        {
          "price": 24500.00,
          "isAvailable": false
        }
        ```
    * **Response:** Updated car details.

* **Delete Car by ID**
    * **Description:** Deletes a car listing by ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/car/{id}`
    * **Path Variable:** `{id}` - The ID of the car.
    * **Response:** Success message.

* **Get Cars by Seller ID**
    * **Description:** Returns a list of cars listed by a specific seller.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/car/seller/{sellerId}`
    * **Path Variable:** `{sellerId}` - The ID of the seller.
    * **Response:** `[{ "id": ..., "model": "...", ... }]`

* **Search Cars by Model Name (Request Parameter)**
    * **Description:** Returns a list of cars whose model name matches the provided parameter.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/car/seller`
    * **Request Parameter:** `name` - The model name to search for (e.g., `?name=bmw`).
    * **Example:** `/v1/api/car/seller?name=bmw`
    * **Response:** `[{ "id": ..., "model": "BMW X5", ... }]`

### CarImage Controller (`/carImage`)

* **Add Car Image**
    * **Description:** Registers a new image for a specific car.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/carImage/register`
    * **Request Body:** (All `Car_image` entity info)
        ```json
        {
          "car_id": 1,
          "public_id": "car_image_xyz123",
          "image_url": "[http://example.com/images/car1_front.jpg](http://example.com/images/car1_front.jpg)"
        }
        ```
    * **Response:** Details of the created car image.

* **Get All Car Images**
    * **Description:** Returns a list of all car image records.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/carImage/`
    * **Response:** `[{ "id": ..., "car_id": ..., "image_url": "..." }]`

* **Get Car Image by ID**
    * **Description:** Returns details of a specific car image by its ID.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/carImage/{id}`
    * **Path Variable:** `{id}` - The ID of the car image.
    * **Response:** `{ "id": ..., "car_id": ..., "image_url": "..." }`

* **Update Car Image by ID**
    * **Description:** Updates details of an existing car image record by ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/carImage/{id}`
    * **Path Variable:** `{id}` - The ID of the car image.
    * **Request Body:** (Partial or full `Car_image` entity info to update)
        ```json
        {
          "image_url": "[http://example.com/images/car1_new_front.jpg](http://example.com/images/car1_new_front.jpg)"
        }
        ```
    * **Response:** Updated car image details.

* **Delete Car Image by ID**
    * **Description:** Deletes a car image record by ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/carImage/{id}`
    * **Path Variable:** `{id}` - The ID of the car image.
    * **Response:** Success message.

---

## Purchase Management

### Purchased Car Controller (`/purchased/`)

* **Record Car Purchase**
    * **Description:** Records a car as purchased.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/purchased/`
    * **Request Body:**
        ```json
        {
          "car_id": 101,
          "buyer_id": 1,
          "seller_id": 5
        }
        ```
    * **Response:** `purchased_id` of the newly created record.

* **Get All Purchased Cars**
    * **Description:** Retrieves a list of all recorded purchased cars, including buyer, seller, and car details.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/purchased/all`
    * **Response:**
      `[{ "purchased_id": ..., "buyer": { ... }, "seller": { ... }, "car": { ... }, "purchased_date": "YYYY-MM-DD" }]`

* **Get Purchased Car by Car ID**
    * **Description:** Retrieves the purchase record for a specific car ID, including buyer and seller details.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/purchased/car/{id}`
    * **Path Variable:** `{id}` - The ID of the car.
    * **Response:**
      `{ "purchased_id": ..., "buyer": { ... }, "seller": { ... }, "car": { ... }, "purchased_date": "YYYY-MM-DD" }`

* **Get Purchased Cars by Buyer ID**
    * **Description:** Retrieves a list of cars purchased by a specific buyer.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/purchased/buyer/{id}`
    * **Path Variable:** `{id}` - The ID of the buyer.
    * **Response:**
      `[{ "purchased_id": ..., "buyer": { ... }, "seller": { ... }, "car": { ... }, "purchased_date": "YYYY-MM-DD" }]`

* **Get Purchased Cars by Seller ID**
    * **Description:** Retrieves a list of cars sold by a specific seller.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/purchased/seller/{id}`
    * **Path Variable:** `{id}` - The ID of the seller.
    * **Response:**
      `[{ "purchased_id": ..., "buyer": { ... }, "seller": { ... }, "car": { ... }, "purchased_date": "YYYY-MM-DD" }]`

* **Update Purchased Car Record**
    * **Description:** Updates an existing purchased car record based on its ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/purchased/{id}`
    * **Path Variable:** `{id}` - The ID of the purchased record to update.
    * **Request Body:** (Partial or full update fields for `car_id`, `buyer_id`, `seller_id`)
        ```json
        {
          "car_id": 102,
          "buyer_id": 2
        }
        ```
    * **Response:** Updated purchase record details.

* **Delete Purchased Car Record**
    * **Description:** Deletes a purchased car record by its ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/purchased/{id}`
    * **Path Variable:** `{id}` - The ID of the purchased record to delete.
    * **Response:** Success message.

---

## Review Management

### Review Controller (`/review`)

* **Submit Review**
    * **Description:** Allows a buyer to submit a review for a car.
    * **Method:** `POST`
    * **Endpoint:** `/v1/api/review/`
    * **Request Body:** (All `Review` entity information)
        ```json
        {
          "car_id": 101,
          "rate": 5,
          "feedback": "Excellent car, very satisfied!",
          "buyer_id": 1
        }
        ```
    * **Response:** Details of the created review.

* **Get Review by ID**
    * **Description:** Retrieves a specific review by its ID.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/review/{id}`
    * **Path Variable:** `{id}` - The ID of the review.
    * **Response:** `{ "rate": ..., "feedback": "...", "car": { ... }, "buyer": { ... } }`

* **Get Reviews by Car ID**
    * **Description:** Retrieves a list of all reviews for a specific car.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/review/car/{carId}`
    * **Path Variable:** `{carId}` - The ID of the car.
    * **Response:** `[{ "rate": ..., "feedback": "...", "car": { ... }, "buyer": { ... } }]`

* **Get Reviews by Buyer ID**
    * **Description:** Retrieves a list of all reviews submitted by a specific buyer.
    * **Method:** `GET`
    * **Endpoint:** `/v1/api/review/buyer/{buyerId}`
    * **Path Variable:** `{buyerId}` - The ID of the buyer.
    * **Response:** `[{ "rate": ..., "feedback": "...", "car": { ... }, "buyer": { ... } }]`

* **Update Review by ID**
    * **Description:** Updates an existing review record by its ID.
    * **Method:** `PUT`
    * **Endpoint:** `/v1/api/review/{id}`
    * **Path Variable:** `{id}` - The ID of the review.
    * **Request Body:** (Partial or full update fields for `rate`, `feedback`)
        ```json
        {
          "rate": 4,
          "feedback": "Still good, but gas mileage could be better."
        }
        ```
    * **Response:** Updated review details.

* **Delete Review by ID**
    * **Description:** Deletes a review record by its ID.
    * **Method:** `DELETE`
    * **Endpoint:** `/v1/api/review/{id}`
    * **Path Variable:** `{id}` - The ID of the review.
    * **Response:** Success message.

---

### Basic CRUD Operations

The service exposes REST endpoints for standard CRUD operations (`GET`, `POST`, `PUT`, `DELETE`) for the following
entities:

* **Buyers:** Create, retrieve (all or by ID), update, delete.
* **Sellers:** Create, retrieve (all or by ID), update, delete.
* **Cars:** Create (listed by a seller), retrieve (all, by ID, by seller), update, delete.
* **PurchasedCars:** Retrieve (all, by ID, by buyer, by seller).

### Specific Business Logic

* **Seller Car Listing:**
    * When creating a `Seller`, you can optionally provide a list of `Car` objects to be associated with that seller.
    * Sellers can add new cars to their inventory.
* **Car Purchase Flow:**
    * A dedicated endpoint will allow a `Buyer` to purchase an available `Car`.
    * Upon a successful purchase:
        * The `Car`'s `isSold` status will be updated to `true`.
        * The purchased `Car` will be "removed" from the `Seller`'s active listings (conceptually, by setting `isSold`
          to true and adding to `PurchasedCar`).
        * A new entry in the `PurchasedCar` entity will be created, linking the `Buyer`, `Car`, and `Seller`.
* **Buyer's Purchased Cars:**
    * An endpoint will be available to retrieve all `PurchasedCar` records for a specific `Buyer`.

---

## Implementation Details

### Project Structure

The project will follow a standard Spring Boot application structure with packages organized for clarity:

* **`controller`**: Contains REST controllers handling incoming HTTP requests.
* **`service`**: Implements business logic and orchestrates operations between controllers and repositories.
* **`repository`**: Provides data access layer using Spring Data JPA.
* **`model`**: Defines JPA entities (DAO - Data Access Objects).
* **`dto`**: Defines Data Transfer Objects for request and response payloads, ensuring separation of concerns.
* **`exception`**: Custom exception classes and global exception handler.
* **`config`**: Configuration classes (e.g., Swagger configuration).

### Data Transfer Objects (DTOs) and Conversions

* DTOs will be used for all API request and response bodies to decouple the API contract from the internal entity
  models.
* Conversion between DTOs and DAO entities will be handled in the service layer. Libraries like MapStruct or ModelMapper
  can be introduced to streamline this process.

### Input Validation

* Input validation will be performed on incoming DTOs using Spring's `@Valid` annotation and JSR 303 (Bean Validation)
  annotations (e.g., `@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Email`).
* Custom validation logic will be implemented in the service layer where necessary (e.g., checking car availability
  before purchase).

### Exception Handling

* Custom exception classes (e.g., `ResourceNotFoundException`, `InvalidPurchaseException`) will be defined for specific
  error scenarios.
* A global exception handler (`@ControllerAdvice`) will be implemented to gracefully handle exceptions and return
  appropriate `ResponseEntity` objects with meaningful error messages and HTTP status codes (e.g., `400 Bad Request`,
  `404 Not Found`, `409 Conflict`).

### Logging

* Logging will be implemented using SLF4J and Logback to provide insights into application behavior.
* Appropriate log levels (INFO, DEBUG, ERROR) will be used across controllers, services, and repositories for effective
  debugging and monitoring.

### Testing

* **Unit Tests:** Will be written for service layer methods and utility classes using JUnit and Mockito.
* **Integration Tests:** Will cover controller endpoints and repository interactions, potentially using Spring Boot's
  test capabilities (`@SpringBootTest`, `@AutoConfigureMockMvc`).

### Code Quality

* **SonarQube:** Integrated into the build process to analyze code quality, identify bugs, code smells, and security
  vulnerabilities.
* **JaCoCo:** Used for code coverage reporting, ensuring that a sufficient portion of the codebase is covered by tests.

---

## How to Run the Service

### Prerequisites

* Java 17 or higher
* Maven
* **PostgreSQL Database:** Ensure you have a running PostgreSQL instance accessible from your machine.

### Database Configuration

(`src/main/resources/application.properties`)

You will need to configure your PostgreSQL database connection in `application.properties`. Replace the placeholders
with your actual database credentials and connection details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Car_App
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update # Use 'create' or 'create-drop' for initial setup, 'update' for subsequent runs
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Running from Source Code

1. **Clone the repository:**
   ```bash
   git clone <repository_url>
   cd car-purchase-service
   ```
2. **Build the project:**
   ```bash
   mvn clean install
   ```
3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   The service will start on `http://localhost:8080` (default port).

### Running from Executable JAR

1. **Build the project (if you haven't already):**
   ```bash
   mvn clean install
   ```
2. **Navigate to the `target` directory:**
   ```bash
   cd target
   ```
3. **Run the JAR file:**
   ```bash
   java -jar car-purchase-service-0.0.1-SNAPSHOT.jar # Adjust version number if needed
   ```

---

## API Documentation (Swagger UI)

Once the service is running, you can access the Swagger UI documentation at:

* **Local URL:** `http://localhost:8080/swagger-ui.html`

This interface will allow you to explore all available endpoints, view their expected request/response formats, and even
send test requests directly from your browser.

---
