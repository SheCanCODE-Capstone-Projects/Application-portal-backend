# Docker Setup Guide

## Prerequisites
- Docker Desktop installed
- Docker Compose installed

## Quick Start

### 1. Configure Environment Variables
```bash
cp .env.example .env
# Edit .env with your actual values
```

### 2. Start All Services
```bash
docker-compose up -d
```

### 3. View Logs
```bash
docker-compose logs -f
```

### 4. Stop Services
```bash
docker-compose down
```

### 5. Stop and Remove Volumes (Clean Reset)
```bash
docker-compose down -v
```

## Service URLs
- Backend API: http://localhost:8084
- PostgreSQL: localhost:5432

## Troubleshooting

### Backend can't connect to database
```bash
docker-compose logs postgres
docker-compose logs backend
```

### Rebuild containers
```bash
docker-compose up -d --build
```

### Access PostgreSQL directly
```bash
docker exec -it application-portal-postgres psql -U postgres -d application_portal_db
```

## Development Workflow

### Local Development (without Docker)
Update application.com.igirerwanda.application_portal_backend.resources.properties to use `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` from .env

### Docker Development
Services automatically use `POSTGRES_*` variables and Docker networking
