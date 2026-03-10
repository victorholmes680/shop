# ShopMall Backend API

Spring Boot REST API backend for the ShopMall e-commerce application.

## Features

- **User Authentication**: JWT-based authentication with role-based access control
- **Product Catalog**: Full CRUD operations for products and categories
- **Shopping Cart**: Add, update, and remove items from cart
- **Order Management**: Complete order processing with payment integration
- **Reviews & Ratings**: Product reviews with verified purchase badges
- **Admin Dashboard**: Statistics, user management, and order management

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **Flyway** - Database migrations
- **H2 Database** - In-memory database for development
- **PostgreSQL** - Production database
- **Lombok** - Reduce boilerplate code
- **SpringDoc OpenAPI** - API documentation (Swagger UI)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 13+ (for production)

### Running the Application

#### Development Mode (H2 Database)

```bash
cd backend
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

#### Production Mode (PostgreSQL)

1. Set up PostgreSQL database:

```sql
CREATE DATABASE shopmall;
CREATE USER shopmall WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE shopmall TO shopmall;
```

2. Configure environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/shopmall
export DATABASE_USERNAME=shopmall
export DATABASE_PASSWORD=your_password
export JWT_SECRET=your-secret-key-at-least-256-bits
```

3. Run with production profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Accessing the Application

- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api/api-docs`
- **H2 Console** (dev only): `http://localhost:8080/api/h2-console`

### Default Credentials

- **Admin**: `admin@shopmall.com` / `admin123`
- **User**: `user@example.com` / `user123`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user
- `PATCH /api/auth/me` - Update profile
- `POST /api/auth/change-password` - Change password

### Products

- `GET /api/products` - List all products (with pagination)
- `GET /api/products/search` - Search products
- `GET /api/products/featured` - Get featured products
- `GET /api/products/{id}` - Get product details
- `POST /api/products` - Create product (Admin)
- `PUT /api/products/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

### Categories

- `GET /api/categories` - List all categories
- `GET /api/categories/{id}` - Get category details
- `POST /api/categories` - Create category (Admin)
- `PUT /api/categories/{id}` - Update category (Admin)
- `DELETE /api/categories/{id}` - Delete category (Admin)

### Shopping Cart

- `GET /api/cart` - Get user's cart
- `GET /api/cart/count` - Get cart item count
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update item quantity
- `DELETE /api/cart/items/{id}` - Remove item from cart
- `DELETE /api/cart` - Clear cart

### Orders

- `POST /api/orders` - Create order
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order details
- `PATCH /api/orders/{id}/cancel` - Cancel order

### Reviews

- `GET /api/reviews/product/{productId}` - Get product reviews
- `GET /api/reviews/product/{productId}/rating` - Get product rating summary
- `POST /api/reviews` - Create review
- `PUT /api/reviews/{id}` - Update review
- `DELETE /api/reviews/{id}` - Delete review

### Admin

- `GET /api/admin/dashboard` - Get dashboard statistics
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user

## Project Structure

```
backend/
├── src/main/
│   ├── java/com/shopmall/
│   │   ├── ShopMallApplication.java
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security configuration
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yml  # Application configuration
│       └── db/migration/    # Database migrations
└── pom.xml                  # Maven dependencies
```

## Configuration

Key configuration options in `application.yml`:

- Server port: `8080`
- Context path: `/api`
- JWT secret: Configure via `JWT_SECRET` environment variable
- JWT expiration: 24 hours (default)
- Database: H2 (dev) / PostgreSQL (prod)

## Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

- `V1__Create_schema.sql` - Initial schema creation
- `V2__Insert_seed_data.sql` - Seed data insertion

## Security

- Passwords are encrypted using BCrypt
- JWT tokens for authentication
- Role-based access control (USER, ADMIN, MANAGER)
- Public endpoints: `/api/auth/**`, `/api/products/**`, `/api/categories/**`
- Protected endpoints: All others require authentication
- Admin endpoints: Require ADMIN role

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -DskipTests
```

The JAR file will be created at `target/shopmall-backend-1.0.0.jar`

### Running the JAR

```bash
java -jar target/shopmall-backend-1.0.0.jar
```

## Docker

Build and run with Docker:

```bash
docker build -t shopmall-backend .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/shopmall \
  -e DATABASE_USERNAME=shopmall \
  -e DATABASE_PASSWORD=your_password \
  shopmall-backend
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | JDBC URL for PostgreSQL | - |
| `DATABASE_USERNAME` | Database username | - |
| `DATABASE_PASSWORD` | Database password | - |
| `JWT_SECRET` | Secret key for JWT signing | - |
| `JWT_EXPIRATION` | JWT token expiration time (ms) | `86400000` |
| `CORS_ORIGINS` | Allowed CORS origins | `http://localhost:3000` |

## License

Apache License 2.0
