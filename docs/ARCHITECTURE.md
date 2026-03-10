# Shopping Mall System - Architecture

## System Overview

A full-stack e-commerce shopping mall system with:
- **Backend**: Spring Boot 3.x REST API
- **Frontend**: React 18+ with Vite
- **Database**: PostgreSQL (or H2 for development)
- **Authentication**: JWT with Spring Security

## Technology Stack

### Backend
| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Spring Boot | 3.2+ |
| Language | Java | 17+ |
| Build Tool | Maven | - |
| Database Access | Spring Data JPA | - |
| ORM | Hibernate | - |
| Security | Spring Security + JWT | - |
| API Documentation | SpringDoc OpenAPI | - |
| Validation | Jakarta Validation | - |
| Utilities | Lombok | - |

### Frontend
| Component | Technology | Version |
|-----------|------------|---------|
| Framework | React | 18+ |
| Build Tool | Vite | 5+ |
| Routing | React Router | 6+ |
| HTTP Client | Axios | - |
| State Management | Context API | - |
| Styling | TailwindCSS | 3+ |
| Charts | Recharts | - |

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          Frontend (React)                       │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │
│  │  Home   │ │Products │ │  Cart   │ │Checkout │ │  Admin  │  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘  │
│                      ┌──────────────┐                          │
│                      │ AuthContext  │                          │
│                      │ CartContext  │                          │
│                      └──────────────┘                          │
└─────────────────────────────────────┬─────────────────────────┘
                                      │ HTTP/REST
                                      │ JWT Token
┌─────────────────────────────────────▼─────────────────────────┐
│                       Backend (Spring Boot)                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Controllers Layer                     │   │
│  │  AuthController | ProductController | CartController    │   │
│  │  OrderController | ReviewController | AdminController   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                     Service Layer                        │   │
│  │  AuthService | ProductService | CartService | OrderService│   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                   Repository Layer                       │   │
│  │  UserRepository | ProductRepository | CartRepository     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Security Layer                        │   │
│  │  JWT Filter | SecurityConfig | UserDetails              │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────┬─────────────────────────┘
                                      │ JPA/Hibernate
┌─────────────────────────────────────▼─────────────────────────┐
│                    Database (PostgreSQL)                       │
│  users | roles | products | categories | cart | orders        │
└───────────────────────────────────────────────────────────────┘
```

## Project Structure

```
shop/
├── backend/
│   ├── src/main/java/com/shopmall/
│   │   ├── controller/      # REST API endpoints
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access layer
│   │   ├── entity/          # JPA entities
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── config/          # Configuration classes
│   │   ├── exception/       # Custom exceptions
│   │   └── security/        # JWT and Security config
│   ├── src/main/resources/
│   │   ├── application.yml  # Configuration
│   │   └── db/migration/    # Flyway migrations
│   └── pom.xml              # Maven dependencies
│
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable components
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   ├── context/         # Context providers
│   │   ├── hooks/           # Custom hooks
│   │   ├── utils/           # Utility functions
│   │   └── styles/          # Global styles
│   ├── public/              # Static assets
│   └── package.json         # NPM dependencies
│
└── docs/
    ├── ARCHITECTURE.md      # This file
    ├── API.md               # API documentation
    └── DATABASE.md          # Database schema
```

## Design Patterns

### Backend
- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separate API models from entity models
- **Repository Pattern**: Spring Data JPA repositories
- **Dependency Injection**: Spring IoC container
- **Exception Handling**: @ControllerAdvice global handler

### Frontend
- **Component-Based Architecture**: Reusable React components
- **Context API**: Global state for auth and cart
- **Service Layer**: Centralized API calls
- **Custom Hooks**: Reusable stateful logic

## Security

### Authentication Flow
1. User submits credentials to `/api/auth/login`
2. Backend validates credentials and generates JWT
3. Client stores JWT in localStorage
4. Client includes JWT in Authorization header for subsequent requests
5. JWT filter validates token on protected endpoints

### Authorization
- **Public**: `/api/auth/**`, `/api/products/**`, `/api/categories/**`
- **Authenticated**: `/api/cart/**`, `/api/orders/**`, `/api/reviews`
- **Admin Only**: `/api/admin/**`

## Deployment Architecture

### Development
```
Frontend (localhost:5173)
    │
    ↓ (CORS enabled)
Backend (localhost:8080)
    │
    ↓
PostgreSQL (localhost:5432)
```

### Production
```
                    ┌─────────────────┐
                    │   Nginx/CDN     │
                    │  (Static files) │
                    └─────────────────┘
                            │
                    ┌─────────────────┐
                    │   React App     │
                    └─────────────────┘
                            │
                            ↓ HTTPS
┌───────────────────────────────────────────┐
│              Backend Server                │
│  (Spring Boot - Tomcat/Docker)            │
└───────────────────────────────────────────┘
                    │
                    ↓
┌───────────────────────────────────────────┐
│           PostgreSQL Database              │
│           (Managed/Cloud)                  │
└───────────────────────────────────────────┘
```

## Scalability Considerations

1. **Horizontal Scaling**: Stateless backend with JWT allows multiple instances
2. **Caching**: Redis for session/cache (future enhancement)
3. **CDN**: Static assets served via CDN
4. **Load Balancer**: Nginx/HAProxy for backend distribution
5. **Database**: Read replicas for scaling reads
