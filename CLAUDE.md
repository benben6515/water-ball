# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a recreation of the Water Ball Software Academy platform (https://world.waterballsa.tw/), a **gamified learning management system** featuring video courses, member management, progression systems, and e-commerce capabilities.

**Key Characteristics**:
- Gamification elements: levels, leaderboards, missions, gym challenges
- Course organization: dungeon/chapter system with difficulty ratings
- Skill tracking: radar charts for OOP analysis, design, and programming
- E-commerce: shopping cart, checkout flow, coupon system

**Reference SOP**: https://waterball.notion.site/SOP-2a73be1863f680418cfcf9e2508e1c38

## Technology Stack

- **Frontend**: Next.js
- **Backend**: Java with Spring Boot
- **Database**: PostgreSQL
- **Deployment**: Docker Compose for local development (all services containerized)
- **Video Storage**: Cloud service (original uses S3, alternatives acceptable)

## Development Commands

### Docker Compose
```bash
# Start all services (frontend, backend, database)
docker-compose up

# Start in detached mode
docker-compose up -d

# Rebuild containers
docker-compose up --build

# Stop all services
docker-compose down
```

### Frontend (Next.js)
```bash
cd frontend  # or wherever Next.js project is located
npm install
npm run dev      # Development server
npm run build    # Production build
npm run start    # Start production server
npm run lint     # Lint code
```

### Backend (Spring Boot)
```bash
cd backend  # or wherever Spring Boot project is located

# Using Maven
./mvnw spring-boot:run           # Run application
./mvnw clean install             # Build
./mvnw test                      # Run all tests
./mvnw test -Dtest=TestClassName # Run single test

# Using Gradle (if chosen instead)
./gradlew bootRun                # Run application
./gradlew build                  # Build
./gradlew test                   # Run all tests
./gradlew test --tests TestClassName # Run single test
```

## Architecture

### Monorepo Structure
The project follows a monorepo structure with separate frontend and backend directories, orchestrated by Docker Compose for local development.

### Priority Levels
Features are categorized by priority:
- **p0**: Core functionality (must implement) - Member system, video playback, course listing
- **p1**: Secondary features (should implement) - Gamification, e-commerce, social integration
- **p2**: Optional features - Reviews, favorites, search, notifications
- **p3**: Advanced optimizations - Analytics, tracking tools

### Core Features (p0)

**Member System**:
- OAuth integration (Google, Facebook) for registration/login
- User profile management: nickname, email, level, gender, GitHub link, occupation, achievements, birthday, location
- Third-party integrations: Discord, GitHub
- Order history tracking

**Video Course System**:
- Video playback with quality selection and speed control
- Auto-advance to next lesson (5-second countdown)
- Auto-save progress at 95% completion
- Resume from last watched position

**Course Listing**:
- Course card display with cover images, instructor info
- Ownership status ("尚未擁有" / "已擁有")
- Course categorization and filtering
- Purchase buttons and quick buy flow

### Secondary Features (p1)

**Gamification System**:
- **Leaderboard**: Weekly growth rankings with Lv. system and real-time updates
- **Mission System**: 10 key missions with progressive unlocking, prerequisite requirements, gym challenges
- **Dungeon/Chapter System**: 8 main dungeons (副本 0-7) with 49 videos, practice problems, difficulty ratings (★ to ★★★★)

**Learning Support**:
- **Skill Tracking**: 6 core skills (OOP analysis, design, programming) with radar chart visualization
- **Prompt Encyclopedia**: Dedicated page at `/journeys/software-design-pattern/sop` (login required)

**E-commerce**:
- Shopping cart with add/remove, quantity adjustment
- Checkout flow with payment gateway integration
- Coupon system with validation and discount calculation
- "Buy Now" quick purchase option

**Social Integration**:
- Discord deep integration (OAuth, progress sharing, activity notifications)
- Facebook community links
- Blog integration (blog.waterballsa.tw)
- Code Review service (submission, instructor feedback)

**Static Pages**:
- Privacy policy: /terms/privacy
- Terms of service: /terms/service
- Homepage: course intro, instructor profiles, social media links

### Optional Features (p2)
- Course reviews and ratings
- Favorites/bookmarks
- Search functionality
- Notification system (in-app, email)

### Advanced Features (p3)
- Google Analytics, Tag Manager
- Microsoft Clarity for behavior analysis

## Key Development Considerations

### Gamification Architecture
This platform is fundamentally a **gamified learning system**. The architecture must support:

**Level System**:
- Experience points (XP) calculation based on video completion, mission completion, practice problem submissions
- Level-up triggers and rewards
- Level display throughout the platform (leaderboard, profile, comments)

**Mission System**:
- Mission state machine: locked → unlocked → in_progress → completed
- Prerequisite dependency graph for progressive unlocking
- Mission types: watch videos, complete problems, share to Discord, gym challenges
- Mission completion validation and reward distribution

**Dungeon/Chapter System**:
- Hierarchical structure: Course → Dungeons (副本) → Videos
- 8 dungeons (副本 0-7) containing 49 total videos
- Difficulty rating metadata (★ to ★★★★)
- Practice problems associated with videos/dungeons
- Progress tracking at dungeon and video level

**Leaderboard System**:
- Real-time or near-real-time ranking updates
- Weekly growth calculation (XP gained in past 7 days)
- Efficient queries for top N users
- Caching strategy for performance

### Skill Tracking System
**Data Model**:
- 6 core skills mapped to learning activities:
  - OOP Analysis: requirements structuring, behavior/structure distinction
  - OOP Design: abstraction, context building, design patterns
  - OOP Programming: implementation quality
- Skill progress calculation based on completed videos/problems
- Radar chart visualization data format

### Authentication & Authorization
The system requires OAuth integration with multiple providers (Google, Facebook) and third-party account linking (Discord, GitHub). Ensure:
- Secure token management and session handling
- Multiple OAuth providers linked to single account
- Discord webhook integration for activity sharing
- Role-based access control (student, instructor, admin)

### Video Streaming & Progress Tracking
Video playback implementation considerations:
- Progress tracking at 95% threshold for completion
- Automatic navigation with 5-second countdown UI
- Resume from last watched position
- Playback speed control, quality selection
- Video metadata: duration, thumbnail, associated dungeon/chapter

**Database Design**:
- `user_video_progress` table: user_id, video_id, watch_percentage, last_position, completed_at
- Efficient queries for "continue watching" and "completed videos"
- Handle concurrent progress updates (multiple devices)

### E-commerce Integration
**Shopping Cart & Checkout**:
- Cart persistence (logged-in users: database, guests: session/local storage)
- Cart sync on login
- Coupon validation: code uniqueness, expiration, usage limits, discount rules
- Payment gateway integration (credit card, ATM, etc.)
- Order state machine: pending → paid → fulfilled
- Post-purchase: grant course access, send confirmation email

**Database Tables**:
- `orders`: user_id, total_amount, coupon_id, payment_status, created_at
- `order_items`: order_id, course_id, price
- `coupons`: code, discount_type, discount_value, expiration, usage_limit

### Docker Compose Integration
All services (frontend, backend, database) must be containerized and work together via Docker Compose. Ensure:
- Proper networking configuration
- Environment variable management (.env files)
- Volume mounting for local development and data persistence
- Service dependencies and health checks
- Development vs production configuration separation

### Database Schema Considerations
PostgreSQL schema should support:

**User Management**:
- User profiles with multiple OAuth providers
- Level, XP, achievements (突破道館 records)
- Third-party account links (Discord, GitHub)

**Course Structure**:
- Courses → Dungeons → Videos hierarchy
- Video metadata: title, description, duration, difficulty, associated dungeon
- Practice problems with difficulty ratings

**Progress Tracking**:
- Video watch progress per user
- Mission completion status per user
- Skill progress tracking
- Dungeon completion tracking

**Gamification**:
- User XP history for leaderboard calculations
- Mission prerequisites and dependencies
- Achievement/badge system

**E-commerce**:
- Orders and order items
- Coupons with validation rules
- User course ownership

**Performance Considerations**:
- Index on frequently queried fields (user_id, course_id, video_id)
- Denormalization for leaderboard queries (consider materialized views)
- Caching layer (Redis) for leaderboard and frequently accessed data

## Active Technologies
- PostgreSQL 15+ (user profiles, OAuth links, orders, course ownership) (001-member-system)

## Recent Changes
- 001-member-system: Added PostgreSQL 15+ (user profiles, OAuth links, orders, course ownership)
