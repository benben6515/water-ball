# Water Ball Software Academy Constitution

<!--
Sync Impact Report (Version 1.0.0):
─────────────────────────────────────────────────────────────────
Version Change: Initial constitution (0.0.0 → 1.0.0)

New Sections Added:
✓ Core Principles (8 principles)
  - I. Gamification-First Architecture
  - II. Test-Driven Development (NON-NEGOTIABLE)
  - III. Performance & Scalability Standards
  - IV. User Experience Consistency
  - V. Security & Data Privacy
  - VI. Code Quality Standards
  - VII. Docker-First Development
  - VIII. Documentation & Knowledge Transfer

✓ Technology Stack Compliance
✓ Development Workflow & Quality Gates
✓ Governance Rules

Templates Impact:
✅ plan-template.md - Constitution Check section aligns with principles
✅ spec-template.md - User scenarios align with UX consistency requirements
✅ tasks-template.md - Test-first workflow matches TDD principle
✅ checklist-template.md - Quality gates align with code standards
✅ agent-file-template.md - No updates required (generic template)

Follow-up Actions: None required
─────────────────────────────────────────────────────────────────
-->

## Core Principles

### I. Gamification-First Architecture

**The platform is fundamentally a gamified learning system.** All technical decisions must support the progression mechanics that drive user engagement:

- **XP & Level System**: Every user action that contributes to learning (video completion, problem submission, mission completion) MUST award experience points and trigger level-up calculations
- **Mission State Machine**: Mission progression (locked → unlocked → in_progress → completed) MUST be enforced with prerequisite validation at every state transition
- **Dungeon Hierarchy**: Course → Dungeons (8 total: 副本 0-7) → Videos (49 total) structure MUST be preserved in all data models
- **Real-time Leaderboard**: Weekly growth rankings MUST update within 5 minutes of XP-earning actions
- **Skill Tracking**: The 6 core OOP skills MUST be calculable from completed activities with radar chart data format support

**Rationale**: Gamification is not a feature overlay—it is the core value proposition. Poor performance or data inconsistency in progression systems directly damages user retention.

### II. Test-Driven Development (NON-NEGOTIABLE)

**Red-Green-Refactor cycle is mandatory for all feature work:**

1. **Red**: Write acceptance tests that capture user scenarios from spec.md. Tests MUST fail initially.
2. **Green**: Implement minimum code to make tests pass. No gold-plating.
3. **Refactor**: Clean up while keeping tests green.

**Test Types Required**:
- **Contract Tests**: For all API endpoints, OAuth flows, payment gateway integrations
- **Integration Tests**: For gamification state transitions, video progress tracking, e-commerce checkout flows
- **Unit Tests**: For XP calculations, coupon validation, mission prerequisite logic

**Test Coverage Minimums**:
- Critical paths (authentication, payment, XP calculation): 90%+
- Gamification logic (missions, leaderboard): 85%+
- General features: 80%+

**Rationale**: This platform handles user payments, learning progress, and social sharing. Regressions in these areas cause direct financial or reputational damage. Tests are the only reliable safety net during rapid iteration.

### III. Performance & Scalability Standards

**The system MUST meet these non-negotiable performance targets:**

**Video Streaming**:
- Video playback start: <2 seconds (p95)
- Quality switching: <500ms
- Progress save latency: <200ms (user should never lose position)
- Concurrent streams supported: 500+ users

**Gamification Queries**:
- Leaderboard generation: <1 second for top 100 users
- XP calculation & level-up: <100ms
- Mission prerequisite check: <50ms
- Skill radar chart data: <200ms

**E-commerce**:
- Cart operations: <100ms
- Checkout flow page load: <1.5 seconds
- Payment processing: <5 seconds end-to-end
- Coupon validation: <50ms

**Database**:
- Index all foreign keys (user_id, course_id, video_id, mission_id)
- Use materialized views for leaderboard weekly growth calculations
- Implement Redis caching for: leaderboard top N, user level cache, active missions

**Horizontal Scaling**:
- Backend MUST be stateless (session in Redis/DB, not memory)
- Database connection pooling required (min 10, max 50 per instance)
- Frontend MUST support CDN caching for static assets

**Rationale**: Poor performance destroys gamification engagement. A 3-second leaderboard load kills the competitive experience. Video buffering breaks learning flow.

### IV. User Experience Consistency

**All UI components and user flows MUST adhere to the reference platform patterns:**

**Visual Language**:
- Course ownership badges: "尚未擁有" (not owned) / "已擁有" (owned) - exact Chinese terms required
- Difficulty stars: ★ to ★★★★ (Unicode U+2605, not images)
- Level display: "Lv." prefix consistently (e.g., "Lv. 15")
- Countdown UI: "5 秒後自動播放下一節" (5-second auto-advance) with cancel button

**Navigation Flows**:
- Video auto-advance: 5-second countdown → auto-play next lesson (cancellable)
- Login wall: Social features redirect to OAuth when not authenticated
- Quick buy: Single-click "立刻購買" bypasses cart, direct to checkout
- Progress resume: "從上次觀看處繼續" option always visible for in-progress videos

**Accessibility**:
- Keyboard navigation for video controls
- Screen reader labels for all interactive elements (Chinese localization)
- Color contrast ratio ≥4.5:1 for text
- Touch targets ≥44×44px on mobile

**Error Handling UX**:
- Network errors: Graceful retry with user-visible status
- Payment failures: Clear error messages with support contact
- Video playback errors: Auto-fallback to lower quality

**Rationale**: Users expect consistency with the original platform. Deviations create confusion and lower trust, especially in payment flows.

### V. Security & Data Privacy

**Security is mandatory, not optional:**

**Authentication & Authorization**:
- OAuth 2.0 for Google/Facebook (PKCE flow for SPAs)
- JWT tokens with 15-minute access token expiry, 7-day refresh token
- Role-based access control (student, instructor, admin) enforced at API layer
- Discord/GitHub linking MUST validate account ownership via OAuth

**Data Protection**:
- Passwords MUST use bcrypt with cost factor ≥12 (if email/password added)
- PII (email, birthday, location) MUST be encrypted at rest (AES-256)
- Payment info NEVER stored (use payment gateway tokens only)
- GDPR compliance: User data export, deletion on request within 30 days

**API Security**:
- Rate limiting: 100 requests/minute per user, 1000/minute per IP
- CORS: Whitelist frontend domain only (no wildcard `*`)
- SQL injection prevention: Parameterized queries ONLY (no string concatenation)
- XSS prevention: Escape all user-generated content in templates

**Audit Logging**:
- Log all: Login attempts, OAuth grants, payment transactions, XP awards, mission completions
- Retention: 90 days minimum for security events, 1 year for financial transactions
- PII redaction in logs (mask email, IP after 30 days)

**Rationale**: This platform handles user payments and personal learning data. A breach would be catastrophic for trust and legally problematic under GDPR/privacy laws.

### VI. Code Quality Standards

**All code MUST pass these gates before merge:**

**Code Style**:
- **Backend (Java/Spring Boot)**: Google Java Style Guide, enforced by Checkstyle
- **Frontend (Next.js)**: Airbnb JavaScript/TypeScript Style Guide, enforced by ESLint + Prettier
- **Database**: Snake_case for tables/columns, singular nouns for tables (e.g., `user`, not `users`)

**Code Review Requirements**:
- Minimum 1 approval from team member
- No merge if CI fails (tests, linting, security scan)
- PR description MUST reference spec.md user story or task ID
- Max PR size: 400 lines changed (excluding generated code) for reviewability

**Complexity Limits**:
- Cyclomatic complexity: <10 per method/function
- Class size: <300 lines (excluding generated getters/setters)
- Method parameters: <5 arguments (use objects for >5)

**Dependency Management**:
- Security: Run `npm audit` / `mvn dependency-check` weekly, fix HIGH+ within 7 days
- Licenses: Approve all dependencies (allow MIT, Apache 2.0, BSD; reject GPL, AGPL)
- Minimize: Justify each new dependency (no "for one function" libraries)

**Technical Debt**:
- Track in TODOs with format: `// TODO(github-username): [Jira-123] description`
- Review quarterly, prioritize debt that blocks scaling or security

**Rationale**: Code quality directly impacts velocity. Messy code slows down the TDD cycle and makes gamification logic brittle (which is mission-critical).

### VII. Docker-First Development

**All services MUST run via Docker Compose for local development:**

**Container Requirements**:
- **Frontend**: Next.js in Node.js Alpine image, hot-reload via volume mounts
- **Backend**: Spring Boot with JDK 17+ Alpine image, JVM tuning for container limits
- **Database**: PostgreSQL 15+ with persistent volume for data
- **Cache** (when added): Redis 7+ for leaderboard and session storage

**Networking**:
- Internal network for backend ↔ database, backend ↔ cache
- Frontend → Backend via exposed port, no direct frontend → database
- Health checks for all services (HTTP /health endpoint, 30s timeout)

**Environment Variables**:
- ALL secrets via `.env` files (NEVER commit to git, use `.env.example` template)
- Separate configs for `dev`, `staging`, `prod` environments
- Required vars: DB credentials, OAuth client IDs/secrets, payment gateway keys, JWT signing key

**Development Workflow**:
- `docker-compose up` MUST start all services in <2 minutes (first run), <30 seconds (cached)
- Code changes auto-reload (no manual container restart for source edits)
- Database migrations auto-apply on backend startup (dev only, manual for prod)

**Production Parity**:
- Use same base images for dev/prod (version pinning, e.g., `node:20-alpine`)
- Multi-stage builds for optimized prod images (<500MB per service)
- Docker Compose for local, Kubernetes manifests for production (maintain parity)

**Rationale**: Containerization prevents "works on my machine" issues and makes onboarding instant. Gamification systems have complex state—reproducible environments are critical for debugging.

### VIII. Documentation & Knowledge Transfer

**Documentation is code—treat it with the same rigor:**

**Required Documentation**:
- **README.md**: Quick start guide (docker-compose up, access URLs, test credentials)
- **CLAUDE.md**: AI assistant context (architecture, commands, priority levels) *(already exists)*
- **API Contracts** (`/specs/[feature]/contracts/`): OpenAPI 3.0 specs for all endpoints
- **Data Models** (`/specs/[feature]/data-model.md`): Entity relationships, state machines (missions, orders)
- **Runbooks**: Common ops tasks (DB backup, cache flush, user data export)

**Code Documentation**:
- **Public APIs**: Javadoc/JSDoc with param descriptions, return types, example usage
- **Complex Logic**: Inline comments for XP formulas, mission prerequisite graphs, coupon discount calculations
- **State Machines**: Document all states and transitions (e.g., mission lifecycle, order statuses)

**Onboarding**:
- New developers MUST complete "First Feature" guide (spec.md → plan.md → tasks.md → implementation)
- Pair programming for first gamification feature (mission or leaderboard) to transfer domain knowledge

**Change Communication**:
- Breaking API changes: Announce 2 sprints ahead, provide migration guide
- Database schema changes: Document in migration files with rollback instructions
- Constitution amendments: PR with rationale, team discussion, version bump

**Rationale**: Gamification domain knowledge is complex (XP formulas, prerequisite graphs). Undocumented tribal knowledge creates bottlenecks and risks loss when team members change.

## Technology Stack Compliance

**Mandatory technologies per project requirements:**

- **Frontend**: Next.js (React framework) with TypeScript (strict mode)
- **Backend**: Java 17+ with Spring Boot 3.x, Maven or Gradle
- **Database**: PostgreSQL 15+ (no NoSQL for core data—consistency over flexibility)
- **Containerization**: Docker + Docker Compose (all services)
- **Video Storage**: S3-compatible object storage (AWS S3, MinIO, Backblaze B2)

**Approved Additional Tools**:
- **Caching**: Redis (for leaderboard, session, rate limiting)
- **Message Queue**: RabbitMQ or Kafka (if async needed for notifications, webhooks)
- **Monitoring**: Prometheus + Grafana (performance metrics), Sentry (error tracking)
- **CI/CD**: GitHub Actions preferred (Docker build, test, security scan)

**Prohibited**:
- No NoSQL databases for core entities (user, course, progress) due to transaction requirements
- No serverless for backend (WebSockets needed for real-time features, cold starts unacceptable)
- No client-side routing for authenticated pages (SEO + security)

**Rationale**: These constraints align with project spec (CLAUDE.md) and ensure team expertise overlap. Deviations require constitution amendment.

## Development Workflow & Quality Gates

**Feature development follows the SpecKit workflow:**

**1. Specification Phase** (`/speckit.specify`):
- Input: User description of feature
- Output: `spec.md` with prioritized user stories (P1, P2, P3), acceptance scenarios, edge cases
- Gate: Product owner approval on user stories

**2. Planning Phase** (`/speckit.plan`):
- Input: Approved `spec.md`
- Output: `plan.md`, `research.md`, `data-model.md`, `contracts/`
- Gate: Constitution check passes (performance targets, security requirements, tech stack compliance)

**3. Task Generation** (`/speckit.tasks`):
- Input: All plan artifacts
- Output: `tasks.md` with dependency-ordered, parallelizable tasks per user story
- Gate: Tasks map 1:1 to acceptance scenarios, tests listed before implementation

**4. Implementation Phase** (`/speckit.implement` or manual):
- **Test-First**: Write contract/integration tests, verify they FAIL
- **Implement**: Code to pass tests (Green)
- **Refactor**: Clean up while keeping tests green
- **Review**: PR with spec/task references, 1+ approval, CI green
- **Merge**: Squash commits, delete feature branch

**5. Validation Gates** (before production):
- [ ] All acceptance scenarios from spec.md pass
- [ ] Performance benchmarks meet Section III standards
- [ ] Security scan (OWASP ZAP, Snyk) shows no HIGH+ vulnerabilities
- [ ] Accessibility audit (axe-core) passes WCAG 2.1 AA
- [ ] Load test: 500 concurrent users, <5% error rate
- [ ] Runbook updated for new operational tasks

**Continuous Validation**:
- **Daily**: CI runs on all PRs (tests, linting, security scan)
- **Weekly**: Dependency security updates reviewed
- **Monthly**: Constitution compliance review (sample 3 recent features)
- **Quarterly**: Technical debt review, performance regression testing

## Governance

**Constitution Authority**:
- This constitution supersedes all other team practices, style guides, or individual preferences
- Violations MUST be flagged in code review and corrected before merge
- Complexity that violates principles (e.g., skipping tests, >10 cyclomatic complexity) MUST be justified in `plan.md` Complexity Tracking table with simpler alternative analysis

**Amendment Process**:
1. Propose change via PR to `constitution.md` with:
   - **Rationale**: Why current principle is insufficient
   - **Impact Analysis**: Which templates/docs need updates
   - **Version Bump**: MAJOR (breaking), MINOR (new principle), PATCH (clarification)
2. Team discussion (minimum 3 days for feedback)
3. Approval: 2/3 majority vote
4. Migration Plan: Update affected code/docs within 2 sprints
5. Merge PR, sync templates, announce in team channel

**Compliance Enforcement**:
- **Code Review**: Reviewers MUST check for principle violations (use `/speckit.checklist` for feature validation)
- **CI Pipeline**: Automated checks for test coverage (80%+), linting, security scans
- **Retrospectives**: Discuss constitution friction quarterly, propose amendments if needed

**Version History**:
- See HTML comment at top of file for change log
- Previous versions archived in `.specify/memory/constitution-archive/`

**Agent Guidance**:
- This constitution applies to human developers and AI assistants (Claude, Copilot, etc.)
- AI-generated code MUST pass same quality gates (tests, security, style)
- Use `CLAUDE.md` for AI-specific development guidance (commands, architecture context)

---

**Version**: 1.0.0 | **Ratified**: 2025-11-14 | **Last Amended**: 2025-11-14
