# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Pairr is a Spring Boot REST API for discovering and collaborating with like-minded people based on skills, proficiency, ratings, and availability. Target users: students, professionals, hobbyists, mentors/learners. Users register, declare skills with proficiency levels, set availability windows, and get ranked recommendations for compatible partners.

## MVP Status

**Built:** Auth, profile + skills, availability, recommendation engine, admin skill/category management
**Not yet built:** Chat (1:1 real-time messaging via WebSockets with stored history), ratings/feedback system, timezone support, meeting link sharing
**Excluded from MVP:** Group chats, video calls, skill verification, AI/ML recommendations, notifications, payments

**Package:** `com.connect.pairr` | **Java 17** | **Spring Boot 3.5.10** | **PostgreSQL 16**

## Build & Run Commands

```bash
docker-compose up -d              # Start PostgreSQL (port 5432, db/user/pass: pairr)
./mvnw spring-boot:run            # Run the application
./mvnw clean package              # Build
./mvnw test                       # Run tests
./mvnw test -Dtest=ClassName      # Run a single test class
./mvnw test -Dtest=ClassName#method  # Run a single test method
```

A default admin account (`admin@pairr.com` / `admin123`) is auto-created on startup via `AdminInitializer`.

## Key Environment Variables

| Variable | Default | Purpose |
|---|---|---|
| `JWT_SECRET` | hardcoded dev key | JWT HMAC-SHA signing key |
| `ADMIN_EMAIL` | `admin@pairr.com` | Default admin email |
| `ADMIN_PASSWORD` | `admin123` | Default admin password |

## Architecture

### Authentication

Stateless JWT auth with role-based access control (USER, ADMIN). `JwtAuthenticationFilter` extracts Bearer tokens, validates them, caches user existence checks (Caffeine, 60min TTL), and sets the SecurityContext with UUID as principal and `ROLE_<role>` authority. Controllers access the current user via `@AuthenticationPrincipal UUID userId`. Tokens expire after 24h.

**Route security** (`SecurityConfig`): `/api/auth/**` is public, `/api/admin/*` requires ADMIN role, everything else requires authentication.

### Recommendation Engine (`core/recommendation/`)

This is the core feature — a multi-factor weighted scoring system:

1. **RecommendationService** validates the requester has the skill and availability, then fetches candidates via a single JPQL query joining users + user_skills + user_availability
2. **RecommendationEngine** selects top-N candidates using a min-heap PriorityQueue — O(n log k)
3. **ScoreCalculator** computes weighted scores: time overlap (50%) + proficiency similarity (25%) + skill rating similarity (15%) + user rating similarity (10%)
4. **TimeMatcher** uses a sweep-line algorithm for overlap/distance calculation between availability windows — O(n log n + m log n)

### Database & Migrations

Schema is managed by **Liquibase** (8 changesets in `src/main/resources/db/changelog/changes/`). Hibernate is set to `ddl-auto: validate` — it never modifies the schema. All entity IDs are UUIDs.

**Tables:** `users`, `categories`, `skills`, `user_skills` (unique on user_id+skill_id), `user_availability` (unique on user_id+day_type+start_time+end_time)

### Code Patterns

- **DTOs as Java records** for request/response objects (`model/dto/`)
- **Entities** use Lombok `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (`model/entity/`)
- **Constructor injection** via `@RequiredArgsConstructor` everywhere
- **Global exception handling** via `@ControllerAdvice` in `GlobalExceptionHandler`
- **Interval merging** in `UserAvailabilityService` prevents overlapping availability windows
- **Bulk operations** in `UserSkillService` and `UserAvailabilityService`

### API Structure

| Prefix | Controller | Auth |
|---|---|---|
| `/api/auth/` | `AuthController` | Public |
| `/api/admin/` | `AdminController` | ADMIN role |
| `/api/categories` | `CategoryController` | Authenticated |
| `/api/skills` | `SkillController` | Authenticated |
| `/api/user/skills` | `UserSkillController` | Authenticated |
| `/api/user/availability` | `UserAvailabilityController` | Authenticated |
| `/api/recommendations` | `RecommendationController` | Authenticated |
| `/api/test/` | `TestDataController` | Authenticated (dev/test only) |
