# Water Ball Platform - Infrastructure Setup Guide

> This guide documents the completed infrastructure setup (Phase 1 & 2) for the Member System feature.

## 📊 Implementation Progress

### ✅ Completed: Phase 1 & 2 (25/180 tasks)

- **Phase 1: Setup** - 9/9 tasks ✅
- **Phase 2: Foundational Infrastructure** - 16/16 tasks ✅
- **Phase 3: User Story 1 (MVP)** - 0/42 tasks ⏳

**Overall Progress**: 13.9% complete (25/180 tasks)

---

## 🏗️ What's Been Built

### Backend Infrastructure (Spring Boot)

**Core Application**:
- ✅ Maven project structure with `pom.xml`
- ✅ `WaterBallApplication.java` main class
- ✅ `application.yml` configuration
- ✅ Dockerfile with hot-reload support

**Security & Authentication**:
- ✅ `JwtTokenProvider` - Generate/validate JWT tokens (15-min access, 7-day refresh)
- ✅ `JwtAuthenticationFilter` - Extract JWT from requests, set SecurityContext
- ✅ `SecurityConfig` - Spring Security filter chain (stateless sessions)
- ✅ `CorsConfig` - CORS configuration (allow frontend origin, credentials enabled)

**Database & Caching**:
- ✅ `RedisConfig` - Redis connection for session storage
- ✅ Flyway migrations configured
- ✅ V1: pgcrypto extension enabled
- ✅ V2: Encryption helper functions (encrypt_pii, decrypt_pii, hash_email)

**Exception Handling**:
- ✅ `GlobalExceptionHandler` - Catch all exceptions, return Chinese error messages
- ✅ Custom exceptions: `ValidationException`, `UnauthorizedException`, `ForbiddenException`, `ResourceNotFoundException`
- ✅ Base DTOs: `ErrorResponse`, `PaginationResponse`

### Frontend Infrastructure (Next.js)

**Authentication**:
- ✅ NextAuth.js configured in `app/api/auth/[...nextauth]/route.ts`
- ✅ Google & Facebook OAuth providers

**Utilities**:
- ✅ `lib/api.ts` - Axios API client with JWT interceptor and auto-refresh
- ✅ `lib/storage.ts` - localStorage helpers (tokens, profile drafts, redirect URLs)
- ✅ `hooks/useAuth.ts` - Custom hook for session management (login, logout, refresh)

### DevOps

**Docker Compose Services**:
1. ✅ **PostgreSQL 15** - Database (port 5432)
2. ✅ **Redis 7** - Session cache (port 6379)
3. ✅ **Backend API** - Spring Boot (port 8080)
4. ✅ **Frontend** - Next.js (port 3000)

**Configuration**:
- ✅ `.env` file with generated secrets
- ✅ `.gitignore` and `.dockerignore` configured
- ✅ Checkstyle for backend code quality
- ✅ Prettier for frontend code formatting

---

## 🚀 Quick Start

### 1. Validate Setup

```bash
./validate-setup.sh
```

**Expected Output**:
```
✓ All critical infrastructure files are in place
✓ Docker Compose configuration is valid
⚠ GOOGLE_CLIENT_ID is placeholder - OAuth will not work until configured
```

### 2. Configure OAuth (Optional for Testing)

To test OAuth flows, add real credentials to `.env`:

```bash
# Get from https://console.cloud.google.com/
GOOGLE_CLIENT_ID=your_actual_client_id
GOOGLE_CLIENT_SECRET=your_actual_client_secret

# Get from https://developers.facebook.com/apps/
FACEBOOK_CLIENT_ID=your_actual_app_id
FACEBOOK_CLIENT_SECRET=your_actual_app_secret
```

**OAuth Redirect URIs**:
- Google: `http://localhost:3000/api/auth/callback/google`
- Facebook: `http://localhost:3000/api/auth/callback/facebook`

### 3. Start Services

```bash
# Build and start all services
docker compose up --build

# Or run in background
docker compose up --build -d
```

**First-time startup**: ~3-5 minutes (building images, downloading dependencies)

### 4. Verify Services

Once services are running:

| Service | URL | Status Check |
|---------|-----|--------------|
| Frontend | http://localhost:3000 | Should load Next.js page |
| Backend | http://localhost:8080/actuator/health | Should return `{"status":"UP"}` |
| PostgreSQL | localhost:5432 | Connect with `psql` |
| Redis | localhost:6379 | Connect with `redis-cli` |

### 5. View Logs

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f db
docker compose logs -f redis
```

### 6. Stop Services

```bash
# Stop containers
docker compose down

# Stop and remove volumes (⚠️ destroys data)
docker compose down -v
```

---

## 📁 File Structure

```
water-ball/
├── backend/
│   ├── src/main/
│   │   ├── java/tw/waterballsa/
│   │   │   ├── WaterBallApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── *Exception.java (4 custom exceptions)
│   │   │   └── dto/
│   │   │       ├── ErrorResponse.java
│   │   │       └── PaginationResponse.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           ├── V1__enable_pgcrypto.sql
│   │           └── V2__encryption_functions.sql
│   ├── Dockerfile
│   ├── pom.xml
│   └── checkstyle.xml
├── frontend/
│   ├── src/
│   │   ├── app/api/auth/[...nextauth]/route.ts
│   │   ├── lib/
│   │   │   ├── api.ts
│   │   │   └── storage.ts
│   │   └── hooks/
│   │       └── useAuth.ts
│   ├── Dockerfile
│   ├── package.json
│   └── .prettierrc
├── docker-compose.yml
├── .env
├── .env.example
├── validate-setup.sh
├── SETUP.md (this file)
└── README.md
```

---

## 🔐 Security Configuration

### JWT Tokens

**Access Token**:
- Expiry: 15 minutes
- Purpose: API authentication
- Stored: localStorage
- Format: `Bearer <token>`

**Refresh Token**:
- Expiry: 7 days
- Purpose: Obtain new access tokens
- Stored: localStorage (will be HTTP-only cookie in production)

### PII Encryption

**Encrypted Fields** (PostgreSQL with pgcrypto):
- Email → `email_encrypted` (BYTEA)
- Birthday → `birthday_encrypted` (BYTEA)
- Location → `location_encrypted` (BYTEA)

**Encryption Functions**:
```sql
-- Encrypt data
SELECT encrypt_pii('user@example.com', 'encryption_key');

-- Decrypt data
SELECT decrypt_pii(encrypted_value, 'encryption_key');

-- Hash email for uniqueness constraint
SELECT hash_email('user@example.com');
```

### CORS Configuration

**Allowed Origins**: `http://localhost:3000` (configurable via `.env`)

**Allowed Methods**: GET, POST, PUT, PATCH, DELETE, OPTIONS

**Credentials**: Enabled (required for JWT tokens and cookies)

### Endpoint Security

**Public Endpoints** (no authentication):
- `/auth/oauth/**` - OAuth flows
- `/auth/refresh` - Token refresh
- `/actuator/health` - Health check

**Protected Endpoints** (JWT required):
- `/auth/session` - Get session info
- `/auth/logout` - Logout
- `/profile/**` - Profile management
- `/linking/**` - Third-party linking
- `/orders/**` - Order history
- `/courses/owned` - Owned courses

---

## 🧪 Testing Infrastructure

### Database Connection

```bash
# Connect to PostgreSQL
docker exec -it waterball-db psql -U waterball_user -d waterball

# List tables (should show flyway_schema_history)
\dt

# Check pgcrypto extension
\dx

# Test encryption function
SELECT encrypt_pii('test data', 'test_key');

# Exit
\q
```

### Redis Connection

```bash
# Connect to Redis
docker exec -it waterball-redis redis-cli -a dev_redis_password

# List all keys
KEYS *

# Set test value
SET test:key "test_value"

# Get test value
GET test:key

# Delete test key
DEL test:key

# Exit
exit
```

### Backend Health Check

```bash
# Check if backend is running
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

### Frontend Health Check

```bash
# Check if frontend is running
curl http://localhost:3000

# Expected: HTML response (Next.js page)
```

---

## 🐛 Troubleshooting

### Backend Won't Start

**Symptom**: `waterball-backend` exits immediately

**Common Causes**:
1. Database not ready yet
2. Missing environment variables
3. Port 8080 already in use

**Solutions**:
```bash
# Check database status
docker compose ps

# View backend logs
docker compose logs backend

# Restart backend after database is ready
docker compose restart backend

# Check if port 8080 is in use
lsof -i :8080
```

### Database Connection Failed

**Symptom**: `Connection refused` or `database does not exist`

**Solutions**:
```bash
# Recreate database (⚠️ destroys data)
docker compose down -v
docker compose up --build

# Check database logs
docker compose logs db
```

### Redis Connection Failed

**Symptom**: `NOAUTH Authentication required` or connection timeout

**Solutions**:
```bash
# Check Redis password in .env matches docker-compose.yml
cat .env | grep REDIS_PASSWORD

# Restart Redis
docker compose restart redis

# Check Redis logs
docker compose logs redis
```

### Frontend Build Failed

**Symptom**: `npm install` errors or missing dependencies

**Solutions**:
```bash
# Rebuild frontend
docker compose build --no-cache frontend

# Check Node.js version in Dockerfile (should be 20-alpine)
cat frontend/Dockerfile

# View frontend logs
docker compose logs frontend
```

### Port Already in Use

**Symptom**: `Error starting userland proxy: listen tcp4 0.0.0.0:3000: bind: address already in use`

**Solutions**:
```bash
# Find process using the port
lsof -i :3000  # or :8080, :5432, :6379

# Kill the process
kill -9 <PID>

# Or change port in docker-compose.yml
# Change "3000:3000" to "3001:3000"
```

---

## 📊 Database Schema Status

### Completed Migrations

| Version | File | Status | Description |
|---------|------|--------|-------------|
| V1 | `V1__enable_pgcrypto.sql` | ✅ Complete | Enable pgcrypto extension |
| V2 | `V2__encryption_functions.sql` | ✅ Complete | Create encryption helper functions |

### Pending Migrations (User Story 1)

| Version | File | Status | Description |
|---------|------|--------|-------------|
| V3 | `V3__create_users_table.sql` | ⏳ Pending | Create users table |
| V4 | `V4__create_oauth_provider_links.sql` | ⏳ Pending | Create oauth_provider_link table |
| V5 | `V5__create_user_indexes.sql` | ⏳ Pending | Create indexes for users |
| V6 | `V6__create_oauth_indexes.sql` | ⏳ Pending | Create indexes for OAuth links |

---

## 📚 Next Steps

### Phase 3: User Story 1 - OAuth Authentication MVP (42 tasks)

**Goal**: Enable users to register and login using Google or Facebook OAuth

**What Needs to be Implemented**:

1. **Database Schema** (4 tasks)
   - Users table with encrypted PII fields
   - OAuth provider links table
   - Indexes for performance

2. **Backend Models** (3 tasks)
   - User JPA entity
   - OAuthProviderLink JPA entity
   - PII encryption AttributeConverter

3. **Backend Repositories** (2 tasks)
   - UserRepository
   - OAuthProviderLinkRepository

4. **Backend Services** (4 tasks)
   - AuthService (OAuth callback, account merge, session management)

5. **Backend Controllers** (6 tasks)
   - AuthController with 5 endpoints

6. **Frontend** (4 tasks)
   - Login page
   - OAuth buttons
   - Session management

7. **Tests** (9 tasks)
   - Contract tests for all endpoints
   - Integration tests for OAuth flows
   - Account merge tests

**To Start Phase 3**:
```bash
# Continue implementation
/speckit.implement
```

---

## 🔗 Documentation

- **Feature Specification**: [specs/001-member-system/spec.md](specs/001-member-system/spec.md)
- **Implementation Plan**: [specs/001-member-system/plan.md](specs/001-member-system/plan.md)
- **Data Model**: [specs/001-member-system/data-model.md](specs/001-member-system/data-model.md)
- **API Contracts**: [specs/001-member-system/contracts/](specs/001-member-system/contracts/)
- **Task Breakdown**: [specs/001-member-system/tasks.md](specs/001-member-system/tasks.md) (25/180 complete)

---

## ✅ Validation Checklist

Run through this checklist to verify infrastructure is working:

- [ ] `./validate-setup.sh` passes all checks
- [ ] `docker compose up` starts all 4 services
- [ ] `curl http://localhost:8080/actuator/health` returns `{"status":"UP"}`
- [ ] `curl http://localhost:3000` returns HTML
- [ ] `docker exec -it waterball-db psql -U waterball_user -d waterball` connects successfully
- [ ] `docker exec -it waterball-redis redis-cli -a dev_redis_password` connects successfully
- [ ] Backend logs show "Started WaterBallApplication"
- [ ] Frontend logs show "Ready in X ms"
- [ ] No error messages in any service logs

---

## 💡 Tips

### Development Workflow

**Backend Changes**:
- Edit files in `backend/src/`
- Changes auto-compile (Spring Boot DevTools)
- Refresh API endpoint to see changes

**Frontend Changes**:
- Edit files in `frontend/src/`
- Browser auto-refreshes (Next.js Fast Refresh)

**Database Changes**:
- Create new migration: `backend/src/main/resources/db/migration/V{N}__{description}.sql`
- Restart backend: `docker compose restart backend`
- Flyway applies migration automatically

### Performance

**Startup Times**:
- PostgreSQL: ~5 seconds
- Redis: ~2 seconds
- Backend: ~30-60 seconds (Maven dependencies, compilation)
- Frontend: ~10-20 seconds (npm dependencies)

**Optimization**:
- Keep containers running during development
- Use `docker compose restart <service>` instead of full rebuild
- Maven dependencies cached in Docker volume

### Cleanup

**Remove Everything**:
```bash
# Stop containers, remove volumes and networks
docker compose down -v

# Remove Docker images
docker rmi $(docker images -q 'water-ball*')

# Remove all stopped containers
docker system prune -a
```

---

Last Updated: 2025-11-14
Infrastructure Version: 1.0.0 (Phase 1 & 2 Complete)
