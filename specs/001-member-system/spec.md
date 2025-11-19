# Feature Specification: Member System

**Feature Branch**: `001-member-system`
**Created**: 2025-11-14
**Status**: Draft
**Input**: User description: "Member system with OAuth authentication (Google, Facebook), user profile management (nickname, email, level, gender, GitHub link, occupation, achievements, birthday, location), third-party account linking (Discord, GitHub via OAuth), and order history tracking (purchase history, order status, owned courses list)"

## Clarifications

### Session 2025-11-14

- Q: When a user registers with Google using email "user@example.com", then later tries to register with Facebook using the same email, what should happen? → A: Auto-merge accounts and link both OAuth providers to single user account
- Q: When a user's session expires and they're redirected to login, where should their unsaved profile edits be preserved during re-authentication? → A: Browser localStorage (survives tab/browser close, persists until login completes)
- Q: When a new user registers via OAuth (Google/Facebook), what should their initial nickname be set to? → A: Extract from OAuth provider name field, fallback to email prefix if name unavailable
- Q: Can a user be logged in from multiple devices/browsers at the same time (concurrent sessions)? → A: Yes, allow unlimited concurrent sessions (login from new device doesn't invalidate existing sessions)
- Q: When a new user registers, what is their initial level (Lv.) value? → A: Lv. 1 (standard starting point for gamification systems)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - OAuth Registration and Login (Priority: P1)

New users need to create accounts and returning users need to access their accounts using their existing Google or Facebook credentials without creating new passwords.

**Why this priority**: This is the foundation of the member system. Without authentication, no other features (profile management, order tracking, course access) can function. This is the minimum viable entry point to the platform.

**Independent Test**: Can be fully tested by attempting to register with Google/Facebook OAuth, then logging out and logging back in. Delivers immediate value by allowing users to access the platform securely.

**Acceptance Scenarios**:

1. **Given** a new user visits the platform, **When** they click "使用 Google 登入" (Login with Google), **Then** they are redirected to Google OAuth consent screen
2. **Given** user approves Google OAuth consent, **When** they are redirected back to the platform, **Then** a new account is created with nickname populated from Google name field (or email prefix if name unavailable), level set to Lv. 1, and they are logged in with session established
3. **Given** a new user visits the platform, **When** they click "使用 Facebook 登入" (Login with Facebook), **Then** they are redirected to Facebook OAuth consent screen
4. **Given** user approves Facebook OAuth consent, **When** they are redirected back, **Then** a new account is created and they are logged in
5. **Given** an existing user with Google account, **When** they click "使用 Google 登入", **Then** they are logged in directly without account creation
6. **Given** an existing user with Facebook account, **When** they click "使用 Facebook 登入", **Then** they are logged in directly without account creation
7. **Given** an existing user registered with Google (email: user@example.com), **When** they attempt to register with Facebook using the same email, **Then** the Facebook OAuth provider is automatically linked to their existing account and they are logged in
8. **Given** a user is already logged in on Device A, **When** they log in on Device B, **Then** both sessions remain active (concurrent sessions allowed)
9. **Given** a logged-in user, **When** they click logout, **Then** their session is terminated and they are redirected to login page
10. **Given** user session expires (after inactivity period), **When** they attempt to access protected page, **Then** they are redirected to login page

---

### User Story 2 - User Profile Management (Priority: P2)

Users need to view and update their personal information including nickname, email, gender, birthday, location, occupation, and professional links (GitHub). Users also need to view their gamification progress (level, achievements).

**Why this priority**: Profile management is essential for personalization and user engagement but depends on authentication. It's the second most critical feature as it allows users to customize their identity on the platform.

**Independent Test**: Can be tested by logging in, navigating to profile page, viewing current information, updating fields (nickname, gender, birthday, location, occupation, GitHub link), and verifying changes are saved and displayed correctly.

**Acceptance Scenarios**:

1. **Given** a logged-in user, **When** they navigate to "個人資料" (Profile) page, **Then** they see all their profile fields (nickname, email, gender, birthday, location, occupation, GitHub link, level, achievements)
2. **Given** user is on profile page, **When** they update their nickname and click "儲存" (Save), **Then** the nickname is updated and confirmation message "個人資料已更新" (Profile updated) is displayed
3. **Given** user updates gender field, **When** they save changes, **Then** gender is persisted and displayed correctly on subsequent page loads
4. **Given** user updates birthday (date picker), **When** they save, **Then** birthday is stored and displayed in format "YYYY-MM-DD"
5. **Given** user updates location field, **When** they save, **Then** location is persisted
6. **Given** user updates occupation field, **When** they save, **Then** occupation is persisted
7. **Given** user enters GitHub link (e.g., "https://github.com/username"), **When** they save, **Then** GitHub link is validated and stored
8. **Given** user views profile page, **When** page loads, **Then** their current level is displayed as "Lv. [number]" (e.g., "Lv. 15")
9. **Given** user views profile page, **When** page loads, **Then** their achievements (突破道館記錄) are displayed as a list
10. **Given** user leaves a required field empty, **When** they attempt to save, **Then** validation error message is displayed indicating which field is required
11. **Given** user is editing profile and session expires, **When** they log in again, **Then** their unsaved edits are restored from browser localStorage and displayed in the form

---

### User Story 3 - Third-Party Account Linking (Priority: P3)

Users need to link their Discord and GitHub accounts to their platform profile to enable social features (Discord activity sharing, GitHub profile verification) and unlock certain missions or achievements.

**Why this priority**: This enhances the user experience through social integration but is not critical for basic platform functionality. It enables advanced features like Discord progress sharing and GitHub-verified achievements.

**Independent Test**: Can be tested by logging in, navigating to account linking section, initiating Discord OAuth flow, completing authorization, and verifying Discord account is linked. Same process for GitHub. Can test unlinking accounts.

**Acceptance Scenarios**:

1. **Given** a logged-in user, **When** they navigate to "帳號綁定" (Account Linking) section in profile, **Then** they see options to link Discord and GitHub accounts with current status (linked/not linked)
2. **Given** user has not linked Discord, **When** they click "綁定 Discord" (Link Discord), **Then** they are redirected to Discord OAuth authorization page
3. **Given** user approves Discord OAuth, **When** they are redirected back, **Then** Discord account is linked, status shows "已綁定" (Linked), and Discord username is displayed
4. **Given** user has not linked GitHub, **When** they click "綁定 GitHub" (Link GitHub), **Then** they are redirected to GitHub OAuth authorization page
5. **Given** user approves GitHub OAuth, **When** they are redirected back, **Then** GitHub account is linked, status shows "已綁定" (Linked), and GitHub username is displayed
6. **Given** user has linked Discord account, **When** they click "解除綁定" (Unlink), **Then** confirmation dialog appears
7. **Given** user confirms unlinking, **When** action completes, **Then** Discord account is unlinked and status shows "未綁定" (Not Linked)
8. **Given** user has linked GitHub account, **When** they click "解除綁定" (Unlink) and confirm, **Then** GitHub account is unlinked
9. **Given** user tries to link Discord account already linked to another platform user, **When** OAuth completes, **Then** error message displays "此 Discord 帳號已被其他用戶綁定" (This Discord account is already linked to another user)
10. **Given** user tries to link GitHub account already linked to another platform user, **When** OAuth completes, **Then** error message displays "此 GitHub 帳號已被其他用戶綁定" (This GitHub account is already linked to another user)

---

### User Story 4 - Order History and Course Ownership Tracking (Priority: P2)

Users need to view their purchase history, track order statuses, and see which courses they own so they can access their purchased content and verify transactions.

**Why this priority**: This is critical for e-commerce functionality and user trust. Users need to see what they've purchased and access their owned courses. It's P2 because it depends on authentication but is essential for the business model.

**Independent Test**: Can be tested by logging in with an account that has purchase history, navigating to orders page, viewing order list, clicking on individual orders to see details, and verifying owned courses list is accurate.

**Acceptance Scenarios**:

1. **Given** a logged-in user with purchase history, **When** they navigate to "訂單紀錄" (Order History) page, **Then** they see a list of all their orders sorted by date (newest first)
2. **Given** user views order list, **When** page loads, **Then** each order displays: order number, order date, total amount, payment status (待付款/已付款/已完成/已取消)
3. **Given** user clicks on an order, **When** order detail page loads, **Then** they see: order items (course names, prices), subtotal, discount (if coupon used), total amount, payment method, order status
4. **Given** user has unpaid order (status: 待付款), **When** they view order details, **Then** they see a "前往付款" (Proceed to Payment) button
5. **Given** user has completed order (status: 已完成), **When** they view order details, **Then** they see list of courses included and can navigate to each course
6. **Given** a logged-in user, **When** they navigate to "我的課程" (My Courses) page, **Then** they see all courses they own (from completed orders)
7. **Given** user views "我的課程" page, **When** page loads, **Then** each owned course displays: course title, instructor, purchase date, and "開始學習" (Start Learning) button
8. **Given** user has no purchase history, **When** they navigate to "訂單紀錄" page, **Then** they see empty state message "您目前沒有任何訂單" (You currently have no orders)
9. **Given** user has no owned courses, **When** they navigate to "我的課程" page, **Then** they see empty state with link to course catalog
10. **Given** user views order history, **When** they have more than 10 orders, **Then** pagination is displayed with 10 orders per page

---

### User Story 5 - Experience Points and Level Progression (Priority: P2)

Users need to earn experience points (exp) by completing learning activities (watching videos to 95%, claiming achievements) and see their level progression with visual feedback showing progress toward the next level.

**Why this priority**: This gamification feature drives user engagement and creates a sense of progression. It's P2 because it enhances the learning experience but depends on basic member and course functionality being in place first.

**Independent Test**: Can be tested by creating a test user at level 1, watching a video to 95% completion and checking the checkbox to claim 200 exp, then claiming an achievement for 1000 exp, and verifying the level increases correctly with progress percentage updating in real-time.

**Acceptance Scenarios**:

1. **Given** a new user registers, **When** their account is created, **Then** they start at Lv. 1 with 0 exp
2. **Given** a user watches a video to 95% completion, **When** they click the completion checkbox, **Then** they earn 200 exp and see confirmation message
3. **Given** a user claims an achievement, **When** the claim completes, **Then** they earn 1000 exp and see confirmation message
4. **Given** a user earns exp, **When** their total exp crosses a level threshold, **Then** their level increases automatically (e.g., 200 exp → Lv. 2, 500 exp → Lv. 3)
5. **Given** a user views their profile, **When** page loads, **Then** they see their current level (Lv. 1-36), total exp, exp needed for next level, and progress percentage (0-100%)
6. **Given** a user at Lv. 1 (0 exp), **When** they earn 100 exp, **Then** progress shows 50% toward Lv. 2 (200 exp threshold)
7. **Given** a user at Lv. 1 (0 exp), **When** they earn 200 exp total, **Then** they level up to Lv. 2 automatically
8. **Given** a user at Lv. 3 (500 exp), **When** they earn 1000 more exp (total: 1500 exp), **Then** they level up to Lv. 4
9. **Given** a user at Lv. 36 (65000 exp), **When** they earn more exp, **Then** they remain at max level (Lv. 36) and progress shows 100%
10. **Given** a user views session info API response, **When** data loads, **Then** response includes: level, exp, exp_for_next_level, exp_progress_percentage
11. **Given** multiple users earn exp concurrently, **When** exp is awarded, **Then** each user's level calculation is accurate without race conditions
12. **Given** a user tries to claim video completion twice, **When** they click checkbox again, **Then** system prevents duplicate exp award for same video

---

### Edge Cases

- What happens when OAuth provider (Google/Facebook/Discord/GitHub) is temporarily unavailable? System should display user-friendly error message "登入服務暫時無法使用，請稍後再試" (Login service temporarily unavailable, please try again later) and allow retry.
- What happens when a user registered with Google tries to register again with Facebook using the same email? System should automatically link the Facebook OAuth provider to the existing user account and log them in (auto-merge). User should see both providers in their account settings.
- What happens when user tries to link an OAuth account that's already linked to their profile? System should display message "此帳號已綁定" (This account is already linked) and show current linked status.
- What happens when user enters invalid GitHub URL format? System should validate URL pattern (https://github.com/username) and display error "請輸入有效的 GitHub 網址" (Please enter a valid GitHub URL).
- What happens when user updates profile while another session is active (concurrent sessions allowed)? System should use last-write-wins strategy; most recent save overwrites previous changes. User should be notified if their changes may have overwritten updates from another session.
- What happens when user's email from OAuth provider changes? System should update email automatically on next login and notify user of the change.
- What happens when user tries to access order details for an order that doesn't belong to them? System should return 403 Forbidden and redirect to order history page.
- What happens when profile save operation fails due to network error? System should display error message "儲存失敗，請檢查網路連線" (Save failed, please check network connection) and preserve user's input for retry.
- What happens when user's session expires while they're editing profile? System should redirect to login page and preserve unsaved changes in browser localStorage to restore after re-login (data persists even if browser closes).
- What happens when a video completion checkbox is clicked but the API call fails? System should display error message "經驗值領取失敗，請稍後再試" (Exp claim failed, please try again later) and leave checkbox unchecked for retry.
- What happens when user earns enough exp to level up multiple levels at once (e.g., earning 10000 exp at Lv. 1)? System should calculate final level correctly based on total exp (would jump from Lv. 1 to Lv. 7 in this case).
- What happens when user's exp data becomes corrupted or negative? System should reset exp to 0 and level to 1, then log error for investigation.
- What happens when two requests to award exp for the same video arrive simultaneously (race condition)? System should use database transactions or idempotency checks to ensure exp is only awarded once per video completion.

## Requirements *(mandatory)*

### Functional Requirements

#### Authentication & Authorization

- **FR-001**: System MUST support user registration via Google OAuth 2.0
- **FR-002**: System MUST support user registration via Facebook OAuth 2.0
- **FR-003**: System MUST support user login via Google OAuth 2.0 for existing accounts
- **FR-004**: System MUST support user login via Facebook OAuth 2.0 for existing accounts
- **FR-005**: System MUST create secure user session after successful OAuth authentication
- **FR-005A**: System MUST support unlimited concurrent sessions (user can be logged in from multiple devices/browsers simultaneously without invalidating existing sessions)
- **FR-006**: System MUST support user logout functionality that terminates session
- **FR-007**: System MUST automatically expire user sessions after 7 days of inactivity
- **FR-008**: System MUST redirect unauthenticated users to login page when accessing protected resources
- **FR-009**: System MUST auto-merge accounts when OAuth email matches existing user: link new OAuth provider to existing account instead of creating duplicate

#### User Profile Management

- **FR-010**: System MUST store and display user nickname (required field)
- **FR-010A**: System MUST populate initial nickname from OAuth provider name field during registration; if name field unavailable, use email prefix (part before @) as fallback
- **FR-011**: System MUST store and display user email (populated from OAuth, required field)
- **FR-012**: System MUST allow users to update their nickname
- **FR-013**: System MUST store and display user gender (optional field with options: 男/女/其他/不透露)
- **FR-014**: System MUST allow users to update their gender
- **FR-015**: System MUST store and display user birthday (optional field, date format)
- **FR-016**: System MUST allow users to update their birthday via date picker
- **FR-017**: System MUST store and display user location (optional field, free text)
- **FR-018**: System MUST allow users to update their location
- **FR-019**: System MUST store and display user occupation (optional field, free text)
- **FR-020**: System MUST allow users to update their occupation
- **FR-021**: System MUST store and display user GitHub profile link (optional field, URL format)
- **FR-022**: System MUST validate GitHub URL format (https://github.com/username) before saving
- **FR-023**: System MUST allow users to update their GitHub profile link
- **FR-024**: System MUST display user level (Lv.) in format "Lv. [number]" (read-only, calculated from gamification system)
- **FR-024A**: System MUST initialize new user level to Lv. 1 upon registration
- **FR-025**: System MUST display user achievements (突破道館記錄) as a list (read-only, populated from gamification system)
- **FR-026**: System MUST persist profile updates within 1 second
- **FR-027**: System MUST display confirmation message "個人資料已更新" after successful profile save
- **FR-028**: System MUST display validation errors for invalid or missing required fields
- **FR-029**: System MUST preserve user input if save operation fails
- **FR-029A**: System MUST preserve unsaved profile edits in browser localStorage when session expires, and restore data after user re-authenticates

#### Third-Party Account Linking

- **FR-030**: System MUST support linking Discord account via Discord OAuth 2.0
- **FR-031**: System MUST support linking GitHub account via GitHub OAuth 2.0
- **FR-032**: System MUST display current Discord linking status (已綁定/未綁定) with username if linked
- **FR-033**: System MUST display current GitHub linking status (已綁定/未綁定) with username if linked
- **FR-034**: System MUST prevent linking Discord account already linked to another platform user
- **FR-035**: System MUST prevent linking GitHub account already linked to another platform user
- **FR-036**: System MUST support unlinking Discord account with confirmation dialog
- **FR-037**: System MUST support unlinking GitHub account with confirmation dialog
- **FR-038**: System MUST validate OAuth account ownership during linking process
- **FR-039**: System MUST display error message if OAuth linking fails

#### Order History & Course Ownership

- **FR-040**: System MUST display all user orders sorted by date (newest first)
- **FR-041**: System MUST display order summary: order number, date, total amount, payment status
- **FR-042**: System MUST support order status values: 待付款 (Pending), 已付款 (Paid), 已完成 (Completed), 已取消 (Cancelled)
- **FR-043**: System MUST display order details: items, subtotal, discount, total, payment method, status
- **FR-044**: System MUST display "前往付款" (Proceed to Payment) button for unpaid orders
- **FR-045**: System MUST display course access links for completed orders
- **FR-046**: System MUST display all courses owned by user (from completed orders)
- **FR-047**: System MUST display owned course information: title, instructor, purchase date
- **FR-048**: System MUST provide "開始學習" (Start Learning) button for each owned course
- **FR-049**: System MUST display empty state message when user has no orders
- **FR-050**: System MUST display empty state with catalog link when user has no owned courses
- **FR-051**: System MUST paginate order history (10 orders per page) when user has more than 10 orders
- **FR-052**: System MUST restrict order detail access to order owner only (authorization check)

#### Experience Points & Level System

- **FR-053**: System MUST initialize new users with 0 exp and level 1
- **FR-054**: System MUST store total experience points (exp) for each user
- **FR-055**: System MUST automatically calculate user level (1-36) based on total exp using the level threshold table from docs/student-exp.md
- **FR-056**: System MUST award 200 exp when user completes a video (watches to 95% and clicks completion checkbox)
- **FR-057**: System MUST award 1000 exp when user claims an achievement
- **FR-058**: System MUST prevent duplicate exp awards for the same video completion (idempotent operation)
- **FR-059**: System MUST prevent duplicate exp awards for the same achievement claim (idempotent operation)
- **FR-060**: System MUST calculate and display exp needed for next level
- **FR-061**: System MUST calculate and display progress percentage toward next level (0-100%)
- **FR-062**: System MUST display max level users (Lv. 36 with 65000+ exp) as 100% progress with no next level
- **FR-063**: System MUST handle multiple level-ups in a single exp award (e.g., earning 10000 exp at Lv. 1 should correctly level up to Lv. 7)
- **FR-064**: System MUST include exp data in session info API response: level, exp, exp_for_next_level, exp_progress_percentage
- **FR-065**: System MUST use database transactions to prevent race conditions when awarding exp concurrently
- **FR-066**: System MUST display confirmation message when exp is successfully awarded
- **FR-067**: System MUST display error message when exp award fails due to network or server error
- **FR-068**: System MUST log and handle corrupted exp data (negative or invalid values) by resetting to safe defaults (0 exp, Lv. 1)

### Key Entities

- **User**: Represents a platform member with profile information
  - Attributes: user_id (unique identifier), email (from OAuth), nickname (initialized from OAuth name or email prefix), gender, birthday, location, occupation, github_link, level (initialized to 1, auto-calculated from exp), exp (experience points, initialized to 0), created_at, updated_at
  - Relationships: Has many OAuth providers, has many third-party links, has many orders, owns many courses, has many video completions, has many achievement claims

- **OAuth Provider Link**: Represents connection between user and OAuth provider (Google/Facebook)
  - Attributes: provider_link_id, user_id, provider_type (Google/Facebook), provider_user_id, provider_email, linked_at
  - Relationships: Belongs to user

- **Third-Party Account Link**: Represents connection to Discord or GitHub
  - Attributes: link_id, user_id, platform_type (Discord/GitHub), platform_user_id, platform_username, linked_at
  - Relationships: Belongs to user

- **Order**: Represents a purchase transaction
  - Attributes: order_id, user_id, order_number, total_amount, payment_status (待付款/已付款/已完成/已取消), payment_method, coupon_id, created_at, updated_at
  - Relationships: Belongs to user, has many order items

- **Order Item**: Represents individual course in an order
  - Attributes: item_id, order_id, course_id, price, purchased_at
  - Relationships: Belongs to order, references course

- **Course Ownership**: Represents user's access to a course
  - Attributes: ownership_id, user_id, course_id, acquired_date (from order completion)
  - Relationships: Belongs to user, references course

- **Achievement**: Represents user accomplishments (突破道館記錄)
  - Attributes: achievement_id, user_id, achievement_type, achievement_name, earned_at, exp_awarded (1000 exp per achievement)
  - Relationships: Belongs to user

- **Video Completion**: Represents user's completion of a video for exp reward
  - Attributes: completion_id, user_id, video_id, completed_at, exp_awarded (200 exp per video)
  - Relationships: Belongs to user, references video

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete OAuth registration (Google or Facebook) in under 30 seconds from clicking login button to account creation
- **SC-002**: Users can complete OAuth login in under 15 seconds from clicking login button to authenticated session
- **SC-003**: Profile update operations complete in under 1 second with confirmation displayed
- **SC-004**: 95% of OAuth authentication attempts succeed on first try (excluding user cancellation)
- **SC-005**: Third-party account linking (Discord/GitHub) completes in under 45 seconds including OAuth flow
- **SC-006**: Order history page loads and displays all orders in under 2 seconds for users with up to 100 orders
- **SC-007**: "我的課程" (My Courses) page loads and displays all owned courses in under 2 seconds for users with up to 50 courses
- **SC-008**: System supports 500 concurrent authenticated users without session management degradation
- **SC-009**: Zero successful unauthorized access attempts to other users' order details or profile data
- **SC-010**: 90% of users successfully complete profile setup (add nickname and at least one optional field) within first login session
- **SC-011**: Session expiration and re-authentication flow completes in under 20 seconds when user returns to platform
- **SC-012**: All Chinese UI labels and messages display correctly without encoding issues
- **SC-013**: Profile validation errors are displayed within 500ms of user input with clear Chinese error messages
- **SC-014**: System maintains 99.9% uptime for authentication services (excluding scheduled maintenance)
- **SC-015**: Order history pagination responds in under 500ms when navigating between pages
- **SC-016**: Exp award operations (video completion, achievement claim) complete in under 1 second with confirmation displayed
- **SC-017**: Level calculation from exp is accurate 100% of the time (no incorrect level assignments)
- **SC-018**: Zero duplicate exp awards for the same video or achievement (idempotent operations)
- **SC-019**: Users can see their exp progress update in real-time (within 500ms of earning exp)
- **SC-020**: System handles 1000 concurrent exp award requests without race conditions or data corruption
- **SC-021**: 95% of users who earn exp see level-up notification within first 3 actions if they cross level threshold
- **SC-022**: Session info API includes accurate exp data (level, exp, exp_for_next_level, exp_progress_percentage) in under 500ms
