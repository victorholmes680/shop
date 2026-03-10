# 🚀 Shopping Mall System - Startup Manual

## Prerequisites (Install These First)

| Software | Version | Required? |
|----------|---------|-----------|
| **Java JDK** | 17+ | ✅ Yes (for backend) |
| **Maven** | 3.6+ | ✅ Yes (for backend) |
| **Node.js** | 18+ | ✅ Yes (for frontend) |
| **npm** | 9+ | ✅ Yes (for frontend) |
| **Docker** | 20+ | ⚪ Optional (for containerized deployment) |
| **PostgreSQL** | 14+ | ⚪ Optional (H2 is used by default) |

---

## 📋 Quick Start Guide

### Option 1: Docker (Easiest - One Command)

```bash
# 1. Go to project directory
cd /root/workspace/shop

# 2. Start everything (database, backend, frontend)
docker-compose up -d

# 3. Wait for services to start (30-60 seconds)

# 4. Access the application
# Frontend:  http://localhost:3000
# Backend:   http://localhost:8080/api
# Swagger:   http://localhost:8080/swagger-ui
```

---

### Option 2: Local Development (Step by Step)

#### **Step 1: Start Backend**

```bash
# 1. Go to backend directory
cd /root/workspace/shop/backend

# 2. Build the project (first time only)
mvn clean install

# 3. Run the backend
mvn spring-boot:run
```

**Backend will start at:** `http://localhost:8080/api`

**Verify it's working:**
- Swagger UI: http://localhost:8080/swagger-ui
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:shopmall`
  - Username: `sa`
  - Password: *(leave empty)*

---

#### **Step 2: Start Frontend (New Terminal)**

```bash
# 1. Go to project directory
cd /root/workspace/shop

# 2. Install dependencies (first time only)
npm install

# 3. Start frontend development server
npm run dev
```

**Frontend will start at:** `http://localhost:5173`

---

## 🔑 Default Login Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@shopmall.com | admin123 |
| User | user@example.com | user123 |

---

## 📊 What Happens on Startup

### Backend Startup:
1. ✅ H2 Database starts (in-memory)
2. ✅ Flyway runs migrations:
   - `V1__Create_schema.sql` - Creates tables
   - `V2__Insert_seed_data.sql` - Inserts admin user & categories
3. ✅ Spring Boot API starts on port 8080
4. ✅ JWT authentication is ready
5. ✅ API endpoints are available

### Frontend Startup:
1. ✅ Vite dev server starts on port 5173
2. ✅ React app loads
3. ✅ API client configured to http://localhost:8080/api

---

## 🛠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 8080 already in use | Change port in `application.yml` or stop the process using port 8080 |
| Port 5173 already in use | Frontend will auto-select next available port |
| Database connection error | Check H2 is enabled in `application.yml` (default: enabled) |
| CORS errors | Verify CORS config allows `http://localhost:5173` |
| JWT errors | Check `JWT_SECRET` in `application.yml` |

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `backend/src/main/resources/application.yml` | Backend configuration |
| `backend/pom.xml` | Maven dependencies |
| `frontend/.env` | Frontend environment variables |
| `docker-compose.yml` | Docker deployment |
| `deployment/init-db.sql` | Database seed data |

---

## ✅ Startup Checklist

- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] Node.js 18+ installed
- [ ] Navigate to `/root/workspace/shop/backend`
- [ ] Run `mvn spring-boot:run` (in one terminal)
- [ ] Navigate to `/root/workspace/shop`
- [ ] Run `npm run dev` (in another terminal)
- [ ] Open http://localhost:5173
- [ ] Login with `admin@shopmall.com` / `admin123`

---

## 🌐 Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | admin@shopmall.com / admin123 |
| Backend API | http://localhost:8080/api | JWT token required |
| Swagger UI | http://localhost:8080/swagger-ui | - |
| H2 Console | http://localhost:8080/h2-console | sa / (empty) |

---

## 📚 Additional Documentation

- [Architecture](docs/ARCHITECTURE.md) - System architecture and design
- [API Documentation](docs/API.md) - Complete REST API reference
- [Database Schema](docs/DATABASE.md) - Database structure

---

**That's it! The system should now be running.** 🎉
