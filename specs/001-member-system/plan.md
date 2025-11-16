# Implementation Plan: Member System

**Branch**: `001-member-system` | **Date**: 2025-11-14 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-member-system/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

The Member System provides OAuth-based authentication (Google, Facebook), user profile management with gamification integration (level, achievements), third-party account linking (Discord, GitHub), and order/course ownership tracking. This is the foundational p0 feature that enables user identity, session management, and access control for all other platform features.

**Technical Approach**: Monorepo web application with Next.js frontend and Spring Boot backend, OAuth 2.0 PKCE flow for authentication, JWT-based session management with Redis caching, PostgreSQL for persistent storage with indexed foreign keys, and RESTful API contracts.

## Technical Context

**Language/Version**:
- Frontend: TypeScript 5.x with Next.js 14+
- Backend: Java 17+ with Spring Boot 3.x

**Primary Dependencies**:
- Frontend: Next.js, React 18+, TypeScript, Axios (HTTP client), OAuth libraries (next-auth or similar)
- Backend: Spring Boot, Spring Security OAuth2, Spring Data JPA, JWT library (jjwt), PostgreSQL driver
- Caching: Redis (for session storage, user level cache)

**Storage**: PostgreSQL 15+ (user profiles, OAuth links, orders, course ownership)

**Testing**:
- Frontend: Jest, React Testing Library
- Backend: JUnit 5, Mockito, TestContainers (for integration tests)

**Target Platform**:
- Frontend: Web browsers (Chrome, Firefox, Safari, Edge), deployed via Docker
- Backend: Linux server (Docker container), JVM 17+

**Project Type**: Web application (Option 2 from template)

**Performance Goals**:
- OAuth authentication: <30 seconds registration, <15 seconds login
- Profile operations: <1 second save latency
- Order history page: <2 seconds load for 100 orders
- Session management: support 500 concurrent users
- API response times: <100ms for profile operations

**Constraints**:
- OAuth 2.0 PKCE flow required (security standard for SPAs)
- JWT access tokens: 15-minute expiry, 7-day refresh tokens
- Session storage: Redis (not in-memory, for stateless backend)
- Profile edits: localStorage preservation on session expiration
- Concurrent sessions: unlimited (multi-device support)

**Scale/Scope**:
- Expected users: 10,000+ registered users
- Concurrent sessions: 500 simultaneous authenticated users
- Order history: Up to 100 orders per user (paginated at 10/page)
- OAuth providers: 2 primary (Google, Facebook) + 2 linkable (Discord, GitHub)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### I. Gamification-First Architecture
- ✅ **PASS**: User entity includes `level` field (initialized to Lv. 1, read-only from gamification system)
- ✅ **PASS**: Achievement entity supports 突破道館記錄 tracking (read-only from gamification system)
- ⚠️ **DEFERRED**: Full XP calculation and level-up logic will be implemented in future gamification feature (p1)
- **Justification**: Member System provides foundation (user.level, achievements relationship) but doesn't implement XP/level-up logic—that belongs to p1 gamification feature

### II. Test-Driven Development (NON-NEGOTIABLE)
- ✅ **PASS**: Spec provides 38 acceptance scenarios across 4 user stories (all testable)
- ✅ **PASS**: Contract tests required for all OAuth flows (FR-001 to FR-009, FR-030 to FR-039)
- ✅ **PASS**: Integration tests required for session management, profile updates, order tracking
- ✅ **PASS**: 90%+ coverage target for authentication (critical path)
- **Action**: Tests MUST be written before implementation begins (Red-Green-Refactor)

### III. Performance & Scalability Standards
- ✅ **PASS**: OAuth registration <30s, login <15s (spec SC-001, SC-002)
- ✅ **PASS**: Profile updates <1s (spec SC-003, constitution <100ms target exceeded by spec)
- ✅ **PASS**: Order history <2s for 100 orders (spec SC-006)
- ✅ **PASS**: 500 concurrent users (spec SC-008 matches constitution)
- ✅ **PASS**: Redis caching for session management (stateless backend requirement)
- ✅ **PASS**: Database indexing on user_id, order_id foreign keys
- **Action**: Implement performance monitoring from day 1 (response time tracking)

### IV. User Experience Consistency
- ✅ **PASS**: Chinese UI labels throughout spec ("使用 Google 登入", "個人資料", "已綁定", etc.)
- ✅ **PASS**: Level display format "Lv. [number]" (e.g., "Lv. 15")
- ✅ **PASS**: Order status Chinese terms (待付款/已付款/已完成/已取消)
- ✅ **PASS**: Error messages in Chinese with clear guidance
- ✅ **PASS**: Empty state messages defined (spec FR-049, FR-050)
- **Action**: Frontend components MUST use exact Chinese terms from spec

### V. Security & Data Privacy
- ✅ **PASS**: OAuth 2.0 for Google/Facebook authentication (FR-001 to FR-004)
- ⚠️ **NEEDS RESEARCH**: PKCE flow implementation details for Next.js SPA
- ✅ **PASS**: JWT tokens with 15-min access, 7-day refresh (constitution requirement)
- ✅ **PASS**: Role-based access control (FR-052: order owner-only access)
- ✅ **PASS**: Session in Redis (not in-memory, supports horizontal scaling)
- ⚠️ **NEEDS RESEARCH**: PII encryption at rest (email, birthday, location) - AES-256 implementation
- ⚠️ **NEEDS RESEARCH**: CORS configuration for Next.js frontend domain
- **Action**: Security audit required post-implementation

### VI. Code Quality Standards
- ✅ **PASS**: Backend follows Google Java Style Guide (Checkstyle enforcement)
- ✅ **PASS**: Frontend follows Airbnb TypeScript Style Guide (ESLint + Prettier)
- ✅ **PASS**: Database uses snake_case for tables/columns (user, oauth_provider_link, etc.)
- ✅ **PASS**: PR size limit 400 lines (feature can be broken into sub-PRs per user story)
- **Action**: Setup linters and formatters in CI pipeline

### VII. Docker-First Development
- ✅ **PASS**: Frontend: Next.js in Node.js Alpine image with hot-reload
- ✅ **PASS**: Backend: Spring Boot with JDK 17+ Alpine image
- ✅ **PASS**: Database: PostgreSQL 15+ with persistent volume
- ✅ **PASS**: Cache: Redis 7+ for session/user level caching
- ⚠️ **NEEDS RESEARCH**: Docker Compose networking configuration for frontend ↔ backend ↔ database
- ⚠️ **NEEDS RESEARCH**: Environment variable management (.env structure for OAuth secrets)
- **Action**: Create docker-compose.yml with all services

### VIII. Documentation & Knowledge Transfer
- ✅ **PASS**: API contracts will be generated in OpenAPI 3.0 format
- ✅ **PASS**: Data model will document entity relationships and state machines
- ✅ **PASS**: Quickstart.md will provide setup instructions
- **Action**: Document OAuth callback URLs and environment setup

**Constitution Gate Result**: ✅ **PASS with RESEARCH ITEMS**

**Research Required** (Phase 0):
1. OAuth 2.0 PKCE flow implementation for Next.js (next-auth vs custom)
2. PII encryption at rest in PostgreSQL (field-level AES-256)
3. Docker Compose networking and environment variable structure
4. CORS configuration for Next.js + Spring Boot

## Project Structure

### Documentation (this feature)

```text
specs/001-member-system/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (OAuth, encryption, Docker)
├── data-model.md        # Phase 1 output (entities, relationships)
├── quickstart.md        # Phase 1 output (setup instructions)
├── contracts/           # Phase 1 output (OpenAPI specs)
│   ├── auth.yaml        # OAuth and session endpoints
│   ├── profile.yaml     # Profile management endpoints
│   ├── linking.yaml     # Third-party account linking endpoints
│   └── orders.yaml      # Order history and course ownership endpoints
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tw/
│   │   │       └── waterballsa/
│   │   │           ├── config/          # Security, OAuth, Redis config
│   │   │           ├── controller/      # REST endpoints
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── ProfileController.java
│   │   │           │   ├── LinkingController.java
│   │   │           │   └── OrderController.java
│   │   │           ├── service/         # Business logic
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── ProfileService.java
│   │   │           │   ├── LinkingService.java
│   │   │           │   └── OrderService.java
│   │   │           ├── repository/      # JPA repositories
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── OAuthProviderLinkRepository.java
│   │   │           │   ├── ThirdPartyAccountLinkRepository.java
│   │   │           │   └── OrderRepository.java
│   │   │           ├── model/           # JPA entities
│   │   │           │   ├── User.java
│   │   │           │   ├── OAuthProviderLink.java
│   │   │           │   ├── ThirdPartyAccountLink.java
│   │   │           │   ├── Order.java
│   │   │           │   ├── OrderItem.java
│   │   │           │   ├── CourseOwnership.java
│   │   │           │   └── Achievement.java
│   │   │           ├── dto/             # Request/response DTOs
│   │   │           ├── security/        # JWT, OAuth filters
│   │   │           └── exception/       # Custom exceptions
│   │   └── resources/
│   │       ├── application.yml          # Spring Boot config
│   │       └── db/
│   │           └── migration/           # Flyway migrations
│   │               ├── V1__create_users_table.sql
│   │               ├── V2__create_oauth_provider_links.sql
│   │               ├── V3__create_third_party_links.sql
│   │               ├── V4__create_orders_tables.sql
│   │               └── V5__create_indexes.sql
│   └── test/
│       └── java/
│           └── tw/
│               └── waterballsa/
│                   ├── contract/        # API contract tests
│                   ├── integration/     # Integration tests
│                   └── unit/            # Unit tests
├── Dockerfile
└── pom.xml                              # Maven dependencies

frontend/
├── src/
│   ├── app/                             # Next.js 14+ App Router
│   │   ├── (auth)/                      # Auth route group
│   │   │   ├── login/
│   │   │   │   └── page.tsx
│   │   │   └── oauth/
│   │   │       └── callback/
│   │   │           └── page.tsx
│   │   ├── profile/                     # Profile management
│   │   │   └── page.tsx
│   │   ├── linking/                     # Third-party linking
│   │   │   └── page.tsx
│   │   ├── orders/                      # Order history
│   │   │   ├── page.tsx
│   │   │   └── [orderId]/
│   │   │       └── page.tsx
│   │   ├── courses/                     # My courses
│   │   │   └── page.tsx
│   │   └── layout.tsx                   # Root layout
│   ├── components/                      # React components
│   │   ├── auth/
│   │   │   ├── OAuthButton.tsx
│   │   │   └── LogoutButton.tsx
│   │   ├── profile/
│   │   │   ├── ProfileForm.tsx
│   │   │   ├── LevelDisplay.tsx
│   │   │   └── AchievementList.tsx
│   │   ├── linking/
│   │   │   ├── DiscordLink.tsx
│   │   │   └── GitHubLink.tsx
│   │   └── orders/
│   │       ├── OrderList.tsx
│   │       └── OrderDetail.tsx
│   ├── lib/                             # Utilities
│   │   ├── api.ts                       # API client (Axios)
│   │   ├── auth.ts                      # Auth helpers
│   │   └── storage.ts                   # localStorage helpers
│   ├── hooks/                           # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useProfile.ts
│   │   └── useOrders.ts
│   └── types/                           # TypeScript types
│       ├── user.ts
│       ├── order.ts
│       └── api.ts
├── public/
├── tests/
│   ├── integration/
│   └── unit/
├── Dockerfile
├── package.json
├── tsconfig.json
├── next.config.js
└── .env.local.example

docker-compose.yml                       # Orchestrate all services
.env.example                             # Environment template
```

**Structure Decision**: Web application (Option 2) selected because this is a full-stack platform with distinct frontend (Next.js SPA) and backend (Spring Boot REST API) responsibilities. The monorepo structure in `frontend/` and `backend/` directories enables independent development, testing, and deployment while maintaining code proximity for coordinated changes.

## Complexity Tracking

> **No constitution violations requiring justification.** All checks pass or have research items that will be resolved in Phase 0.
