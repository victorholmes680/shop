# ShopMall Database Schema Documentation

## Overview

This document describes the database schema for the ShopMall shopping application.

## Entity Relationship Diagram

```
users (1) ----< (N) user_roles
users (1) ----< (1) carts
users (1) ----< (N) orders
users (1) ----< (N) reviews

categories (1) ----< (N) categories (self-referencing for subcategories)
categories (1) ----< (N) products

carts (1) ----< (N) cart_items
products (1) ----< (N) cart_items

orders (1) ----< (N) order_items
products (1) ----< (N) order_items

products (1) ----< (N) reviews
```

## Tables

### users

Stores user account information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| email | VARCHAR(100) | NOT NULL, UNIQUE | User email |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| first_name | VARCHAR(50) | NOT NULL | First name |
| last_name | VARCHAR(50) | NOT NULL | Last name |
| phone | VARCHAR(20) | | Phone number |
| address | VARCHAR(255) | | Address |
| enabled | BOOLEAN | NOT NULL, DEFAULT TRUE | Account status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### user_roles

Stores user roles for authorization.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT | PK, FK | Reference to users |
| role | VARCHAR(20) | PK | Role name (USER, ADMIN, MANAGER) |

### categories

Stores product categories with support for hierarchical structure.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Category name |
| description | VARCHAR(255) | | Description |
| image_url | VARCHAR(500) | | Category image URL |
| parent_id | BIGINT | FK | Parent category (for subcategories) |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### products

Stores product information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(255) | NOT NULL | Product name |
| description | VARCHAR(2000) | | Product description |
| price | DECIMAL(10,2) | NOT NULL, CHECK >= 0 | Product price |
| stock_quantity | INT | NOT NULL, DEFAULT 0, CHECK >= 0 | Available stock |
| image_url | VARCHAR(500) | | Product image URL |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| featured | BOOLEAN | NOT NULL, DEFAULT FALSE | Featured flag |
| category_id | BIGINT | NOT NULL, FK | Reference to categories |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### carts

Stores user shopping carts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, UNIQUE, FK | Reference to users |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### cart_items

Stores items in shopping carts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| cart_id | BIGINT | NOT NULL, FK | Reference to carts |
| product_id | BIGINT | NOT NULL, FK | Reference to products |
| quantity | INT | NOT NULL, CHECK > 0 | Item quantity |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### orders

Stores customer orders.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| order_number | VARCHAR(50) | NOT NULL, UNIQUE | Order number |
| user_id | BIGINT | NOT NULL, FK | Reference to users |
| status | VARCHAR(20) | NOT NULL | Order status |
| total_amount | DECIMAL(10,2) | NOT NULL | Total amount |
| discount_amount | DECIMAL(10,2) | | Discount amount |
| tax_amount | DECIMAL(10,2) | | Tax amount |
| shipping_amount | DECIMAL(10,2) | | Shipping amount |
| shipping_address | VARCHAR(255) | | Shipping address |
| shipping_city | VARCHAR(100) | | Shipping city |
| shipping_postal_code | VARCHAR(20) | | Shipping postal code |
| shipping_phone | VARCHAR(20) | | Shipping phone |
| payment_method | VARCHAR(50) | | Payment method |
| payment_status | VARCHAR(20) | | Payment status |
| notes | VARCHAR(500) | | Order notes |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

### order_items

Stores items in orders.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| order_id | BIGINT | NOT NULL, FK | Reference to orders |
| product_id | BIGINT | NOT NULL, FK | Reference to products |
| product_name | VARCHAR(255) | NOT NULL | Product name (snapshot) |
| unit_price | DECIMAL(10,2) | NOT NULL | Unit price (snapshot) |
| quantity | INT | NOT NULL | Item quantity |
| subtotal | DECIMAL(10,2) | NOT NULL | Subtotal |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |

### reviews

Stores product reviews.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | Reference to users |
| product_id | BIGINT | NOT NULL, FK | Reference to products |
| rating | INT | NOT NULL, CHECK 1-5 | Rating (1-5 stars) |
| comment | VARCHAR(1000) | | Review comment |
| verified_purchase | BOOLEAN | NOT NULL, DEFAULT FALSE | Verified purchase flag |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |

## Enums

### Order Status (OrderStatus)
- PENDING - Order placed, awaiting confirmation
- CONFIRMED - Order confirmed by seller
- PROCESSING - Order being processed
- SHIPPED - Order shipped
- DELIVERED - Order delivered
- CANCELLED - Order cancelled
- REFUNDED - Order refunded

### Payment Status (PaymentStatus)
- PENDING - Payment pending
- COMPLETED - Payment completed
- FAILED - Payment failed
- REFUNDED - Payment refunded

### Role
- USER - Regular user
- ADMIN - Administrator
- MANAGER - Store manager

## Indexes

The following indexes are created for better query performance:

- `idx_users_email` on users(email)
- `idx_categories_parent` on categories(parent_id)
- `idx_products_category` on products(category_id)
- `idx_products_active` on products(active)
- `idx_orders_user` on orders(user_id)
- `idx_orders_status` on orders(status)
- `idx_orders_order_number` on orders(order_number)
- `idx_reviews_product` on reviews(product_id)
- `idx_cart_items_cart` on cart_items(cart_id)

## Seed Data

The following seed data is included:

1. **Admin User**: admin@shopmall.com / admin123
2. **Regular User**: user@example.com / user123
3. **Categories**: Electronics, Clothing, Books, Home & Garden, Sports, Toys
4. **Subcategories**: Smartphones, Laptops, Tablets, Audio, Men's, Women's, Kids
5. **Sample Products**: 9 products across various categories
6. **Sample Reviews**: 2 reviews for testing
