# Quickstart: Member System

**Feature**: Member System (001-member-system)
**Prerequisites**: Docker, Docker Compose, Git
**Estimated Setup Time**: 15 minutes

## Overview

This guide will help you set up the Member System locally for development. By the end, you'll have:
- PostgreSQL database with schema initialized
- Redis cache for session storage
- Spring Boot backend API running on `http://localhost:8080`
- Next.js frontend running on `http://localhost:3000`
- OAuth providers configured (Google, Facebook, Discord, GitHub)

---

## Step 1: Clone Repository and Checkout Feature Branch

```bash
# Navigate to project root
cd /path/to/water-ball

# Fetch latest changes
git fetch origin

# Checkout member system feature branch
git checkout 001-member-system

# Verify you're on the correct branch
git branch --show-current
# Output: 001-member-system
```

---

## Step 2: Configure Environment Variables

### Create `.env` File

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your values
nano .env  # or use your preferred editor
```

### Required OAuth Credentials

**Google OAuth Console** (https://console.cloud.google.com/):
1. Create new project or select existing
2. Navigate to "APIs & Services" > "Credentials"
3. Create "OAuth 2.0 Client ID" (Application type: Web application)
4. Add authorized redirect URI: `http://localhost:3000/api/auth/callback/google`
5. Copy Client ID and Client Secret to `.env`

**Facebook App Console** (https://developers.facebook.com/apps/):
1. Create new app or select existing
2. Add "Facebook Login" product
3. Navigate to Settings > Basic
4. Copy App ID and App Secret to `.env`
5. In Facebook Login settings, add redirect URI: `http://localhost:3000/api/auth/callback/facebook`

**Discord Developer Portal** (https://discord.com/developers/applications):
1. Create new application
2. Navigate to OAuth2 settings
3. Add redirect URI: `http://localhost:3000/api/auth/callback/discord`
4. Copy Client ID and Client Secret to `.env`
5. Under OAuth2 > Scopes, select: `identify`, `email`

**GitHub OAuth Apps** (https://github.com/settings/developers):
1. Create new OAuth App
2. Authorization callback URL: `http://localhost:3000/api/auth/callback/github`
3. Copy Client ID and Client Secret to `.env`

### Example `.env` File

```bash
# Database
DB_NAME=waterball
DB_USER=waterball_user
DB_PASSWORD=dev_password_change_in_production

# Redis
REDIS_PASSWORD=dev_redis_password

# JWT Secrets (generate with: openssl rand -base64 32)
JWT_SECRET=your_jwt_secret_min_32_characters_long
NEXTAUTH_SECRET=your_nextauth_secret_min_32_characters_long

# OAuth - Google
GOOGLE_CLIENT_ID=123456789-abcdef.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your_google_client_secret

# OAuth - Facebook
FACEBOOK_CLIENT_ID=your_facebook_app_id
FACEBOOK_CLIENT_SECRET=your_facebook_app_secret

# OAuth - Discord
DISCORD_CLIENT_ID=your_discord_client_id
DISCORD_CLIENT_SECRET=your_discord_client_secret

# OAuth - GitHub
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Frontend
NEXTAUTH_URL=http://localhost:3000
NEXT_PUBLIC_API_URL=http://localhost:8080

# Encryption (generate with: openssl rand -base64 32)
APP_ENCRYPTION_KEY=your_encryption_key_32_characters_long
```

---

## Step 3: Start Services with Docker Compose

```bash
# From project root, start all services
docker-compose up --build

# Expected output:
# ✅ waterball-db        - PostgreSQL ready on port 5432
# ✅ waterball-redis     - Redis ready on port 6379
# ✅ waterball-backend   - Spring Boot API on port 8080
# ✅ waterball-frontend  - Next.js dev server on port 3000

# Services will auto-restart on file changes (hot-reload enabled)
```

**First-Time Setup Duration**: ~2 minutes (Docker image builds)
**Subsequent Starts**: ~30 seconds (cached images)

---

## Step 4: Verify Database Schema

```bash
# Connect to PostgreSQL container
docker exec -it waterball-db psql -U waterball_user -d waterball

# List tables
\dt

# Expected output:
#  Schema |         Name          | Type  |     Owner
# --------+-----------------------+-------+----------------
#  public | user                  | table | waterball_user
#  public | oauth_provider_link   | table | waterball_user
#  public | third_party_account_link | table | waterball_user
#  public | order                 | table | waterball_user
#  public | order_item            | table | waterball_user
#  public | course_ownership      | table | waterball_user
#  public | achievement           | table | waterball_user
#  public | flyway_schema_history | table | waterball_user

# Verify pgcrypto extension
\dx

# Expected: pgcrypto extension enabled

# Exit PostgreSQL
\q
```

---

## Step 5: Test API Endpoints

### Health Check

```bash
# Backend health check
curl http://localhost:8080/actuator/health

# Expected: {"status":"UP"}

# Frontend health check
curl http://localhost:3000
# Expected: HTML response
```

### OAuth Flow Test

1. Open browser: `http://localhost:3000`
2. Click "使用 Google 登入" (Login with Google)
3. Authorize app in Google consent screen
4. Redirected back to `http://localhost:3000/profile`
5. Profile page displays:
   - Nickname (from Google name or email prefix)
   - Email (from Google)
   - Level: Lv. 1 (initial value)
   - Empty achievements list

### API Contract Test

```bash
# Get session (requires authentication)
curl -X GET http://localhost:8080/api/auth/session \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# Expected:
# {
#   "user_id": "550e8400-e29b-41d4-a716-446655440000",
#   "nickname": "John Doe",
#   "email": "john@example.com",
#   "level": 1,
#   "oauth_providers": ["GOOGLE"]
# }
```

---

## Step 6: Development Workflow

### Backend Development

```bash
# Backend logs
docker logs -f waterball-backend

# Run backend tests
docker exec -it waterball-backend ./mvnw test

# Hot-reload: Edit files in backend/src/, changes auto-compile
```

### Frontend Development

```bash
# Frontend logs
docker logs -f waterball-frontend

# Run frontend tests
docker exec -it waterball-frontend npm test

# Hot-reload: Edit files in frontend/src/, browser auto-refreshes
```

### Database Migrations

```bash
# Create new migration
cd backend/src/main/resources/db/migration
touch V6__add_new_field.sql

# Edit migration file
# Restart backend container to apply
docker-compose restart backend
```

---

## Step 7: Access Web Interfaces

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | OAuth (Google/Facebook) |
| Backend API | http://localhost:8080/api | Bearer token from OAuth |
| API Docs (Swagger) | http://localhost:8080/swagger-ui.html | - |
| Database (pgAdmin) | Install pgAdmin locally | Host: localhost, Port: 5432, User: waterball_user |
| Redis (RedisInsight) | Install RedisInsight locally | Host: localhost, Port: 6379, Password: from .env |

---

## Troubleshooting

### Backend Fails to Start

**Symptom**: `waterball-backend` exits with error
```
Database connection failed
```

**Solution**:
```bash
# Check database is healthy
docker-compose ps

# Verify database container logs
docker logs waterball-db

# Common fix: Wait 10 seconds for PostgreSQL to initialize, then restart
docker-compose restart backend
```

### OAuth Redirect Error

**Symptom**: "Redirect URI mismatch" error from OAuth provider

**Solution**:
- Verify OAuth console has exact callback URL: `http://localhost:3000/api/auth/callback/{provider}`
- Check `.env` has correct `NEXTAUTH_URL=http://localhost:3000`
- Restart frontend after .env changes: `docker-compose restart frontend`

### Port Already in Use

**Symptom**: `Cannot start service: port is already allocated`

**Solution**:
```bash
# Find process using port 3000 (frontend)
lsof -i :3000
kill -9 <PID>

# Or use different ports in docker-compose.yml
# Change "3000:3000" to "3001:3000" for frontend
```

### Database Schema Missing

**Symptom**: `Table "user" does not exist`

**Solution**:
```bash
# Force Flyway re-migration
docker-compose down -v  # Deletes volumes (WARNING: destroys data)
docker-compose up --build

# Or manually run migrations
docker exec -it waterball-backend ./mvnw flyway:migrate
```

---

## Testing the Full User Flow

### 1. User Registration (Google OAuth)

1. Navigate to: `http://localhost:3000`
2. Click "使用 Google 登入"
3. Complete Google OAuth consent
4. Redirected to profile page
5. Verify nickname populated from Google name

### 2. Profile Update

1. On profile page, click "編輯個人資料" (Edit Profile)
2. Update fields: gender, birthday, location, occupation, GitHub link
3. Click "儲存" (Save)
4. Verify confirmation message: "個人資料已更新"
5. Refresh page, verify fields persisted

### 3. Third-Party Account Linking

1. Navigate to: `http://localhost:3000/linking`
2. Click "綁定 Discord" (Link Discord)
3. Complete Discord OAuth
4. Verify status shows "已綁定" with Discord username
5. Click "解除綁定" (Unlink), confirm, verify status shows "未綁定"

### 4. Session Persistence

1. Log in via Google
2. Close browser
3. Reopen browser, navigate to `http://localhost:3000/profile`
4. Verify still logged in (session persisted in Redis)
5. Wait 7 days (or manually delete Redis key) → session expires, redirected to login

### 5. Account Auto-Merge

1. Register with Google using email `test@example.com`
2. Log out
3. Attempt to register with Facebook using same email `test@example.com`
4. Verify: Logged into same account, both Google AND Facebook listed in oauth_providers

---

## Next Steps

### Run Tests (TDD Workflow)

```bash
# Backend: Run all tests
docker exec -it waterball-backend ./mvnw test

# Backend: Run specific test class
docker exec -it waterball-backend ./mvnw test -Dtest=AuthServiceTest

# Frontend: Run all tests
docker exec -it waterball-frontend npm test

# Frontend: Run tests in watch mode
docker exec -it waterball-frontend npm test -- --watch
```

### Generate Tasks

```bash
# Generate task list for implementation
/speckit.tasks

# This will create specs/001-member-system/tasks.md with:
# - Dependency-ordered tasks per user story
# - Test-first tasks (write tests before implementation)
# - Parallelizable tasks marked with [P]
```

### Implement User Story 1 (MVP)

Focus on **User Story 1 - OAuth Registration and Login** for MVP:
- Write contract tests for OAuth endpoints (RED)
- Implement AuthController, AuthService (GREEN)
- Refactor and clean up (REFACTOR)
- Deploy MVP: Users can register and login

---

## Useful Commands

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Restart specific service
docker-compose restart backend

# Stop all services
docker-compose down

# Stop and remove volumes (DESTROYS DATA)
docker-compose down -v

# Rebuild specific service
docker-compose up --build backend

# Execute command in container
docker exec -it waterball-backend bash
docker exec -it waterball-frontend sh

# Check service health
docker-compose ps
```

---

## Additional Resources

- **Feature Spec**: [spec.md](./spec.md) - User stories and requirements
- **Implementation Plan**: [plan.md](./plan.md) - Technical architecture
- **Data Model**: [data-model.md](./data-model.md) - Database schema
- **API Contracts**: [contracts/](./contracts/) - OpenAPI specifications
- **Research**: [research.md](./research.md) - Technical decisions
- **Constitution**: `/.specify/memory/constitution.md` - Development standards

---

## Support

- **Documentation Issues**: Update this quickstart.md with missing steps
- **Bug Reports**: Create issue on GitHub with logs and steps to reproduce
- **OAuth Setup Help**: Consult provider documentation linked in Step 2
