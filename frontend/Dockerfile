# Multi-stage Dockerfile for React Frontend

# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN rm -f package-lock.json && npm install --legacy-peer-deps

# Explicitly reinstall vite to fix corrupted binary
RUN npm uninstall vite && npm install --save-dev vite

# Copy source code and build
COPY . .

# Accept API URL as build argument (default to relative path for nginx proxy)
ARG VITE_API_URL=/api/
ENV VITE_API_URL=${VITE_API_URL}

RUN npm run build

# Stage 2: Runtime with nginx
FROM nginx:alpine

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Ensure proper permissions for nginx
RUN chown -R nginx:nginx /usr/share/nginx/html && \
  chown -R nginx:nginx /var/cache/nginx && \
  chown -R nginx:nginx /var/log/nginx && \
  touch /var/run/nginx.pid && \
  chown -R nginx:nginx /var/run/nginx.pid

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Start nginx as nginx user (nginx:alpine is configured for this)
USER nginx
CMD ["nginx", "-g", "daemon off;"]
