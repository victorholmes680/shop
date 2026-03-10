# Frontend Manual Startup Guide

This guide explains how to start the ShopMall frontend manually without using Docker.

## Prerequisites

- Node.js (v20 or higher recommended)
- npm or yarn package manager
- Backend API running on `http://localhost:8080`

---

## Quick Start

### 1. Navigate to the frontend directory

```bash
cd /probe/shop/frontend
```

### 2. Install dependencies

```bash
npm install
```

### 3. Start the development server

```bash
npm run dev
```

The Vite development server will start at **http://localhost:5173**

---

## Available Commands

| Command | Description |
|---------|-------------|
| `npm run dev` | Start development server with hot-reload |
| `npm run build` | Build for production (creates `dist/` folder) |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint for code quality checks |

---

## Configuration

### Development Server

- **Default URL**: `http://localhost:5173`
- **Network Access**: `http://YOUR_IP:5173`

### Backend Connection

The frontend expects the backend API at `http://localhost:8080`

To start the backend (if not already running):

```bash
cd /probe/shop/backend
mvn spring-boot:run
```

Or via Docker:

```bash
docker-compose up -d postgres backend
```

---

## Production Build

To build and serve the frontend in production mode:

```bash
# 1. Build the frontend
cd /probe/shop/frontend
npm run build

# 2. Preview the production build
npm run preview
```

The preview server runs on **http://localhost:4173**

---

## Advanced Options

### Use a Different Port

If port 5173 is already in use:

```bash
npm run dev -- --port 3000
```

### Access from Other Devices

To make the dev server accessible from other devices on your network:

```bash
npm run dev -- --host
```

### Clear Dependencies

If you encounter dependency issues:

```bash
# Remove node_modules and lock file
rm -rf node_modules package-lock.json

# Reinstall dependencies
npm install
```

---

## Tech Stack

The frontend uses:

- **React 19** - UI framework
- **Vite** - Build tool and dev server
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **React Router** - Client-side routing
- **Zustand** - State management
- **Axios** - HTTP client
- **Headless UI** - Accessible UI components
- **Lucide React** - Icon library

---

## Project Structure

```
frontend/
├── public/          # Static assets
├── src/             # Source code
├── index.html       # Entry HTML
├── package.json     # Dependencies
├── vite.config.ts   # Vite configuration
├── tsconfig.json    # TypeScript configuration
├── tailwind.config.js # Tailwind configuration
└── Dockerfile       # Docker configuration (optional)
```

---

## Troubleshooting

### Port Already in Use

```bash
# Check what's using the port
lsof -i :5173

# Kill the process if needed
kill -9 <PID>

# Or use a different port
npm run dev -- --port 3000
```

### Module Not Found Errors

```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### Backend Connection Issues

- Verify backend is running on port 8080
- Check CORS settings in backend configuration
- Ensure API base URL is correct in frontend code

---

## Docker Alternative

If you prefer using Docker (after manual development):

```bash
# From project root
docker-compose up -d frontend
```

The frontend will be available at **http://localhost:3000**

---

## Support

For issues or questions, refer to:
- **Vite Documentation**: https://vitejs.dev/
- **React Documentation**: https://react.dev/
- **Project README**: /probe/shop/README.md
