# Data Model: Member System

**Feature**: Member System (001-member-system)
**Date**: 2025-11-14
**Purpose**: Define entities, relationships, validation rules, and state transitions

## Entity Relationship Diagram

```
┌──────────────────────┐
│       User           │
│──────────────────────│
│ user_id (PK)         │◄────┐
│ nickname             │     │
│ email_encrypted      │     │  Has Many
│ gender               │     │
│ birthday_encrypted   │     ├─────────────────────────────┐
│ location_encrypted   │     │                             │
│ occupation           │     │                             │
│ github_link          │     │                             │
│ level (default: 1)   │     │                             │
│ created_at           │     │                             │
│ updated_at           │     │                             │
└──────────────────────┘     │                             │
                             │                             │
        ┌────────────────────┼─────────────┐               │
        │                    │             │               │
        ▼                    ▼             ▼               ▼
┌──────────────┐  ┌─────────────────┐  ┌──────────┐  ┌──────────────┐
│OAuth Provider│  │Third-Party Link │  │   Order  │  │ Achievement  │
│     Link     │  │                 │  │          │  │              │
│──────────────│  │─────────────────│  │──────────│  │──────────────│
│link_id (PK)  │  │link_id (PK)     │  │order_id  │  │achievement_id│
│user_id (FK)  │  │user_id (FK)     │  │user_id   │  │user_id (FK)  │
│provider_type │  │platform_type    │  │order_num │  │type          │
│provider_uid  │  │platform_uid     │  │total_amt │  │name          │
│provider_email│  │platform_username│  │status    │  │earned_at     │
│linked_at     │  │linked_at        │  │payment   │  └──────────────┘
└──────────────┘  └─────────────────┘  │coupon_id │
                                       │created_at│
                                       │updated_at│
                                       └──────────┘
                                            │
                                            │ Has Many
                                            ▼
                                    ┌──────────────┐
                                    │ Order Item   │
                                    │──────────────│
                                    │item_id (PK)  │
                                    │order_id (FK) │
                                    │course_id     │
                                    │price         │
                                    │purchased_at  │
                                    └──────────────┘
                                            │
                                            │ Creates
                                            ▼
                                    ┌──────────────────┐
                                    │Course Ownership  │
                                    │──────────────────│
                                    │ownership_id (PK) │
                                    │user_id (FK)      │
                                    │course_id         │
                                    │acquired_date     │
                                    └──────────────────┘
```

## Entities

### 1. User

**Purpose**: Core entity representing a platform member with profile information and authentication credentials

**Table**: `user`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| user_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| nickname | VARCHAR(255) | NOT NULL | Display name (initialized from OAuth name or email prefix) |
| email_encrypted | BYTEA | NOT NULL, UNIQUE | Encrypted email from OAuth provider (AES-256) |
| gender | VARCHAR(50) | NULL, CHECK IN ('男', '女', '其他', '不透露') | User gender (Chinese terms) |
| birthday_encrypted | BYTEA | NULL | Encrypted birth date (AES-256) |
| location_encrypted | BYTEA | NULL | Encrypted location string (AES-256) |
| occupation | VARCHAR(255) | NULL | User occupation/profession |
| github_link | VARCHAR(500) | NULL, CHECK (valid URL format) | GitHub profile URL (https://github.com/username) |
| level | INTEGER | NOT NULL, DEFAULT 1, CHECK (>= 1) | Gamification level (read-only, updated by gamification system) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Account creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last profile update timestamp |

**Indexes**:
- `idx_user_email_encrypted` - For login lookup by email (encrypted field requires custom index)
- `idx_user_level` - For leaderboard queries
- `idx_user_created_at` - For user registration analytics

**Validation Rules**:
- **Nickname**: Required, 1-255 characters
- **Email**: Required, unique across all users (enforced via encrypted value hash)
- **Gender**: Optional, must be one of: 男 (male), 女 (female), 其他 (other), 不透露 (prefer not to say)
- **GitHub Link**: Optional, must match pattern `^https://github\.com/[a-zA-Z0-9\-]+$`
- **Level**: Always >= 1, cannot be manually edited (updated by gamification service only)

**Business Rules**:
- **Email Uniqueness**: When OAuth provider returns email already in database → auto-merge accounts (link new provider to existing user)
- **Initial Values**: New user gets nickname from OAuth name field (or email prefix if unavailable), level = 1
- **Profile Updates**: All fields editable except email (from OAuth), level (from gamification), user_id, created_at

---

### 2. OAuth Provider Link

**Purpose**: Represents connection between user and OAuth authentication provider (Google, Facebook)

**Table**: `oauth_provider_link`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| provider_link_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| user_id | UUID | NOT NULL, FOREIGN KEY → user(user_id) ON DELETE CASCADE | User reference |
| provider_type | VARCHAR(50) | NOT NULL, CHECK IN ('GOOGLE', 'FACEBOOK') | OAuth provider name |
| provider_user_id | VARCHAR(255) | NOT NULL | User ID from OAuth provider |
| provider_email | VARCHAR(255) | NOT NULL | Email from OAuth provider (plaintext for provider matching) |
| linked_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Link creation timestamp |

**Indexes**:
- `idx_oauth_user_id` - For user login queries
- `idx_oauth_provider_type_uid` - Unique constraint (provider_type, provider_user_id) to prevent duplicate links
- `idx_oauth_provider_email` - For email-based account merging

**Validation Rules**:
- **Provider Type**: Must be 'GOOGLE' or 'FACEBOOK'
- **Provider User ID**: Required, unique per provider (one Google account can't link to multiple platform users)
- **Provider Email**: Required, used for auto-merge logic

**Business Rules**:
- **Auto-Merge**: If user logs in with Facebook and email matches existing Google-linked user → create Facebook link for same user_id
- **Multiple Providers**: Single user can have both Google AND Facebook links (2 rows with same user_id, different provider_type)
- **Duplicate Prevention**: Same OAuth account (provider_type + provider_user_id) cannot link to multiple users

---

### 3. Third-Party Account Link

**Purpose**: Represents connection to Discord or GitHub for social features and achievement unlocking

**Table**: `third_party_account_link`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| link_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| user_id | UUID | NOT NULL, FOREIGN KEY → user(user_id) ON DELETE CASCADE | User reference |
| platform_type | VARCHAR(50) | NOT NULL, CHECK IN ('DISCORD', 'GITHUB') | Platform name |
| platform_user_id | VARCHAR(255) | NOT NULL | User ID from platform |
| platform_username | VARCHAR(255) | NOT NULL | Display username from platform |
| linked_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Link creation timestamp |

**Indexes**:
- `idx_thirdparty_user_id` - For user profile queries
- `idx_thirdparty_platform_type_uid` - Unique constraint (platform_type, platform_user_id) to prevent duplicate links
- `idx_thirdparty_user_platform` - Unique constraint (user_id, platform_type) to prevent user linking same platform twice

**Validation Rules**:
- **Platform Type**: Must be 'DISCORD' or 'GITHUB'
- **Platform User ID**: Required, unique per platform
- **Platform Username**: Required, displayed in UI

**Business Rules**:
- **One Link Per Platform**: User can link Discord once, GitHub once (not multiple Discord accounts)
- **Duplicate Prevention**: Discord account already linked to User A cannot link to User B
- **Unlinking**: User can unlink platform, which deletes this row (cascade deletions handled in application)

---

### 4. Order

**Purpose**: Represents a purchase transaction for courses

**Table**: `order`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| order_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| user_id | UUID | NOT NULL, FOREIGN KEY → user(user_id) ON DELETE RESTRICT | User reference (prevent deletion if orders exist) |
| order_number | VARCHAR(50) | NOT NULL, UNIQUE | Human-readable order ID (e.g., "ORD-20251114-0001") |
| total_amount | DECIMAL(10,2) | NOT NULL, CHECK (>= 0) | Final order total after discounts |
| payment_status | VARCHAR(50) | NOT NULL, CHECK IN ('待付款', '已付款', '已完成', '已取消') | Current order status |
| payment_method | VARCHAR(100) | NULL | Payment method used (credit card, ATM, etc.) |
| coupon_id | UUID | NULL, FOREIGN KEY → coupon(coupon_id) | Applied coupon reference |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Order creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last status update timestamp |

**Indexes**:
- `idx_order_user_id_created` - For user order history (sorted by date)
- `idx_order_number` - For order lookup
- `idx_order_status` - For admin order filtering

**Validation Rules**:
- **Order Number**: Unique, format `ORD-YYYYMMDD-####`
- **Total Amount**: >= 0 (free orders allowed with 100% coupon)
- **Payment Status**: Must transition through valid states (see state machine below)

**State Machine**:
```
待付款 (Pending)
  ├─> 已付款 (Paid) [on successful payment]
  ├─> 已取消 (Cancelled) [user cancels OR payment timeout]

已付款 (Paid)
  └─> 已完成 (Completed) [after course access granted]

已完成 (Completed) [TERMINAL STATE]
已取消 (Cancelled) [TERMINAL STATE]
```

**Business Rules**:
- **Status Transitions**:
  - 待付款 → 已付款: Payment gateway confirms payment
  - 已付款 → 已完成: Course ownership records created
  - 待付款 → 已取消: User cancels or 24-hour payment timeout
- **Order Modification**: Once status != 待付款, order is immutable
- **Authorization**: Users can only view their own orders (FR-052)

---

### 5. Order Item

**Purpose**: Represents individual course in an order (line item)

**Table**: `order_item`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| item_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| order_id | UUID | NOT NULL, FOREIGN KEY → order(order_id) ON DELETE CASCADE | Order reference |
| course_id | UUID | NOT NULL | Course reference (from future course catalog) |
| price | DECIMAL(10,2) | NOT NULL, CHECK (>= 0) | Course price at purchase time |
| purchased_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Item purchase timestamp |

**Indexes**:
- `idx_orderitem_order_id` - For order detail queries
- `idx_orderitem_course_id` - For course sales analytics

**Validation Rules**:
- **Price**: >= 0, captures price at time of purchase (historical record)
- **Course ID**: Required, references course catalog (defined in future feature)

**Business Rules**:
- **Price Snapshot**: Price stored is what user paid, not current course price
- **Cascade Delete**: If order deleted, all items deleted (unlikely, orders usually never deleted)

---

### 6. Course Ownership

**Purpose**: Represents user's access rights to a course (derived from completed orders)

**Table**: `course_ownership`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| ownership_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| user_id | UUID | NOT NULL, FOREIGN KEY → user(user_id) ON DELETE CASCADE | User reference |
| course_id | UUID | NOT NULL | Course reference |
| acquired_date | TIMESTAMP | NOT NULL | Date course was acquired (from order completion) |

**Indexes**:
- `idx_ownership_user_course` - Unique constraint (user_id, course_id) to prevent duplicate ownership
- `idx_ownership_user_id` - For "My Courses" page queries
- `idx_ownership_course_id` - For course enrollment count

**Validation Rules**:
- **Unique Ownership**: User can own a course only once
- **Acquired Date**: Timestamp when order status changed to 已完成

**Business Rules**:
- **Creation Trigger**: Created automatically when order status → 已完成
- **Lifetime Access**: Once created, ownership never expires (unless user account deleted)
- **Query Optimization**: Denormalized from order_item for fast "My Courses" queries

---

### 7. Achievement

**Purpose**: Represents user accomplishments (突破道館記錄) tracked by gamification system

**Table**: `achievement`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| achievement_id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier |
| user_id | UUID | NOT NULL, FOREIGN KEY → user(user_id) ON DELETE CASCADE | User reference |
| achievement_type | VARCHAR(100) | NOT NULL | Achievement category (e.g., "突破道館", "完成任務") |
| achievement_name | VARCHAR(255) | NOT NULL | Display name (e.g., "黑段道館挑戰者") |
| earned_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp when earned |

**Indexes**:
- `idx_achievement_user_id` - For user profile achievement list
- `idx_achievement_type` - For achievement category filtering
- `idx_achievement_earned_at` - For recent achievements queries

**Validation Rules**:
- **Achievement Type**: Required, category identifier
- **Achievement Name**: Required, display text in Chinese
- **Earned At**: Immutable once created

**Business Rules**:
- **Read-Only**: Member System displays achievements, does NOT create them (gamification system owns this)
- **Display**: Shown in user profile as list ordered by earned_at DESC
- **Future Integration**: Gamification system (p1 feature) will populate this table

---

## Relationships Summary

| Relationship | Cardinality | Constraints |
|--------------|-------------|-------------|
| User → OAuth Provider Links | 1:N | User can have multiple OAuth providers (Google + Facebook) |
| User → Third-Party Links | 1:N | User can link Discord and GitHub (max 1 per platform) |
| User → Orders | 1:N | User can have multiple orders |
| User → Course Ownership | 1:N | User can own multiple courses |
| User → Achievements | 1:N | User can earn multiple achievements |
| Order → Order Items | 1:N | Order contains multiple course line items |
| Order Item → Course Ownership | 1:1 | Each completed order item creates one ownership record |

---

## Database Migration Strategy

**Tool**: Flyway (versioned SQL migrations)

**Migration Order**:
1. `V1__create_users_table.sql` - User entity + pgcrypto extension
2. `V2__create_oauth_provider_links.sql` - OAuth provider links
3. `V3__create_third_party_links.sql` - Discord/GitHub links
4. `V4__create_orders_tables.sql` - Orders, order items, course ownership
5. `V5__create_achievements_table.sql` - Achievements (read-only)
6. `V6__create_indexes.sql` - Performance indexes
7. `V7__create_encryption_functions.sql` - pgcrypto helper functions

**Rollback Strategy**:
- Each migration includes corresponding `UNDO` script
- Test rollback in development before production deployment
- Database backups before applying migrations

---

## Data Integrity Rules

### Referential Integrity
- **ON DELETE CASCADE**: oauth_provider_link, third_party_link, course_ownership, achievement (if user deleted, these are meaningless)
- **ON DELETE RESTRICT**: order (prevent user deletion if unpaid orders exist)
- **Foreign Key Validation**: All FK constraints enforced at database level

### Data Consistency
- **Email Uniqueness**: Enforced via unique index on encrypted email hash (computed column)
- **OAuth Duplicate Prevention**: Unique index on (provider_type, provider_user_id)
- **Third-Party Duplicate Prevention**: Unique index on (platform_type, platform_user_id) AND (user_id, platform_type)
- **Course Ownership Uniqueness**: Unique index on (user_id, course_id)

### Audit Trail
- **created_at**: All entities have creation timestamp (immutable)
- **updated_at**: User, Order have last update timestamp (trigger-updated)
- **State History**: Order status transitions logged separately (future enhancement)

---

## Performance Considerations

### Query Optimization
- **User Lookup by Email**: Create hash index on pgp_sym_encrypt(email) for O(1) lookup
- **Order History**: Composite index (user_id, created_at DESC) for paginated queries
- **My Courses**: Separate course_ownership table avoids JOIN to orders (denormalized for performance)

### Caching Strategy
- **User Level**: Cache in Redis (key: `user:level:{user_id}`, TTL: 5 minutes) - Updated by gamification system
- **Session Data**: Redis session store (key: `session:{token}`, TTL: 7 days)
- **OAuth Provider Links**: Cache user's providers (key: `user:oauth:{user_id}`, TTL: 1 hour)

### Pagination
- **Order History**: Limit 10 orders per page, use `OFFSET` (acceptable for small datasets <100 orders per user)
- **Achievements**: Limit 20 per page, cursor-based pagination recommended if >100 achievements per user

---

## Security Considerations

### PII Encryption
- **Encrypted Fields**: email, birthday, location (using pgcrypto AES-256-GCM)
- **Key Management**: Encryption key in environment variable, rotatable via dual-key strategy
- **Decryption**: Application-layer AttributeConverter decrypts on read, encrypts on write

### Access Control
- **Row-Level Security**: Users can only query their own orders (enforced in application layer via user_id filter)
- **OAuth Provider Validation**: Verify OAuth state parameter to prevent CSRF
- **Third-Party Unlinking**: Require confirmation dialog + re-authentication for sensitive operations

### SQL Injection Prevention
- **Parameterized Queries**: All JPA queries use named parameters (`:userId`, `:email`)
- **No String Concatenation**: NEVER build SQL with `+` operator
- **Input Validation**: Validate GitHub URL format, gender enum values before database insert
