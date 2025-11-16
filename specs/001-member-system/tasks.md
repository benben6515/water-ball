# Tasks: Member System

**Feature Branch**: `001-member-system`
**Input**: Design documents from `/specs/001-member-system/`
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Tests**: TDD is MANDATORY per constitution. All test tasks MUST be completed BEFORE implementation tasks.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Web app structure**: `backend/src/`, `frontend/src/`
- Backend: `backend/src/main/java/tw/waterballsa/`
- Frontend: `frontend/src/`
- Migrations: `backend/src/main/resources/db/migration/`
- Tests: `backend/src/test/java/tw/waterballsa/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Create monorepo directory structure: backend/, frontend/, docker-compose.yml at root
- [X] T002 Initialize backend Spring Boot project with Maven in backend/pom.xml
- [X] T003 [P] Initialize frontend Next.js project with TypeScript in frontend/package.json
- [X] T004 [P] Create .env.example at repository root with OAuth credentials template
- [X] T005 [P] Configure ESLint and Prettier for frontend in frontend/.eslintrc.js
- [X] T006 [P] Configure Checkstyle for backend in backend/checkstyle.xml
- [X] T007 Create backend Dockerfile in backend/Dockerfile (JDK 17 Alpine, hot-reload)
- [X] T008 [P] Create frontend Dockerfile in frontend/Dockerfile (Node Alpine, hot-reload)
- [X] T009 Create docker-compose.yml with services: db (PostgreSQL 15+), redis (Redis 7+), backend, frontend

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Database & Caching Setup

- [X] T010 Enable pgcrypto extension in backend/src/main/resources/db/migration/V1__enable_pgcrypto.sql
- [X] T011 [P] Configure Flyway in backend/src/main/resources/application.yml
- [X] T012 [P] Configure Redis connection in backend/src/main/java/tw/waterballsa/config/RedisConfig.java
- [X] T013 [P] Create encryption helper functions in backend/src/main/resources/db/migration/V2__encryption_functions.sql

### Security & Authentication Framework

- [X] T014 Add Spring Security OAuth2 dependencies to backend/pom.xml (spring-boot-starter-oauth2-client, jjwt)
- [X] T015 [P] Configure CORS in backend/src/main/java/tw/waterballsa/config/CorsConfig.java (allow frontend origin, credentials: true)
- [X] T016 [P] Implement JWT utility class in backend/src/main/java/tw/waterballsa/security/JwtTokenProvider.java (generate, validate, extract claims)
- [X] T017 [P] Create JWT authentication filter in backend/src/main/java/tw/waterballsa/security/JwtAuthenticationFilter.java
- [X] T018 Configure Spring Security filter chain in backend/src/main/java/tw/waterballsa/config/SecurityConfig.java (stateless session, JWT filter, OAuth2)
- [X] T019 [P] Configure NextAuth.js in frontend/src/app/api/auth/[...nextauth]/route.ts (Google, Facebook providers, JWT strategy, Redis adapter)

### Base Infrastructure

- [X] T020 [P] Create global exception handler in backend/src/main/java/tw/waterballsa/exception/GlobalExceptionHandler.java (Chinese error messages)
- [X] T021 [P] Create custom exceptions in backend/src/main/java/tw/waterballsa/exception/ (ValidationException, UnauthorizedException, ForbiddenException, ResourceNotFoundException)
- [X] T022 [P] Create base DTOs in backend/src/main/java/tw/waterballsa/dto/ (ErrorResponse, PaginationResponse)
- [X] T023 [P] Create API client utility in frontend/src/lib/api.ts (Axios instance with JWT interceptor)
- [X] T024 [P] Create localStorage helper in frontend/src/lib/storage.ts (save/restore profile edits on session expiry)
- [X] T025 [P] Create custom useAuth hook in frontend/src/hooks/useAuth.ts (session management, logout)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - OAuth Registration and Login (Priority: P1) 🎯 MVP

**Goal**: Enable users to register and login using Google or Facebook OAuth 2.0, with automatic account merging by email

**Independent Test**: Register with Google, logout, login with Google. Register with Facebook using same email, verify both providers linked to same account.

### Tests for User Story 1 (RED Phase - Write FIRST)

> **CRITICAL**: Write these tests FIRST, ensure they FAIL before implementation

- [ ] T026 [P] [US1] Contract test for GET /auth/oauth/{provider}/authorize in backend/src/test/java/tw/waterballsa/contract/AuthControllerTest.java (test redirect to OAuth provider)
- [ ] T027 [P] [US1] Contract test for GET /auth/oauth/{provider}/callback in backend/src/test/java/tw/waterballsa/contract/AuthControllerTest.java (test account creation, login, auto-merge)
- [ ] T028 [P] [US1] Contract test for GET /auth/session in backend/src/test/java/tw/waterballsa/contract/AuthControllerTest.java (test authenticated session info)
- [ ] T029 [P] [US1] Contract test for POST /auth/refresh in backend/src/test/java/tw/waterballsa/contract/AuthControllerTest.java (test token refresh)
- [ ] T030 [P] [US1] Contract test for POST /auth/logout in backend/src/test/java/tw/waterballsa/contract/AuthControllerTest.java (test session termination)
- [ ] T031 [P] [US1] Integration test for OAuth registration flow in backend/src/test/java/tw/waterballsa/integration/OAuthRegistrationTest.java (mock OAuth provider, verify user creation)
- [ ] T032 [P] [US1] Integration test for OAuth login flow in backend/src/test/java/tw/waterballsa/integration/OAuthLoginTest.java (existing user, verify session)
- [ ] T033 [P] [US1] Integration test for account auto-merge in backend/src/test/java/tw/waterballsa/integration/AccountMergeTest.java (same email, different providers)
- [ ] T034 [P] [US1] Integration test for concurrent sessions in backend/src/test/java/tw/waterballsa/integration/ConcurrentSessionTest.java (login from multiple devices)

### Database Schema for User Story 1 (GREEN Phase)

- [ ] T035 [P] [US1] Create users table migration in backend/src/main/resources/db/migration/V3__create_users_table.sql (user_id, nickname, email_encrypted, gender, birthday_encrypted, location_encrypted, occupation, github_link, level default 1, created_at, updated_at)
- [ ] T036 [P] [US1] Create oauth_provider_link table migration in backend/src/main/resources/db/migration/V4__create_oauth_provider_links.sql (provider_link_id, user_id FK, provider_type, provider_user_id, provider_email, linked_at)
- [ ] T037 [P] [US1] Create indexes for users in backend/src/main/resources/db/migration/V5__create_user_indexes.sql (idx_user_email_encrypted, idx_user_level, idx_user_created_at)
- [ ] T038 [P] [US1] Create indexes for oauth_provider_link in backend/src/main/resources/db/migration/V6__create_oauth_indexes.sql (idx_oauth_user_id, unique idx_oauth_provider_type_uid, idx_oauth_provider_email)

### Backend Models for User Story 1 (GREEN Phase)

- [ ] T039 [P] [US1] Create User entity in backend/src/main/java/tw/waterballsa/model/User.java (JPA entity with encrypted fields AttributeConverter for email, birthday, location)
- [ ] T040 [P] [US1] Create OAuthProviderLink entity in backend/src/main/java/tw/waterballsa/model/OAuthProviderLink.java (JPA entity with @ManyToOne User)
- [ ] T041 [P] [US1] Create PII encryption AttributeConverter in backend/src/main/java/tw/waterballsa/security/EncryptionConverter.java (pgcrypto integration)

### Backend Repositories for User Story 1 (GREEN Phase)

- [ ] T042 [P] [US1] Create UserRepository in backend/src/main/java/tw/waterballsa/repository/UserRepository.java (findByEmail with decryption, save)
- [ ] T043 [P] [US1] Create OAuthProviderLinkRepository in backend/src/main/java/tw/waterballsa/repository/OAuthProviderLinkRepository.java (findByProviderTypeAndProviderUserId, findByUserId, save)

### Backend DTOs for User Story 1 (GREEN Phase)

- [ ] T044 [P] [US1] Create SessionInfoResponse DTO in backend/src/main/java/tw/waterballsa/dto/SessionInfoResponse.java (user_id, nickname, email, level, oauth_providers list)
- [ ] T045 [P] [US1] Create RefreshTokenRequest DTO in backend/src/main/java/tw/waterballsa/dto/RefreshTokenRequest.java (refresh_token field)
- [ ] T046 [P] [US1] Create TokenResponse DTO in backend/src/main/java/tw/waterballsa/dto/TokenResponse.java (access_token, token_type, expires_in)

### Backend Service for User Story 1 (GREEN Phase)

- [ ] T047 [US1] Implement AuthService in backend/src/main/java/tw/waterballsa/service/AuthService.java (handleOAuthCallback: check existing user by email, create or link provider, generate JWT, store session in Redis)
- [ ] T048 [US1] Implement OAuth callback logic in AuthService: auto-merge accounts when email matches existing user
- [ ] T049 [US1] Implement session management in AuthService: getSession (validate JWT, query Redis), refreshToken (validate refresh token, issue new access token), logout (invalidate Redis session)
- [ ] T050 [US1] Implement nickname extraction logic in AuthService: extract from OAuth name field, fallback to email prefix

### Backend Controller for User Story 1 (GREEN Phase)

- [ ] T051 [US1] Implement AuthController in backend/src/main/java/tw/waterballsa/controller/AuthController.java (5 endpoints from contracts/auth.yaml)
- [ ] T052 [US1] Implement GET /auth/oauth/{provider}/authorize endpoint: validate provider, redirect to OAuth consent (Google/Facebook)
- [ ] T053 [US1] Implement GET /auth/oauth/{provider}/callback endpoint: exchange code for token, call AuthService.handleOAuthCallback, set refresh_token cookie, redirect to frontend with access token
- [ ] T054 [US1] Implement GET /auth/session endpoint: validate JWT, return SessionInfoResponse from Redis or DB
- [ ] T055 [US1] Implement POST /auth/refresh endpoint: validate refresh_token, return new TokenResponse
- [ ] T056 [US1] Implement POST /auth/logout endpoint: invalidate Redis session, clear cookies, return 204

### Frontend Types for User Story 1 (GREEN Phase)

- [ ] T057 [P] [US1] Create User type in frontend/src/types/user.ts (user_id, nickname, email, level, oauth_providers)
- [ ] T058 [P] [US1] Create API response types in frontend/src/types/api.ts (SessionInfoResponse, TokenResponse, ErrorResponse)

### Frontend Components for User Story 1 (GREEN Phase)

- [ ] T059 [P] [US1] Create OAuthButton component in frontend/src/components/auth/OAuthButton.tsx (props: provider, label "使用 Google 登入"/"使用 Facebook 登入", onClick redirect to backend OAuth authorize)
- [ ] T060 [P] [US1] Create LogoutButton component in frontend/src/components/auth/LogoutButton.tsx (call /auth/logout, redirect to login page)

### Frontend Pages for User Story 1 (GREEN Phase)

- [ ] T061 [US1] Create login page in frontend/src/app/(auth)/login/page.tsx (render OAuthButtons for Google and Facebook)
- [ ] T062 [US1] Create OAuth callback page in frontend/src/app/(auth)/oauth/callback/page.tsx (extract token from URL, store in localStorage, fetch session, redirect to /profile)
- [ ] T063 [US1] Implement session expiration redirect in frontend/src/hooks/useAuth.ts (detect 401, save form data to localStorage, redirect to login)
- [ ] T064 [US1] Create protected route wrapper in frontend/src/app/layout.tsx (check authentication, redirect unauthenticated users to login)

### Refactor Phase

- [ ] T065 [US1] Refactor AuthService: extract OAuth provider validation to separate method
- [ ] T066 [US1] Refactor AuthController: consolidate error handling for Chinese error messages
- [ ] T067 [US1] Add logging for OAuth flows in AuthService (registration, login, merge events)

**Checkpoint**: User Story 1 complete. Users can register/login with Google/Facebook, accounts auto-merge by email, concurrent sessions work.

---

## Phase 4: User Story 2 - User Profile Management (Priority: P2)

**Goal**: Allow users to view and update their profile (nickname, gender, birthday, location, occupation, github_link), view level and achievements

**Independent Test**: Login, navigate to /profile, view current profile, update nickname and github_link, save, refresh page to verify persistence. Verify level displays as "Lv. 1".

### Tests for User Story 2 (RED Phase)

- [ ] T068 [P] [US2] Contract test for GET /profile in backend/src/test/java/tw/waterballsa/contract/ProfileControllerTest.java (test authenticated profile retrieval)
- [ ] T069 [P] [US2] Contract test for PUT /profile in backend/src/test/java/tw/waterballsa/contract/ProfileControllerTest.java (test profile update, validation errors)
- [ ] T070 [P] [US2] Integration test for profile update in backend/src/test/java/tw/waterballsa/integration/ProfileUpdateTest.java (update all fields, verify persistence)
- [ ] T071 [P] [US2] Integration test for GitHub URL validation in backend/src/test/java/tw/waterballsa/integration/ProfileValidationTest.java (invalid URL returns 400 with Chinese error)
- [ ] T072 [P] [US2] Integration test for localStorage preservation in frontend/tests/integration/profile-session-expiry.test.tsx (session expires during edit, login restores data)

### Database Schema for User Story 2 (GREEN Phase)

- [ ] T073 [US2] Create achievement table migration in backend/src/main/resources/db/migration/V7__create_achievements_table.sql (achievement_id, user_id FK, achievement_type, achievement_name, earned_at)
- [ ] T074 [P] [US2] Create indexes for achievements in backend/src/main/resources/db/migration/V8__create_achievement_indexes.sql (idx_achievement_user_id, idx_achievement_type, idx_achievement_earned_at)

### Backend Models for User Story 2 (GREEN Phase)

- [ ] T075 [P] [US2] Create Achievement entity in backend/src/main/java/tw/waterballsa/model/Achievement.java (JPA entity with @ManyToOne User, read-only)

### Backend Repositories for User Story 2 (GREEN Phase)

- [ ] T076 [P] [US2] Create AchievementRepository in backend/src/main/java/tw/waterballsa/repository/AchievementRepository.java (findByUserIdOrderByEarnedAtDesc)

### Backend DTOs for User Story 2 (GREEN Phase)

- [ ] T077 [P] [US2] Create UserProfileResponse DTO in backend/src/main/java/tw/waterballsa/dto/UserProfileResponse.java (user_id, nickname, email, gender, birthday, location, occupation, github_link, level, achievements list)
- [ ] T078 [P] [US2] Create UpdateProfileRequest DTO in backend/src/main/java/tw/waterballsa/dto/UpdateProfileRequest.java (nickname required, gender, birthday, location, occupation, github_link optional)
- [ ] T079 [P] [US2] Create AchievementDTO in backend/src/main/java/tw/waterballsa/dto/AchievementDTO.java (achievement_id, achievement_type, achievement_name, earned_at)

### Backend Service for User Story 2 (GREEN Phase)

- [ ] T080 [US2] Implement ProfileService in backend/src/main/java/tw/waterballsa/service/ProfileService.java (getProfile: fetch user with achievements, decrypt PII fields)
- [ ] T081 [US2] Implement updateProfile in ProfileService: validate fields (nickname required, github_link URL format, gender enum), encrypt PII fields, persist, return updated profile
- [ ] T082 [US2] Implement GitHub URL validation in ProfileService: regex pattern ^https://github\.com/[a-zA-Z0-9\-]+$, throw ValidationException with Chinese message if invalid

### Backend Controller for User Story 2 (GREEN Phase)

- [ ] T083 [US2] Implement ProfileController in backend/src/main/java/tw/waterballsa/controller/ProfileController.java (2 endpoints from contracts/profile.yaml)
- [ ] T084 [US2] Implement GET /profile endpoint: validate JWT, call ProfileService.getProfile, return UserProfileResponse
- [ ] T085 [US2] Implement PUT /profile endpoint: validate JWT, call ProfileService.updateProfile, return updated UserProfileResponse with confirmation

### Frontend Types for User Story 2 (GREEN Phase)

- [ ] T086 [P] [US2] Create Profile types in frontend/src/types/user.ts (UserProfile, Achievement, UpdateProfileRequest)

### Frontend Components for User Story 2 (GREEN Phase)

- [ ] T087 [P] [US2] Create ProfileForm component in frontend/src/components/profile/ProfileForm.tsx (form fields for nickname, gender dropdown, birthday date picker, location, occupation, github_link with validation)
- [ ] T088 [P] [US2] Create LevelDisplay component in frontend/src/components/profile/LevelDisplay.tsx (display "Lv. [number]" format, read-only)
- [ ] T089 [P] [US2] Create AchievementList component in frontend/src/components/profile/AchievementList.tsx (list achievements with earned_at, empty state "尚無成就")

### Frontend Hooks for User Story 2 (GREEN Phase)

- [ ] T090 [P] [US2] Create useProfile hook in frontend/src/hooks/useProfile.ts (fetchProfile, updateProfile, handle 401 redirect with localStorage preservation)

### Frontend Pages for User Story 2 (GREEN Phase)

- [ ] T091 [US2] Create profile page in frontend/src/app/profile/page.tsx (fetch profile with useProfile, render LevelDisplay, AchievementList, ProfileForm)
- [ ] T092 [US2] Implement profile form submission: call updateProfile, display success message "個人資料已更新", handle validation errors in Chinese
- [ ] T093 [US2] Implement localStorage preservation in frontend/src/lib/storage.ts: save unsaved edits on session expiry, restore on re-authentication

### Refactor Phase

- [ ] T094 [US2] Refactor ProfileService: extract validation logic to separate validator class
- [ ] T095 [US2] Add performance logging for profile updates (<1s requirement)

**Checkpoint**: User Story 2 complete. Users can view/update profile, see level and achievements, form data preserved on session expiry.

---

## Phase 5: User Story 3 - Third-Party Account Linking (Priority: P3)

**Goal**: Allow users to link/unlink Discord and GitHub accounts via OAuth, with duplicate prevention

**Independent Test**: Login, navigate to /linking, link Discord account (complete OAuth), verify status shows "已綁定" with username, unlink Discord, verify status shows "未綁定".

### Tests for User Story 3 (RED Phase)

- [ ] T096 [P] [US3] Contract test for GET /linking/status in backend/src/test/java/tw/waterballsa/contract/LinkingControllerTest.java (test linked/unlinked status)
- [ ] T097 [P] [US3] Contract test for GET /linking/{platform}/authorize in backend/src/test/java/tw/waterballsa/contract/LinkingControllerTest.java (test redirect to Discord/GitHub OAuth)
- [ ] T098 [P] [US3] Contract test for GET /linking/{platform}/callback in backend/src/test/java/tw/waterballsa/contract/LinkingControllerTest.java (test account linking, duplicate prevention)
- [ ] T099 [P] [US3] Contract test for DELETE /linking/{platform} in backend/src/test/java/tw/waterballsa/contract/LinkingControllerTest.java (test unlinking)
- [ ] T100 [P] [US3] Integration test for Discord linking flow in backend/src/test/java/tw/waterballsa/integration/DiscordLinkingTest.java (mock Discord OAuth, verify link creation)
- [ ] T101 [P] [US3] Integration test for duplicate prevention in backend/src/test/java/tw/waterballsa/integration/DuplicateLinkTest.java (link Discord to User A, attempt link to User B, expect 409 error)

### Database Schema for User Story 3 (GREEN Phase)

- [ ] T102 [US3] Create third_party_account_link table migration in backend/src/main/resources/db/migration/V9__create_third_party_links.sql (link_id, user_id FK, platform_type, platform_user_id, platform_username, linked_at)
- [ ] T103 [P] [US3] Create indexes for third_party_account_link in backend/src/main/resources/db/migration/V10__create_thirdparty_indexes.sql (idx_thirdparty_user_id, unique idx_thirdparty_platform_type_uid, unique idx_thirdparty_user_platform)

### Backend Models for User Story 3 (GREEN Phase)

- [ ] T104 [P] [US3] Create ThirdPartyAccountLink entity in backend/src/main/java/tw/waterballsa/model/ThirdPartyAccountLink.java (JPA entity with @ManyToOne User)

### Backend Repositories for User Story 3 (GREEN Phase)

- [ ] T105 [P] [US3] Create ThirdPartyAccountLinkRepository in backend/src/main/java/tw/waterballsa/repository/ThirdPartyAccountLinkRepository.java (findByUserId, findByPlatformTypeAndPlatformUserId, deleteByUserIdAndPlatformType)

### Backend DTOs for User Story 3 (GREEN Phase)

- [ ] T106 [P] [US3] Create LinkingStatusResponse DTO in backend/src/main/java/tw/waterballsa/dto/LinkingStatusResponse.java (discord: PlatformLink, github: PlatformLink)
- [ ] T107 [P] [US3] Create PlatformLink DTO in backend/src/main/java/tw/waterballsa/dto/PlatformLink.java (linked: boolean, username: string nullable, linked_at: timestamp nullable)

### Backend Service for User Story 3 (GREEN Phase)

- [ ] T108 [US3] Implement LinkingService in backend/src/main/java/tw/waterballsa/service/LinkingService.java (getLinkingStatus: fetch Discord/GitHub links for user, return status)
- [ ] T109 [US3] Implement initiateLinking in LinkingService: validate platform (discord/github), check not already linked, redirect to OAuth authorize
- [ ] T110 [US3] Implement linkingCallback in LinkingService: exchange OAuth code for token, fetch platform user info, check duplicate (throw 409 if already linked to another user), create ThirdPartyAccountLink
- [ ] T111 [US3] Implement unlinkAccount in LinkingService: validate platform linked, delete ThirdPartyAccountLink, return 204

### Backend Controller for User Story 3 (GREEN Phase)

- [ ] T112 [US3] Implement LinkingController in backend/src/main/java/tw/waterballsa/controller/LinkingController.java (4 endpoints from contracts/linking.yaml)
- [ ] T113 [US3] Implement GET /linking/status endpoint: validate JWT, call LinkingService.getLinkingStatus, return LinkingStatusResponse
- [ ] T114 [US3] Implement GET /linking/{platform}/authorize endpoint: validate JWT and platform, call LinkingService.initiateLinking, return 302 redirect
- [ ] T115 [US3] Implement GET /linking/{platform}/callback endpoint: validate JWT, state, code, call LinkingService.linkingCallback, redirect to frontend with status
- [ ] T116 [US3] Implement DELETE /linking/{platform} endpoint: validate JWT, call LinkingService.unlinkAccount, return 204

### Frontend Types for User Story 3 (GREEN Phase)

- [ ] T117 [P] [US3] Create Linking types in frontend/src/types/user.ts (LinkingStatusResponse, PlatformLink)

### Frontend Components for User Story 3 (GREEN Phase)

- [ ] T118 [P] [US3] Create DiscordLink component in frontend/src/components/linking/DiscordLink.tsx (show status "已綁定"/"未綁定", link/unlink buttons, display username if linked)
- [ ] T119 [P] [US3] Create GitHubLink component in frontend/src/components/linking/GitHubLink.tsx (show status, link/unlink buttons, display username if linked)

### Frontend Hooks for User Story 3 (GREEN Phase)

- [ ] T120 [P] [US3] Create useLinking hook in frontend/src/hooks/useLinking.ts (fetchLinkingStatus, initiateLink, unlinkAccount)

### Frontend Pages for User Story 3 (GREEN Phase)

- [ ] T121 [US3] Create linking page in frontend/src/app/linking/page.tsx (fetch linking status, render DiscordLink and GitHubLink components)
- [ ] T122 [US3] Implement link button onClick: call /linking/{platform}/authorize, redirect to OAuth
- [ ] T123 [US3] Implement unlink button onClick: show confirmation dialog "確定要解除綁定嗎?", call DELETE /linking/{platform}, refresh status
- [ ] T124 [US3] Handle duplicate error: display Chinese error message "此 Discord 帳號已被其他用戶綁定"

### Refactor Phase

- [ ] T125 [US3] Refactor LinkingService: extract OAuth client logic to separate provider classes (DiscordOAuthClient, GitHubOAuthClient)

**Checkpoint**: User Story 3 complete. Users can link/unlink Discord and GitHub accounts, duplicate prevention works.

---

## Phase 6: User Story 4 - Order History and Course Ownership Tracking (Priority: P2)

**Goal**: Allow users to view order history (paginated), order details, and owned courses list

**Independent Test**: Login with account that has purchase history, navigate to /orders, verify orders displayed sorted by date, click order to see details, navigate to /courses/owned to see owned courses.

### Tests for User Story 4 (RED Phase)

- [ ] T126 [P] [US4] Contract test for GET /orders in backend/src/test/java/tw/waterballsa/contract/OrderControllerTest.java (test paginated order list, empty state)
- [ ] T127 [P] [US4] Contract test for GET /orders/{orderId} in backend/src/test/java/tw/waterballsa/contract/OrderControllerTest.java (test order details, authorization check)
- [ ] T128 [P] [US4] Contract test for GET /courses/owned in backend/src/test/java/tw/waterballsa/contract/OrderControllerTest.java (test owned courses list, empty state)
- [ ] T129 [P] [US4] Integration test for order history pagination in backend/src/test/java/tw/waterballsa/integration/OrderPaginationTest.java (create 15 orders, fetch page 1 and 2, verify 10 per page)
- [ ] T130 [P] [US4] Integration test for order authorization in backend/src/test/java/tw/waterballsa/integration/OrderAuthorizationTest.java (User A tries to access User B's order, expect 403)

### Database Schema for User Story 4 (GREEN Phase)

- [ ] T131 [US4] Create order table migration in backend/src/main/resources/db/migration/V11__create_orders_table.sql (order_id, user_id FK RESTRICT, order_number unique, total_amount, payment_status, payment_method, coupon_id, created_at, updated_at)
- [ ] T132 [P] [US4] Create order_item table migration in backend/src/main/resources/db/migration/V12__create_order_items_table.sql (item_id, order_id FK CASCADE, course_id, price, purchased_at)
- [ ] T133 [P] [US4] Create course_ownership table migration in backend/src/main/resources/db/migration/V13__create_course_ownership_table.sql (ownership_id, user_id FK CASCADE, course_id, acquired_date, unique constraint user_id+course_id)
- [ ] T134 [P] [US4] Create indexes for orders in backend/src/main/resources/db/migration/V14__create_order_indexes.sql (idx_order_user_id_created, idx_order_number, idx_order_status)
- [ ] T135 [P] [US4] Create indexes for order_item in backend/src/main/resources/db/migration/V15__create_order_item_indexes.sql (idx_orderitem_order_id, idx_orderitem_course_id)
- [ ] T136 [P] [US4] Create indexes for course_ownership in backend/src/main/resources/db/migration/V16__create_ownership_indexes.sql (unique idx_ownership_user_course, idx_ownership_user_id, idx_ownership_course_id)

### Backend Models for User Story 4 (GREEN Phase)

- [ ] T137 [P] [US4] Create Order entity in backend/src/main/java/tw/waterballsa/model/Order.java (JPA entity with @ManyToOne User, @OneToMany OrderItem, payment_status enum)
- [ ] T138 [P] [US4] Create OrderItem entity in backend/src/main/java/tw/waterballsa/model/OrderItem.java (JPA entity with @ManyToOne Order)
- [ ] T139 [P] [US4] Create CourseOwnership entity in backend/src/main/java/tw/waterballsa/model/CourseOwnership.java (JPA entity with @ManyToOne User)
- [ ] T140 [P] [US4] Create PaymentStatus enum in backend/src/main/java/tw/waterballsa/model/PaymentStatus.java (待付款, 已付款, 已完成, 已取消)

### Backend Repositories for User Story 4 (GREEN Phase)

- [ ] T141 [P] [US4] Create OrderRepository in backend/src/main/java/tw/waterballsa/repository/OrderRepository.java (findByUserIdOrderByCreatedAtDesc with Pageable, findByOrderIdAndUserId)
- [ ] T142 [P] [US4] Create OrderItemRepository in backend/src/main/java/tw/waterballsa/repository/OrderItemRepository.java (findByOrderId)
- [ ] T143 [P] [US4] Create CourseOwnershipRepository in backend/src/main/java/tw/waterballsa/repository/CourseOwnershipRepository.java (findByUserIdOrderByAcquiredDateDesc with Pageable)

### Backend DTOs for User Story 4 (GREEN Phase)

- [ ] T144 [P] [US4] Create OrderListResponse DTO in backend/src/main/java/tw/waterballsa/dto/OrderListResponse.java (orders: List<OrderSummary>, pagination: Pagination)
- [ ] T145 [P] [US4] Create OrderSummary DTO in backend/src/main/java/tw/waterballsa/dto/OrderSummary.java (order_id, order_number, created_at, total_amount, payment_status in Chinese)
- [ ] T146 [P] [US4] Create OrderDetail DTO in backend/src/main/java/tw/waterballsa/dto/OrderDetail.java (order_id, order_number, created_at, updated_at, total_amount, payment_status, payment_method, items, subtotal, discount)
- [ ] T147 [P] [US4] Create OrderItemDTO in backend/src/main/java/tw/waterballsa/dto/OrderItemDTO.java (item_id, course_id, course_name, price, purchased_at)
- [ ] T148 [P] [US4] Create OwnedCoursesResponse DTO in backend/src/main/java/tw/waterballsa/dto/OwnedCoursesResponse.java (courses: List<OwnedCourse>, pagination: Pagination)
- [ ] T149 [P] [US4] Create OwnedCourse DTO in backend/src/main/java/tw/waterballsa/dto/OwnedCourse.java (course_id, course_title, instructor, acquired_date)
- [ ] T150 [P] [US4] Create Pagination DTO in backend/src/main/java/tw/waterballsa/dto/Pagination.java (current_page, page_size, total_items, total_pages)

### Backend Service for User Story 4 (GREEN Phase)

- [ ] T151 [US4] Implement OrderService in backend/src/main/java/tw/waterballsa/service/OrderService.java (getOrders: fetch paginated orders for user, return OrderListResponse)
- [ ] T152 [US4] Implement getOrderDetail in OrderService: fetch order by ID, verify user_id matches (throw 403 if not), fetch order items, return OrderDetail
- [ ] T153 [US4] Implement getOwnedCourses in OrderService: fetch paginated course_ownership for user, return OwnedCoursesResponse with course info
- [ ] T154 [US4] Implement pagination logic in OrderService: calculate total_pages, handle page boundaries, return empty list if no data

### Backend Controller for User Story 4 (GREEN Phase)

- [ ] T155 [US4] Implement OrderController in backend/src/main/java/tw/waterballsa/controller/OrderController.java (3 endpoints from contracts/orders.yaml)
- [ ] T156 [US4] Implement GET /orders endpoint: validate JWT, parse page and page_size query params (default page=1, page_size=10), call OrderService.getOrders, return OrderListResponse
- [ ] T157 [US4] Implement GET /orders/{orderId} endpoint: validate JWT, call OrderService.getOrderDetail, handle 403/404, return OrderDetail
- [ ] T158 [US4] Implement GET /courses/owned endpoint: validate JWT, parse pagination params (default page=1, page_size=20), call OrderService.getOwnedCourses, return OwnedCoursesResponse

### Frontend Types for User Story 4 (GREEN Phase)

- [ ] T159 [P] [US4] Create Order types in frontend/src/types/order.ts (OrderListResponse, OrderSummary, OrderDetail, OrderItem, OwnedCoursesResponse, OwnedCourse, Pagination)

### Frontend Components for User Story 4 (GREEN Phase)

- [ ] T160 [P] [US4] Create OrderList component in frontend/src/components/orders/OrderList.tsx (display orders with order_number, created_at, total_amount, payment_status in Chinese, pagination controls, empty state "您目前沒有任何訂單")
- [ ] T161 [P] [US4] Create OrderDetail component in frontend/src/components/orders/OrderDetail.tsx (display order items, subtotal, discount, total, payment method, status)

### Frontend Hooks for User Story 4 (GREEN Phase)

- [ ] T162 [P] [US4] Create useOrders hook in frontend/src/hooks/useOrders.ts (fetchOrders with pagination, fetchOrderDetail)
- [ ] T163 [P] [US4] Create useOwnedCourses hook in frontend/src/hooks/useOwnedCourses.ts (fetchOwnedCourses with pagination)

### Frontend Pages for User Story 4 (GREEN Phase)

- [ ] T164 [US4] Create orders page in frontend/src/app/orders/page.tsx (fetch orders with useOrders, render OrderList with pagination, handle page navigation)
- [ ] T165 [US4] Create order detail page in frontend/src/app/orders/[orderId]/page.tsx (fetch order detail with useOrders, render OrderDetail, handle 403/404 errors)
- [ ] T166 [US4] Create owned courses page in frontend/src/app/courses/page.tsx (fetch owned courses with useOwnedCourses, display course cards with "開始學習" button, empty state with link to catalog)

### Refactor Phase

- [ ] T167 [US4] Refactor OrderService: extract pagination logic to reusable PaginationUtil class
- [ ] T168 [US4] Add performance logging for order queries (<2s requirement for 100 orders)

**Checkpoint**: User Story 4 complete. Users can view order history with pagination, order details with authorization, and owned courses list.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T169 [P] Update quickstart.md with OAuth setup verification steps in specs/001-member-system/quickstart.md
- [ ] T170 [P] Add API response time logging middleware in backend/src/main/java/tw/waterballsa/config/PerformanceLoggingInterceptor.java (log all endpoint response times)
- [ ] T171 [P] Add frontend error boundary in frontend/src/app/error.tsx (catch React errors, display Chinese error page)
- [ ] T172 [P] Implement Chinese error messages for all exception types in backend/src/main/java/tw/waterballsa/exception/ErrorMessages.java
- [ ] T173 [P] Add unit tests for validation logic in backend/src/test/java/tw/waterballsa/unit/ValidationTest.java (GitHub URL, gender enum, email format)
- [ ] T174 [P] Add unit tests for encryption in backend/src/test/java/tw/waterballsa/unit/EncryptionTest.java (encrypt/decrypt PII fields)
- [ ] T175 [P] Setup CI pipeline configuration in .github/workflows/ci.yml (run tests, checkstyle, eslint on PR)
- [ ] T176 [P] Add frontend accessibility improvements: ARIA labels for Chinese UI elements
- [ ] T177 Security audit: scan dependencies for vulnerabilities with OWASP Dependency Check
- [ ] T178 Performance test: verify 500 concurrent users with JMeter in backend/src/test/resources/jmeter/concurrent-users.jmx
- [ ] T179 Run quickstart.md validation: verify all steps work from scratch on clean environment
- [ ] T180 Create PR checklist template in .github/pull_request_template.md (tests pass, constitution compliance, <400 lines)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (Phase 1) completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (Phase 2) completion - MVP baseline
- **User Story 2 (Phase 4)**: Depends on Foundational (Phase 2) completion - Can start in parallel with US1 if staffed, or after US1 if sequential
- **User Story 3 (Phase 5)**: Depends on Foundational (Phase 2) completion - Can start in parallel with US1/US2 if staffed
- **User Story 4 (Phase 6)**: Depends on Foundational (Phase 2) completion - Can start in parallel with US1/US2/US3 if staffed
- **Polish (Phase 7)**: Depends on desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories - Foundation only
- **User Story 2 (P2)**: No dependencies on other stories - Foundation only (uses User entity from US1 but can be developed in parallel)
- **User Story 3 (P3)**: No dependencies on other stories - Foundation only (uses User entity from US1 but can be developed in parallel)
- **User Story 4 (P2)**: No dependencies on other stories - Foundation only (uses User entity from US1 but can be developed in parallel)

**Key Insight**: All 4 user stories are independently implementable and testable once Foundational phase completes.

### Within Each User Story

- **Tests** (RED) MUST be written FIRST and FAIL before implementation
- **Schema** migrations before models
- **Models** before repositories
- **Repositories** before services
- **DTOs** before services/controllers
- **Services** before controllers
- **Frontend types** before components
- **Components** before pages
- **Core implementation** complete before refactoring

### Parallel Opportunities

**Setup Phase (Phase 1)**:
- T003 (frontend init) + T002 (backend init) can run in parallel
- T005 (eslint) + T006 (checkstyle) + T004 (.env) can run in parallel
- T007 (backend Dockerfile) + T008 (frontend Dockerfile) can run in parallel

**Foundational Phase (Phase 2)**:
- T011 (Flyway) + T012 (Redis) + T013 (encryption functions) can run in parallel
- T015 (CORS) + T016 (JWT util) + T017 (JWT filter) can run in parallel after T014
- T020-T025 (all infrastructure tasks) can run in parallel

**User Story 1 (after Foundational)**:
- T026-T034 (all 9 tests) can run in parallel
- T035-T038 (all 4 schema migrations) can run in parallel
- T039-T041 (all 3 models) can run in parallel after T035-T038
- T042-T043 (all 2 repositories) can run in parallel after T039-T041
- T044-T046 (all 3 DTOs) can run in parallel
- T057-T058 (all 2 frontend types) can run in parallel
- T059-T060 (all 2 components) can run in parallel

**User Story 2 (after Foundational)**:
- T068-T072 (all 5 tests) can run in parallel
- T075, T076, T077-T079 can run in parallel

**User Story 3 (after Foundational)**:
- T096-T101 (all 6 tests) can run in parallel
- T102-T103 can run in parallel

**User Story 4 (after Foundational)**:
- T126-T130 (all 5 tests) can run in parallel
- T131-T136 (all 6 schema migrations) can run in parallel
- T137-T140 (all 4 models) can run in parallel
- T141-T143 (all 3 repositories) can run in parallel
- T144-T150 (all 7 DTOs) can run in parallel

**Polish Phase**:
- T169-T176 (all 8 documentation/testing tasks) can run in parallel

**Cross-Story Parallel Execution**:
If you have 4 developers, after completing Foundational (Phase 2):
- Developer A: Complete User Story 1 (T026-T067)
- Developer B: Complete User Story 2 (T068-T095)
- Developer C: Complete User Story 3 (T096-T125)
- Developer D: Complete User Story 4 (T126-T168)

All 4 stories are independently testable and deployable.

---

## Parallel Example: User Story 1 Tests

```bash
# Launch all 9 contract/integration tests together (RED phase):
Task T026: "Contract test for GET /auth/oauth/{provider}/authorize"
Task T027: "Contract test for GET /auth/oauth/{provider}/callback"
Task T028: "Contract test for GET /auth/session"
Task T029: "Contract test for POST /auth/refresh"
Task T030: "Contract test for POST /auth/logout"
Task T031: "Integration test for OAuth registration flow"
Task T032: "Integration test for OAuth login flow"
Task T033: "Integration test for account auto-merge"
Task T034: "Integration test for concurrent sessions"

# Launch all 4 schema migrations together (GREEN phase):
Task T035: "Create users table migration"
Task T036: "Create oauth_provider_link table migration"
Task T037: "Create indexes for users"
Task T038: "Create indexes for oauth_provider_link"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. ✅ Complete Phase 1: Setup (T001-T009)
2. ✅ Complete Phase 2: Foundational (T010-T025) - CRITICAL BLOCKER
3. ✅ Complete Phase 3: User Story 1 (T026-T067)
4. **STOP and VALIDATE**: Test User Story 1 independently
   - Register with Google, logout, login with Google
   - Register with Facebook using same email, verify auto-merge
   - Test concurrent sessions on multiple devices
   - Verify session expiration and re-authentication
5. Deploy/demo MVP if ready

### Incremental Delivery (Recommended)

1. Complete Setup + Foundational → Foundation ready (T001-T025)
2. Add User Story 1 → Test independently → Deploy/Demo (MVP: OAuth authentication works!) (T026-T067)
3. Add User Story 2 → Test independently → Deploy/Demo (Users can edit profiles, see achievements) (T068-T095)
4. Add User Story 4 → Test independently → Deploy/Demo (Users can track orders and owned courses) (T126-T168)
5. Add User Story 3 → Test independently → Deploy/Demo (Users can link Discord/GitHub) (T096-T125)
6. Polish → Final release (T169-T180)

**Rationale**: US4 (order history) is P2 and more critical for business than US3 (third-party linking), so implement US4 before US3.

### Parallel Team Strategy

With 4 developers after Foundational phase (T025 complete):

**Week 1**: Foundation
- Team: Complete Setup (T001-T009) together
- Team: Complete Foundational (T010-T025) together

**Week 2-3**: Parallel User Stories
- Developer A: User Story 1 (T026-T067) - OAuth authentication
- Developer B: User Story 2 (T068-T095) - Profile management
- Developer C: User Story 4 (T126-T168) - Order history
- Developer D: User Story 3 (T096-T125) - Third-party linking

**Week 4**: Integration & Polish
- Team: Integration testing across all stories
- Team: Polish phase (T169-T180)
- Team: Deploy full feature set

---

## Notes

- **[P]** tasks = Different files, no dependencies, safe to parallelize
- **[Story]** label = Maps task to specific user story for traceability
- **TDD Mandatory**: All tests MUST fail before implementation (Red-Green-Refactor)
- **Independent Stories**: Each user story is independently completable and testable
- **Commit Strategy**: Commit after each task or logical group (e.g., all tests for a story)
- **Checkpoints**: Stop at any checkpoint to validate story independently
- **Constitution Compliance**: Tests required, 90%+ coverage for auth, <100ms API responses, Chinese UI
- **File Paths**: All paths are absolute from repository root
- **Avoid**: Vague tasks, same file conflicts, cross-story dependencies that break independence

---

## Task Summary

- **Total Tasks**: 180
- **Setup Phase**: 9 tasks
- **Foundational Phase**: 16 tasks (CRITICAL - blocks all stories)
- **User Story 1 (MVP)**: 42 tasks (T026-T067)
- **User Story 2**: 28 tasks (T068-T095)
- **User Story 3**: 30 tasks (T096-T125)
- **User Story 4**: 43 tasks (T126-T168)
- **Polish Phase**: 12 tasks (T169-T180)

**Parallel Opportunities Identified**: 89 tasks marked with [P] can run in parallel within their phase constraints.

**Independent Test Criteria**:
- **US1**: Can register/login with OAuth, accounts auto-merge, concurrent sessions work
- **US2**: Can view/edit profile, see level and achievements, localStorage preservation works
- **US3**: Can link/unlink Discord and GitHub, duplicate prevention works
- **US4**: Can view paginated orders, order details with authorization, owned courses list

**Suggested MVP Scope**: Complete User Story 1 only (OAuth authentication). Deploy and validate before proceeding to additional stories.
