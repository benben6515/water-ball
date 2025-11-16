#!/bin/bash

# Validation script for Water Ball Platform infrastructure
# Tests configuration and setup without starting services

set -e

echo "======================================"
echo "Water Ball Platform - Setup Validation"
echo "======================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print success
success() {
    echo -e "${GREEN}✓${NC} $1"
}

# Function to print error
error() {
    echo -e "${RED}✗${NC} $1"
}

# Function to print warning
warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

echo "1. Checking required files..."
echo "------------------------------"

# Check backend files
if [ -f "backend/pom.xml" ]; then
    success "backend/pom.xml exists"
else
    error "backend/pom.xml not found"
    exit 1
fi

if [ -f "backend/src/main/resources/application.yml" ]; then
    success "backend/application.yml exists"
else
    error "backend/application.yml not found"
    exit 1
fi

if [ -f "backend/Dockerfile" ]; then
    success "backend/Dockerfile exists"
else
    error "backend/Dockerfile not found"
    exit 1
fi

# Check frontend files
if [ -f "frontend/package.json" ]; then
    success "frontend/package.json exists"
else
    error "frontend/package.json not found"
    exit 1
fi

if [ -f "frontend/Dockerfile" ]; then
    success "frontend/Dockerfile exists"
else
    error "frontend/Dockerfile not found"
    exit 1
fi

# Check docker-compose
if [ -f "docker-compose.yml" ]; then
    success "docker-compose.yml exists"
else
    error "docker-compose.yml not found"
    exit 1
fi

# Check .env
if [ -f ".env" ]; then
    success ".env file exists"
else
    error ".env file not found"
    exit 1
fi

echo ""
echo "2. Checking database migrations..."
echo "-----------------------------------"

if [ -f "backend/src/main/resources/db/migration/V1__enable_pgcrypto.sql" ]; then
    success "V1__enable_pgcrypto.sql exists"
else
    error "V1__enable_pgcrypto.sql not found"
    exit 1
fi

if [ -f "backend/src/main/resources/db/migration/V2__encryption_functions.sql" ]; then
    success "V2__encryption_functions.sql exists"
else
    error "V2__encryption_functions.sql not found"
    exit 1
fi

echo ""
echo "3. Checking security infrastructure..."
echo "---------------------------------------"

if [ -f "backend/src/main/java/tw/waterballsa/security/JwtTokenProvider.java" ]; then
    success "JwtTokenProvider exists"
else
    error "JwtTokenProvider not found"
    exit 1
fi

if [ -f "backend/src/main/java/tw/waterballsa/security/JwtAuthenticationFilter.java" ]; then
    success "JwtAuthenticationFilter exists"
else
    error "JwtAuthenticationFilter not found"
    exit 1
fi

if [ -f "backend/src/main/java/tw/waterballsa/config/SecurityConfig.java" ]; then
    success "SecurityConfig exists"
else
    error "SecurityConfig not found"
    exit 1
fi

if [ -f "backend/src/main/java/tw/waterballsa/config/CorsConfig.java" ]; then
    success "CorsConfig exists"
else
    error "CorsConfig not found"
    exit 1
fi

echo ""
echo "4. Checking exception handling..."
echo "-----------------------------------"

if [ -f "backend/src/main/java/tw/waterballsa/exception/GlobalExceptionHandler.java" ]; then
    success "GlobalExceptionHandler exists"
else
    error "GlobalExceptionHandler not found"
    exit 1
fi

if [ -f "backend/src/main/java/tw/waterballsa/exception/ValidationException.java" ]; then
    success "Custom exceptions exist"
else
    error "Custom exceptions not found"
    exit 1
fi

echo ""
echo "5. Checking frontend infrastructure..."
echo "---------------------------------------"

if [ -f "frontend/src/lib/api.ts" ]; then
    success "API client exists"
else
    error "API client not found"
    exit 1
fi

if [ -f "frontend/src/lib/storage.ts" ]; then
    success "localStorage helper exists"
else
    error "localStorage helper not found"
    exit 1
fi

if [ -f "frontend/src/hooks/useAuth.ts" ]; then
    success "useAuth hook exists"
else
    error "useAuth hook not found"
    exit 1
fi

if [ -f "frontend/src/app/api/auth/[...nextauth]/route.ts" ]; then
    success "NextAuth.js route exists"
else
    error "NextAuth.js route not found"
    exit 1
fi

echo ""
echo "6. Validating Docker Compose configuration..."
echo "-----------------------------------------------"

# Validate docker-compose.yml syntax
docker compose config > /dev/null 2>&1
if [ $? -eq 0 ]; then
    success "docker-compose.yml syntax is valid"
else
    error "docker-compose.yml has syntax errors"
    exit 1
fi

# Check services defined
services=$(docker compose config --services 2>/dev/null)
expected_services=("db" "redis" "backend" "frontend")

for service in "${expected_services[@]}"; do
    if echo "$services" | grep -q "^${service}$"; then
        success "Service '${service}' is defined"
    else
        error "Service '${service}' is not defined"
        exit 1
    fi
done

echo ""
echo "7. Checking environment variables..."
echo "--------------------------------------"

# Check if critical env vars are set in .env
if grep -q "JWT_SECRET=" .env && ! grep -q "JWT_SECRET=$" .env; then
    success "JWT_SECRET is configured"
else
    warning "JWT_SECRET is not configured (using placeholder)"
fi

if grep -q "APP_ENCRYPTION_KEY=" .env && ! grep -q "APP_ENCRYPTION_KEY=$" .env; then
    success "APP_ENCRYPTION_KEY is configured"
else
    warning "APP_ENCRYPTION_KEY is not configured (using placeholder)"
fi

if grep -q "GOOGLE_CLIENT_ID=YOUR_GOOGLE" .env; then
    warning "GOOGLE_CLIENT_ID is placeholder - OAuth will not work until configured"
else
    success "GOOGLE_CLIENT_ID appears to be configured"
fi

echo ""
echo "======================================"
echo "Validation Summary"
echo "======================================"
echo ""
success "All critical infrastructure files are in place"
success "Docker Compose configuration is valid"
echo ""
echo "Next steps:"
echo "1. Configure OAuth credentials in .env file"
echo "2. Run: docker compose up --build"
echo "3. Wait for services to start (2-3 minutes)"
echo "4. Access frontend at http://localhost:3000"
echo "5. Access backend at http://localhost:8080"
echo ""
echo "To start services:"
echo "  docker compose up --build"
echo ""
echo "To stop services:"
echo "  docker compose down"
echo ""
echo "To view logs:"
echo "  docker compose logs -f"
echo ""
