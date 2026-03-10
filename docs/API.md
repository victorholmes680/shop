# Shopping Mall System - API Documentation

## Base URL
- Development: `http://localhost:8080`
- Production: `https://api.shopmall.com`

## Authentication
Most endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

---

## Authentication APIs

### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}

Response 201:
{
  "id": "1",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "CUSTOMER",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}

Response 200:
{
  "id": "1",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "CUSTOMER",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Get Current User
```http
GET /api/auth/me
Authorization: Bearer <token>

Response 200:
{
  "id": "1",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "CUSTOMER"
}
```

---

## Category APIs

### Get All Categories
```http
GET /api/categories

Response 200:
[
  {
    "id": "1",
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }
]
```

### Get Category by ID
```http
GET /api/categories/{id}

Response 200:
{
  "id": "1",
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "productCount": 25
}
```

### Create Category (Admin)
```http
POST /api/categories
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Books",
  "description": "Books and publications"
}
```

---

## Product APIs

### Get All Products
```http
GET /api/products?page=0&size=12&sort=price,asc&category=1&search=laptop

Query Parameters:
- page: Page number (default: 0)
- size: Items per page (default: 12)
- sort: Sort field and direction (e.g., price,asc)
- category: Filter by category ID
- search: Search in name and description
- minPrice: Minimum price filter
- maxPrice: Maximum price filter

Response 200:
{
  "content": [
    {
      "id": "1",
      "name": "Laptop Pro 15",
      "description": "High-performance laptop",
      "price": 1299.99,
      "stockQuantity": 50,
      "imageUrl": "/images/laptop.jpg",
      "category": {
        "id": "1",
        "name": "Electronics"
      },
      "averageRating": 4.5,
      "reviewCount": 120
    }
  ],
  "pageable": {...},
  "totalPages": 5,
  "totalElements": 60,
  "number": 0,
  "size": 12
}
```

### Get Product by ID
```http
GET /api/products/{id}

Response 200:
{
  "id": "1",
  "name": "Laptop Pro 15",
  "description": "High-performance laptop with 16GB RAM",
  "price": 1299.99,
  "stockQuantity": 50,
  "imageUrl": "/images/laptop.jpg",
  "category": {
    "id": "1",
    "name": "Electronics"
  },
  "averageRating": 4.5,
  "reviewCount": 120,
  "createdAt": "2024-01-15T10:00:00"
}
```

### Create Product (Admin)
```http
POST /api/products
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "New Product",
  "description": "Product description",
  "price": 99.99,
  "stockQuantity": 100,
  "categoryId": "1",
  "imageUrl": "/images/product.jpg"
}
```

### Update Product (Admin)
```http
PUT /api/products/{id}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Updated Product",
  "price": 89.99,
  "stockQuantity": 80
}
```

### Delete Product (Admin)
```http
DELETE /api/products/{id}
Authorization: Bearer <admin-token>

Response 204
```

---

## Cart APIs

### Get User's Cart
```http
GET /api/cart
Authorization: Bearer <token>

Response 200:
{
  "id": "1",
  "items": [
    {
      "id": "1",
      "product": {
        "id": "1",
        "name": "Laptop Pro 15",
        "price": 1299.99,
        "imageUrl": "/images/laptop.jpg"
      },
      "quantity": 2,
      "unitPrice": 1299.99,
      "subtotal": 2599.98
    }
  ],
  "totalItems": 2,
  "totalAmount": 2599.98
}
```

### Add Item to Cart
```http
POST /api/cart/items
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": "1",
  "quantity": 2
}

Response 200: Updated cart object
```

### Update Cart Item Quantity
```http
PUT /api/cart/items/{itemId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "quantity": 3
}

Response 200: Updated cart object
```

### Remove Item from Cart
```http
DELETE /api/cart/items/{itemId}
Authorization: Bearer <token>

Response 200: Updated cart object
```

### Clear Cart
```http
DELETE /api/cart
Authorization: Bearer <token>

Response 204
```

---

## Order APIs

### Create Order
```http
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "shippingAddress": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "paymentMethod": "CREDIT_CARD"
}

Response 201:
{
  "id": "ORD-001",
  "orderDate": "2024-01-15T10:30:00",
  "status": "PENDING",
  "totalAmount": 2599.98,
  "items": [
    {
      "id": "1",
      "productName": "Laptop Pro 15",
      "quantity": 2,
      "unitPrice": 1299.99,
      "subtotal": 2599.98
    }
  ],
  "shippingAddress": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

### Get User's Orders
```http
GET /api/orders?page=0&size=10&status=PENDING
Authorization: Bearer <token>

Response 200:
{
  "content": [...],
  "totalElements": 5
}
```

### Get Order by ID
```http
GET /api/orders/{id}
Authorization: Bearer <token>

Response 200: Order object with full details
```

### Cancel Order
```http
POST /api/orders/{id}/cancel
Authorization: Bearer <token>

Response 200: Updated order with status CANCELLED
```

---

## Payment APIs

### Process Payment
```http
POST /api/payments/process
Authorization: Bearer <token>
Content-Type: application/json

{
  "orderId": "ORD-001",
  "paymentDetails": {
    "cardNumber": "4111111111111111",
    "cardHolder": "John Doe",
    "expiryDate": "12/25",
    "cvv": "123"
  }
}

Response 200:
{
  "paymentId": "PAY-001",
  "status": "SUCCESS",
  "transactionId": "txn_1234567890"
}
```

---

## Review APIs

### Get Product Reviews
```http
GET /api/reviews/product/{productId}?page=0&size=10

Response 200:
{
  "content": [
    {
      "id": "1",
      "user": {
        "id": "1",
        "name": "John Doe"
      },
      "rating": 5,
      "comment": "Great product!",
      "createdAt": "2024-01-10T15:30:00"
    }
  ],
  "totalElements": 120
}
```

### Add Review
```http
POST /api/reviews
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": "1",
  "rating": 5,
  "comment": "Great product!"
}

Response 201: Created review object
```

### Delete Review
```http
DELETE /api/reviews/{reviewId}
Authorization: Bearer <token>

Response 204
```

---

## Admin APIs

### Dashboard Statistics
```http
GET /api/admin/dashboard
Authorization: Bearer <admin-token>

Response 200:
{
  "totalRevenue": 125000.50,
  "totalOrders": 450,
  "totalUsers": 120,
  "totalProducts": 85,
  "recentOrders": [...],
  "topProducts": [...]
}
```

### Update Order Status (Admin)
```http
PUT /api/admin/orders/{orderId}/status
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "status": "SHIPPED"
}

Response 200: Updated order
```

### Get All Users (Admin)
```http
GET /api/admin/users?page=0&size=20
Authorization: Bearer <admin-token>

Response 200: Paginated user list
```

### Get All Orders (Admin)
```http
GET /api/admin/orders?page=0&size=20&status=PENDING
Authorization: Bearer <admin-token>

Response 200: Paginated order list with all users
```

---

## Error Responses

### Error Format
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/products",
  "errors": [
    {
      "field": "price",
      "message": "Price must be positive"
    }
  ]
}
```

### HTTP Status Codes
- `200` - Success
- `201` - Created
- `204` - No Content
- `400` - Bad Request / Validation Error
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict (e.g., insufficient stock)
- `500` - Internal Server Error
