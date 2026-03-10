# Shopping Mall System - Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Deployment](#docker-deployment)
4. [Production Deployment](#production-deployment)
5. [Environment Variables](#environment-variables)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### For Local Development:
- **Java**: 17 or higher
- **Node.js**: 20.19+ or 22.12+
- **Maven**: 3.9+
- **PostgreSQL**: 16+ (or H2 for quick testing)

### For Docker Deployment:
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

---

## Local Development Setup

### 1. Clone and Setup
```bash
cd /root/workspace/shop
```

### 2. Backend Setup
```bash
cd backend

# Configure database (edit src/main/resources/application.yml)
# For H2 (in-memory, no setup required):
# - Already configured for development

# For PostgreSQL:
# - Install PostgreSQL
# - Create database: createdb shopmall
# - Update application.yml with your credentials

# Run the backend
mvn spring-boot:run

# Backend will be available at: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

### 3. Frontend Setup
```bash
# From project root
cd /root/workspace/shop

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend will be available at: http://localhost:5173
```

### 4. Default Accounts
- **Admin**: admin@shopmall.com / admin123
- Register new customers via the UI

---

## Docker Deployment

### Quick Start (All Services)
```bash
cd /root/workspace/shop

# Build and start all services
docker-compose -f deployment/docker-compose.yml up -d

# View logs
docker-compose -f deployment/docker-compose.yml logs -f

# Stop services
docker-compose -f deployment/docker-compose.yml down

# Stop and remove volumes
docker-compose -f deployment/docker-compose.yml down -v
```

### Access Points
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost/swagger-ui
- **PostgreSQL**: localhost:5432

### Individual Services

#### Backend Only
```bash
docker build -f deployment/Dockerfile.backend -t shopmall-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/shopmall \
  -e SPRING_DATASOURCE_USERNAME=shopuser \
  -e SPRING_DATASOURCE_PASSWORD=shoppass123 \
  shopmall-backend
```

#### Frontend Only
```bash
docker build -f deployment/Dockerfile.frontend -t shopmall-frontend .
docker run -p 80:80 shopmall-frontend
```

---

## Production Deployment

### 1. Environment Configuration

Create a `.env.production` file:
```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/shopmall
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password_here

# JWT Security (CHANGE THESE!)
JWT_SECRET=your-super-secret-jwt-key-at-least-256-bits-long-change-in-production
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Frontend API URL
VITE_API_URL=https://api.yourdomain.com
```

### 2. Security Checklist
- [ ] Change all default passwords
- [ ] Set strong JWT_SECRET (minimum 256 bits)
- [ ] Enable HTTPS/SSL
- [ ] Configure firewall rules
- [ ] Set up database backups
- [ ] Enable rate limiting
- [ ] Configure CORS properly
- [ ] Set up monitoring/logging

### 3. Cloud Deployment Options

#### AWS ECS/Fargate
```bash
# Build and push to ECR
docker build -f deployment/Dockerfile.backend -t shopmall-backend .
docker tag shopmall-backend:latest <ecr-repo-url>:latest
docker push <ecr-repo-url>:latest

# Deploy using ECS task definition
```

#### Google Cloud Run
```bash
gcloud run deploy shopmall-backend \
  --source=backend/ \
  --platform=managed \
  --region=us-central1 \
  --allow-unauthenticated \
  --set-env-vars="SPRING_DATASOURCE_URL=..."
```

#### Azure Container Instances
```bash
az container create \
  --resource-group shopmall-rg \
  --name shopmall-backend \
  --image shopmall-backend:latest \
  --ports 8080 \
  --environment-variables SPRING_DATASOURCE_URL=...
```

### 4. Database Setup

#### PostgreSQL (Recommended)
```sql
-- Create database and user
CREATE DATABASE shopmall;
CREATE USER shopuser WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE shopmall TO shopuser;

-- Run migrations (Flyway will handle this automatically)
```

---

## Environment Variables

### Backend Variables
| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | JDBC URL | jdbc:postgresql://localhost:5432/shopmall |
| SPRING_DATASOURCE_USERNAME | Database user | shopuser |
| SPRING_DATASOURCE_PASSWORD | Database password | shoppass123 |
| JWT_SECRET | JWT signing key | (must be set) |
| JWT_EXPIRATION | Token expiration (ms) | 86400000 |
| CORS_ALLOWED_ORIGINS | Allowed CORS origins | * |

### Frontend Variables
| Variable | Description | Default |
|----------|-------------|---------|
| VITE_API_URL | Backend API URL | http://localhost:8080 |

---

## Troubleshooting

### Backend Issues

#### Port 8080 Already in Use
```bash
# Find and kill the process
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml:
server.port: 8081
```

#### Database Connection Failed
```bash
# Check PostgreSQL is running
pg_isready -h localhost -p 5432

# Check connection string in application.yml
```

#### JWT Token Expired
- Tokens expire after 24 hours by default
- Update JWT_EXPIRATION if needed
- Users must log in again after expiration

### Frontend Issues

#### Module Not Found
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### API Connection Refused
- Check VITE_API_URL in .env
- Ensure backend is running
- Check CORS configuration

### Docker Issues

#### Container Exits Immediately
```bash
# View logs
docker logs <container-id>

# Common issues:
# - Database not ready (check depends_on)
# - Port conflicts
# - Missing environment variables
```

#### Build Fails
```bash
# Clear Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

---

## Performance Tuning

### Backend
- Enable database connection pooling
- Configure JVM heap size: `-Xmx1g -Xms512m`
- Enable response compression
- Consider Redis for session caching

### Frontend
- Enable code splitting (already configured)
- Use CDN for static assets
- Enable gzip compression (nginx)
- Consider SSR for SEO-critical pages

---

## Monitoring

### Health Checks
- Backend: `GET /actuator/health`
- Frontend: HTTP 200 on root path

### Logging
- Backend: Logs to stdout (use Docker drivers)
- Frontend: Browser console + error tracking

### Metrics (Optional)
- Spring Boot Actuator endpoints
- Prometheus integration
- APM tools (New Relic, DataDog)

---

## Backup Strategy

### Database Backups
```bash
# Daily backup
pg_dump -U shopuser shopmall > backup_$(date +%Y%m%d).sql

# Restore
psql -U shopuser shopmall < backup_20240101.sql
```

### Application Backup
- Version control (Git)
- Docker images (tagged releases)
- Environment variables (secure storage)

---

## Support

For issues or questions:
1. Check logs: `docker-compose logs -f`
2. Review this guide
3. Check API docs: http://localhost:8080/swagger-ui
