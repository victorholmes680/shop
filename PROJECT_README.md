# ShopMall - Full-Stack E-Commerce Application

A complete e-commerce shopping mall application built with Spring Boot (backend) and React (frontend).

## Project Structure

```
shopmall/
├── backend/                 # Spring Boot backend API
│   ├── src/
│   │   ├── java/com/shopmall/
│   │   └── resources/
│   ├── Dockerfile
│   ├── README.md
│   └── pom.xml
├── frontend/                # React frontend (root directory)
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── docs/                    # Documentation
│   └── DATABASE.md
└── docker-compose.yml
```

## Features

### Customer Features
- **User Authentication**: Registration, login, profile management with JWT
- **Product Catalog**: Browse products by category, search, filter by price
- **Shopping Cart**: Add/remove items, quantity management
- **Checkout**: Secure order placement with payment integration
- **Order Tracking**: View order history and status
- **Reviews & Ratings**: Rate and review purchased products

### Admin Features
- **Dashboard**: Sales statistics, recent orders, low stock alerts
- **Product Management**: Full CRUD operations for products
- **Category Management**: Organize products into categories
- **Order Management**: View and update order status
- **User Management**: Manage users and roles

## Tech Stack

### Backend
- **Java 17** with Spring Boot 3.2.5
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **PostgreSQL** database (production) / H2 (development)
- **Flyway** for database migrations
- **SpringDoc OpenAPI** for API documentation

### Frontend
- **React 18** with TypeScript
- **Vite** for fast development
- **React Router** for navigation
- **Axios** for API calls
- **Tailwind CSS** for styling

## Quick Start with Docker

The easiest way to run the entire application is using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Backend API on http://localhost:8080/api
- Frontend on http://localhost:3000

### Default Credentials

- **Admin**: admin@shopmall.com / admin123
- **User**: user@example.com / user123

## Development Setup

### Backend Setup

```bash
cd backend
mvn spring-boot:run
```

Backend will be available at http://localhost:8080/api
Swagger UI: http://localhost:8080/api/swagger-ui.html
H2 Console: http://localhost:8080/api/h2-console

See [backend/README.md](backend/README.md) for more details.

### Frontend Setup

```bash
npm install
npm run dev
```

Frontend will be available at http://localhost:5173

## API Documentation

Once the backend is running, visit:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

## Database Schema

See [docs/DATABASE.md](docs/DATABASE.md) for complete database schema documentation.

## Environment Variables

### Backend
| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | - |
| `DATABASE_USERNAME` | Database username | - |
| `DATABASE_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret | - |
| `JWT_EXPIRATION` | Token expiration (ms) | 86400000 |
| `CORS_ORIGINS` | Allowed CORS origins | http://localhost:3000 |

### Frontend
| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_URL` | Backend API URL | http://localhost:8080/api |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user

### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product details
- `GET /api/products/search` - Search products

### Shopping Cart
- `GET /api/cart` - Get user's cart
- `POST /api/cart/items` - Add item to cart
- `DELETE /api/cart/items/{id}` - Remove item

### Orders
- `POST /api/orders` - Create order
- `GET /api/orders` - Get user's orders

### Admin
- `GET /api/admin/dashboard` - Get statistics
- `GET /api/admin/users` - Manage users

## License

Apache License 2.0
