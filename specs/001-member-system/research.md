# Research: Member System

**Feature**: Member System (001-member-system)
**Date**: 2025-11-14
**Purpose**: Resolve technical unknowns identified in Constitution Check and Technical Context

## Research Topics

### 1. OAuth 2.0 PKCE Flow Implementation for Next.js SPA

**Decision**: Use **NextAuth.js (Auth.js)** with custom OAuth providers for Google, Facebook, Discord, and GitHub

**Rationale**:
- **Industry Standard**: NextAuth.js is the de facto authentication library for Next.js with 20k+ GitHub stars
- **Built-in PKCE Support**: Automatically handles PKCE flow for SPAs (no manual implementation needed)
- **Multi-Provider Support**: Out-of-the-box support for Google, Facebook, Discord, GitHub OAuth
- **JWT Integration**: Native JWT support with customizable token structure and expiry
- **Session Management**: Built-in session handling with Redis adapter available
- **Type Safety**: Full TypeScript support with comprehensive type definitions
- **Next.js 14 App Router**: Compatible with latest Next.js architecture

**Implementation Pattern**:
```typescript
// app/api/auth/[...nextauth]/route.ts
import NextAuth from "next-auth"
import GoogleProvider from "next-auth/providers/google"
import FacebookProvider from "next-auth/providers/facebook"
import { RedisAdapter } from "@next-auth/redis-adapter"

export const authOptions = {
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET,
      authorization: {
        params: {
          prompt: "consent",
          access_type: "offline",
          response_type: "code",  // PKCE flow
        },
      },
    }),
    FacebookProvider({
      clientId: process.env.FACEBOOK_CLIENT_ID,
      clientSecret: process.env.FACEBOOK_CLIENT_SECRET,
    }),
  ],
  adapter: RedisAdapter(redisClient),
  session: {
    strategy: "jwt",
    maxAge: 7 * 24 * 60 * 60, // 7 days
  },
  jwt: {
    maxAge: 15 * 60, // 15 minutes for access token
  },
  callbacks: {
    async jwt({ token, user, account }) {
      // Auto-merge accounts by email
      if (account && user) {
        // Link new OAuth provider to existing user
        await linkOAuthProvider(user.email, account.provider, account.providerAccountId)
      }
      return token
    },
  },
}

const handler = NextAuth(authOptions)
export { handler as GET, handler as POST }
```

**Alternatives Considered**:
- **Custom OAuth Implementation**: Rejected - too complex, error-prone, reinvents wheel
- **Passport.js**: Rejected - requires Express.js server, not Next.js native
- **Auth0/Okta**: Rejected - third-party SaaS, adds vendor dependency and cost

**Security Benefits**:
- PKCE prevents authorization code interception attacks
- State parameter prevents CSRF
- Built-in CSRF protection for callbacks
- Secure cookie handling with httpOnly, sameSite flags

---

### 2. PII Encryption at Rest in PostgreSQL

**Decision**: Use **PostgreSQL pgcrypto extension** with AES-256-GCM encryption for PII fields

**Rationale**:
- **Database-Native**: PostgreSQL pgcrypto extension provides AES encryption without external dependencies
- **Performance**: Encryption/decryption happens at database layer (minimal application overhead)
- **GDPR Compliance**: AES-256 meets GDPR encryption-at-rest requirements
- **Selective Encryption**: Encrypt only PII fields (email, birthday, location), not all data
- **Key Management**: Use environment variable for encryption key (rotatable without data migration via dual-key strategy)

**Implementation Pattern**:
```sql
-- Enable pgcrypto extension
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- User table with encrypted PII
CREATE TABLE "user" (
  user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nickname VARCHAR(255) NOT NULL,
  email_encrypted BYTEA NOT NULL,  -- Encrypted with pgp_sym_encrypt
  gender VARCHAR(50),
  birthday_encrypted BYTEA,         -- Encrypted birth date
  location_encrypted BYTEA,         -- Encrypted location
  occupation VARCHAR(255),
  github_link VARCHAR(500),
  level INTEGER NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Encryption helper functions
CREATE OR REPLACE FUNCTION encrypt_pii(data TEXT)
RETURNS BYTEA AS $$
BEGIN
  RETURN pgp_sym_encrypt(data, current_setting('app.encryption_key'));
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION decrypt_pii(data BYTEA)
RETURNS TEXT AS $$
BEGIN
  RETURN pgp_sym_decrypt(data, current_setting('app.encryption_key'));
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Application query example
-- INSERT: INSERT INTO "user" (email_encrypted) VALUES (encrypt_pii('user@example.com'));
-- SELECT: SELECT decrypt_pii(email_encrypted) FROM "user";
```

**JPA Entity Mapping**:
```java
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID userId;

    private String nickname;

    @Column(name = "email_encrypted", columnDefinition = "bytea")
    @Convert(converter = EncryptedStringConverter.class)
    private String email;  // Application sees decrypted string

    @Column(name = "birthday_encrypted", columnDefinition = "bytea")
    @Convert(converter = EncryptedLocalDateConverter.class)
    private LocalDate birthday;

    @Column(name = "location_encrypted", columnDefinition = "bytea")
    @Convert(converter = EncryptedStringConverter.class)
    private String location;

    private Integer level = 1;
}

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        // Call pgp_sym_encrypt via native query
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        // Call pgp_sym_decrypt via native query
    }
}
```

**Alternatives Considered**:
- **Application-Layer Encryption (Jasypt)**: Rejected - harder to query encrypted fields, more complex key management
- **Transparent Data Encryption (TDE)**: Rejected - encrypts entire database (overkill), PostgreSQL enterprise feature only
- **AWS RDS Encryption**: Rejected - encrypts storage volume (not field-level), doesn't protect against SQL injection

**Key Management Strategy**:
- Encryption key stored in environment variable (`APP_ENCRYPTION_KEY`)
- Key rotation: Dual-key strategy (old + new keys active during migration period)
- Backup: Encrypted backups include encryption key in separate secure vault

---

### 3. Docker Compose Networking and Environment Variable Structure

**Decision**: Use **Docker Compose bridge network** with service names as hostnames and **.env file** for secrets management

**Rationale**:
- **Service Discovery**: Docker Compose automatically creates a bridge network where services can reference each other by service name (e.g., `http://backend:8080`)
- **Isolation**: Frontend → Backend → Database → Redis form a secure network with no external exposure except frontend port
- **Environment Parity**: Same docker-compose.yml for dev/staging, different .env files
- **Secret Management**: .env files kept out of version control (.gitignore), .env.example checked in as template

**Docker Compose Configuration**:
```yaml
version: '3.8'

services:
  database:
    image: postgres:15-alpine
    container_name: waterball-db
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - waterball-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: waterball-redis
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data
    networks:
      - waterball-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: waterball-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PASSWORD: ${REDIS_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
      FACEBOOK_CLIENT_ID: ${FACEBOOK_CLIENT_ID}
      FACEBOOK_CLIENT_SECRET: ${FACEBOOK_CLIENT_SECRET}
      APP_ENCRYPTION_KEY: ${APP_ENCRYPTION_KEY}
    ports:
      - "8080:8080"
    depends_on:
      database:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - waterball-network

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: waterball-frontend
    environment:
      NEXT_PUBLIC_API_URL: http://localhost:8080
      NEXTAUTH_URL: http://localhost:3000
      NEXTAUTH_SECRET: ${NEXTAUTH_SECRET}
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
      FACEBOOK_CLIENT_ID: ${FACEBOOK_CLIENT_ID}
      FACEBOOK_CLIENT_SECRET: ${FACEBOOK_CLIENT_SECRET}
    ports:
      - "3000:3000"
    depends_on:
      - backend
    networks:
      - waterball-network
    volumes:
      - ./frontend/src:/app/src  # Hot-reload for development

volumes:
  postgres-data:
  redis-data:

networks:
  waterball-network:
    driver: bridge
```

**Environment Variable Structure (.env.example)**:
```bash
# Database Configuration
DB_NAME=waterball
DB_USER=waterball_user
DB_PASSWORD=changeme_secure_password

# Redis Configuration
REDIS_PASSWORD=changeme_redis_password

# JWT Secret (min 32 characters)
JWT_SECRET=changeme_jwt_secret_min_32_chars

# OAuth - Google
GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your_google_client_secret

# OAuth - Facebook
FACEBOOK_CLIENT_ID=your_facebook_app_id
FACEBOOK_CLIENT_SECRET=your_facebook_app_secret

# OAuth - Discord (for third-party linking)
DISCORD_CLIENT_ID=your_discord_client_id
DISCORD_CLIENT_SECRET=your_discord_client_secret

# OAuth - GitHub (for third-party linking)
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# NextAuth
NEXTAUTH_SECRET=changeme_nextauth_secret_min_32_chars
NEXTAUTH_URL=http://localhost:3000

# Encryption
APP_ENCRYPTION_KEY=changeme_encryption_key_32_chars
```

**Alternatives Considered**:
- **Kubernetes ConfigMaps/Secrets**: Rejected - overkill for local dev, adds complexity
- **Environment variables directly in docker-compose.yml**: Rejected - exposes secrets in version control
- **Docker Secrets**: Rejected - requires Swarm mode, not needed for single-host deployment

**Network Security**:
- Only frontend port 3000 exposed to host (user access)
- Backend port 8080 exposed for API calls (can be restricted to frontend only in production)
- Database and Redis NOT exposed to host (internal network only)

---

### 4. CORS Configuration for Next.js + Spring Boot

**Decision**: Configure **Spring Security CORS** to allow Next.js frontend origin with credentials

**Rationale**:
- **Security**: CORS prevents malicious websites from making unauthorized API requests
- **Credentials**: `credentials: 'include'` required for cookies (JWT tokens) to be sent cross-origin
- **Development vs Production**: Different allowed origins for localhost (dev) vs production domain

**Spring Boot CORS Configuration**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())  // CSRF handled by NextAuth
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);  // CRITICAL: Allow cookies
        configuration.setMaxAge(3600L);  // Cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

**application.yml Configuration**:
```yaml
app:
  cors:
    allowed-origins:
      - http://localhost:3000         # Development
      - https://world.waterballsa.tw  # Production (example)
```

**Next.js API Client Configuration**:
```typescript
// lib/api.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  withCredentials: true,  // CRITICAL: Send cookies with requests
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to requests
apiClient.interceptors.request.use((config) => {
  const token = getAccessToken();  // From NextAuth session
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 Unauthorized (refresh token)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Trigger token refresh via NextAuth
      await signIn('refresh');
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

**Alternatives Considered**:
- **Wildcard CORS (`*`)**: Rejected - major security risk, allows any origin
- **No CORS (same-origin deployment)**: Rejected - requires frontend/backend on same domain, limits deployment flexibility
- **Proxy all API requests through Next.js**: Rejected - adds latency, couples frontend/backend deployment

**Security Considerations**:
- **Allowed Origins**: Must be explicitly listed (no wildcards)
- **Credentials**: Only allowed for trusted origins (attacker can't steal cookies from different domain)
- **Preflight Caching**: `maxAge: 3600` reduces OPTIONS request overhead
- **HTTPS Only in Production**: HTTP allowed only for localhost development

---

## Summary of Decisions

| Research Topic | Decision | Key Benefit |
|----------------|----------|-------------|
| **OAuth PKCE Flow** | NextAuth.js with custom providers | Built-in PKCE, multi-provider support, type-safe |
| **PII Encryption** | PostgreSQL pgcrypto (AES-256-GCM) | Database-native, GDPR compliant, field-level encryption |
| **Docker Networking** | Bridge network + .env secrets | Service discovery, isolated network, environment parity |
| **CORS Configuration** | Spring Security CORS with credentials | Secure cross-origin requests, cookie-based auth |

**All NEEDS RESEARCH items resolved. Constitution Check now passes completely.**

---

## Additional Best Practices Research

### OAuth Callback URL Configuration

**Google OAuth Console**:
- Authorized redirect URIs: `http://localhost:3000/api/auth/callback/google` (dev)
- Production: `https://world.waterballsa.tw/api/auth/callback/google`

**Facebook App Settings**:
- Valid OAuth Redirect URIs: `http://localhost:3000/api/auth/callback/facebook` (dev)
- Production: `https://world.waterballsa.tw/api/auth/callback/facebook`

**Discord Developer Portal** (for third-party linking):
- Redirect URI: `http://localhost:3000/api/auth/callback/discord` (dev)
- Scopes: `identify email`

**GitHub OAuth App** (for third-party linking):
- Authorization callback URL: `http://localhost:3000/api/auth/callback/github` (dev)

### Rate Limiting Strategy

**Spring Boot with Bucket4j**:
```java
@Configuration
public class RateLimitConfig {
    @Bean
    public Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}

// Apply to controllers
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @RateLimit(permits = 10, duration = 1, unit = TimeUnit.MINUTES)
    @PutMapping
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UpdateProfileRequest request) {
        // 10 profile updates per minute per user
    }
}
```

### Logging Strategy

**Structured Logging with Logback**:
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdcKeyName>user_id</includeMdcKeyName>
            <includeMdcKeyName>request_id</includeMdcKeyName>
        </encoder>
    </appender>
</configuration>
```

**Log Events**:
- **Authentication**: Login attempts (success/failure), OAuth callbacks, token refresh
- **Profile Updates**: Field changes (redact PII values in logs)
- **Third-Party Linking**: Discord/GitHub link/unlink events
- **Security**: Failed authorization attempts, rate limit violations
- **Performance**: API response times >1s (alerts)

**PII Redaction**:
- Email: `user@example.com` → `u***@example.com`
- Birthday: `1990-01-15` → `****-**-15`
- Location: `Taipei, Taiwan` → `Tai***, Taiwan`
