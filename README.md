# Shopping Mall System

A full-stack e-commerce shopping mall application with:
- **Backend**: Spring Boot 3.2 (Java 17)
- **Frontend**: React 19 + TypeScript + Vite
- **Database**: PostgreSQL with Flyway migrations
- **Authentication**: JWT-based security

## Features

| Feature | Description |
|---------|-------------|
| 🔐 User Authentication | Registration, login, JWT-based auth |
| 🛍️ Product Catalog | Browse, search, filter products by category |
| 🛒 Shopping Cart | Add, update, remove items with real-time total |
| 💳 Checkout Flow | Address, payment, order confirmation |
| 📦 Order Management | View order history, track order status |
| ⭐ Reviews & Ratings | Customer reviews on products |
| 👨‍💼 Admin Dashboard | Manage products, orders, and users |

## Tech Stack

### Backend
- Spring Boot 3.2.5
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- PostgreSQL / H2
- Flyway migrations
- SpringDoc OpenAPI (Swagger)

### Frontend
- React 19.2
- TypeScript 5.9
- Vite 7.3
- React Router 7
- Zustand (state management)
- Axios (HTTP client)
- TailwindCSS 4

## Quick Start

### Option 1: Docker (Recommended)
```bash
# Start all services (backend, frontend, database)
docker-compose -f deployment/docker-compose.yml up -d

# Access at http://localhost
```

### Option 2: Local Development

**Backend:**
```bash
cd backend
mvn spring-boot:run
# Available at http://localhost:8080
# Swagger UI at http://localhost:8080/swagger-ui/index.html
```

**Frontend:**
```bash
npm install
npm run dev
# Available at http://localhost:5173
```

## Default Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@shopmall.com | admin123 |

## Project Structure

```
shop/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/shopmall/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Data access layer
│   │   ├── entity/            # JPA entities
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── config/            # Configuration
│   │   ├── security/          # JWT & Security
│   │   └── exception/         # Error handling
│   └── pom.xml
│
├── src/                        # React frontend
│   ├── components/            # Reusable components
│   ├── pages/                 # Page components
│   ├── stores/                # Zustand stores
│   ├── services/              # API services
│   ├── layouts/               # Layout components
│   ├── hooks/                 # Custom hooks
│   ├── types/                 # TypeScript types
│   └── utils/                 # Utilities
│
├── deployment/                 # Docker configs
│   ├── docker-compose.yml
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── nginx.conf
│
└── docs/                       # Documentation
    ├── ARCHITECTURE.md         # System architecture
    ├── API.md                  # API documentation
    ├── DATABASE.md             # Database schema
    └── DEPLOYMENT.md           # Deployment guide
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login (returns JWT)
- `GET /api/auth/me` - Get current user

### Products
- `GET /api/products` - List products (with pagination/filtering)
- `GET /api/products/{id}` - Get product details
- `POST /api/products` - Create product (admin)
- `PUT /api/products/{id}` - Update product (admin)
- `DELETE /api/products/{id}` - Delete product (admin)

### Cart
- `GET /api/cart` - Get user's cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update quantity
- `DELETE /api/cart/items/{id}` - Remove item

### Orders
- `POST /api/orders` - Create order
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order details

### Admin
- `GET /api/admin/dashboard` - Dashboard statistics
- `PUT /api/admin/orders/{id}/status` - Update order status

For complete API documentation, see [docs/API.md](docs/API.md) or visit `/swagger-ui` when running.

## Documentation

- [Architecture](docs/ARCHITECTURE.md) - System architecture and design
- [API Documentation](docs/API.md) - Complete REST API reference
- [Database Schema](docs/DATABASE.md) - Database structure
- [Deployment Guide](docs/DEPLOYMENT.md) - Deployment instructions

## Environment Variables

**Backend (.env or application.yml):**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/shopmall
SPRING_DATASOURCE_USERNAME=shopuser
SPRING_DATASOURCE_PASSWORD=shoppass123
JWT_SECRET=your-secret-key-min-256-chars
JWT_EXPIRATION=86400000
```

**Frontend (.env):**
```env
VITE_API_URL=http://localhost:8080
```

## Development

### Backend
```bash
cd backend
mvn clean install        # Build
mvn spring-boot:run      # Run
mvn test                 # Test
```

### Frontend
```bash
npm run dev              # Development server
npm run build            # Production build
npm run preview          # Preview production build
npm run lint             # Lint code
```

## Deployment

See [Deployment Guide](docs/DEPLOYMENT.md) for:
- Docker deployment
- Cloud deployment (AWS, GCP, Azure)
- Security checklist
- Environment configuration

## License

MIT License - feel free to use for learning and development.

## Team

This project was developed by a coordinated team using:
- **Backend Developer**: Spring Boot API implementation
- **Frontend Developer**: React UI implementation
- **Team Lead**: Architecture, coordination, and integration
