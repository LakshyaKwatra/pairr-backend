# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Pairr is a Spring Boot REST API for discovering and collaborating with like-minded people based on skills, proficiency, ratings, and availability. Target users: students, professionals, hobbyists, mentors/learners. Users register, declare skills with proficiency levels, set availability windows, and get ranked recommendations for compatible partners.

## MVP Status

**Built:** Auth, profile + skills, availability, recommendation engine, ratings/feedback system, 1:1 chat (REST + WebSocket), admin skill/category management
**Not yet built:** Timezone support, meeting link sharing
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
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Allowed frontend origin(s) for CORS |
| `PORT` | `8080` | Server port (auto-set by Railway in prod) |

## Deployment

**Production profile:** `application-prod.yml` activated via `SPRING_PROFILES_ACTIVE=prod` on Railway. Overrides DB connection (uses Railway's `PGHOST`/`PGPORT`/etc.), disables SQL logging, disables Swagger, exposes only `/actuator/health`.

**CORS:** Configured in `SecurityConfig` via `cors.allowed-origins` property (env var `CORS_ALLOWED_ORIGINS`). Same origins are shared with WebSocket config in `WebSocketConfig`. Default is `http://localhost:5173` (Vite dev server).

**CI/CD:** `.github/workflows/ci.yml` runs `./mvnw clean test` on push to `main` and PRs. Railway auto-deploys from GitHub.

## Architecture

### Authentication

Stateless JWT auth with role-based access control (USER, ADMIN). `JwtAuthenticationFilter` extracts Bearer tokens, validates them, caches user existence checks (Caffeine, 60min TTL), and sets the SecurityContext with UUID as principal and `ROLE_<role>` authority. Controllers access the current user via `@AuthenticationPrincipal UUID userId`. Tokens expire after 24h.

**Route security** (`SecurityConfig`): `/api/auth/**`, `/ws/**`, and `/actuator/health` are public, `/api/admin/**` requires ADMIN role, everything else requires authentication. CORS is configured via `CorsConfigurationSource` bean (origins from `cors.allowed-origins` property). WebSocket auth is handled separately by `JwtHandshakeInterceptor` (not the HTTP filter chain).

### Recommendation Engine (`core/recommendation/`)

This is the core feature — a multi-factor weighted scoring system:

1. **RecommendationService** validates the requester has the skill and availability, then fetches candidates via a single JPQL query joining users + user_skills + user_availability
2. **RecommendationEngine** selects top-N candidates using a min-heap PriorityQueue — O(n log k)
3. **ScoreCalculator** computes weighted scores: time overlap (50%) + proficiency similarity (25%) + skill rating similarity (15%) + user rating similarity (10%)
4. **TimeMatcher** uses a sweep-line algorithm for overlap/distance calculation between availability windows — O(n log n + m log n)

### Real-Time Chat (`auth/websocket/`, `controller/WebSocketChatController`)

Two-layer architecture: REST endpoints for history + WebSocket (STOMP) for real-time delivery.

- **WebSocket endpoint:** `/ws?token=<JWT>` — auth via `JwtHandshakeInterceptor` during HTTP upgrade
- **STOMP channel auth:** `WebSocketAuthChannelInterceptor` sets the Principal on STOMP CONNECT from session attributes
- **Message handler:** `WebSocketChatController` receives at `/app/chat.send`, persists via `ChatService`, pushes to both sender and recipient at `/user/queue/messages` via `SimpMessagingTemplate`
- **Conversation deduplication:** sorted UUID pairs ensure one conversation row per user pair

### Database & Migrations

Schema is managed by **Liquibase** (10 changesets in `src/main/resources/db/changelog/changes/`). Hibernate is set to `ddl-auto: validate` — it never modifies the schema. All entity IDs are UUIDs.

**Tables:** `users`, `categories`, `skills`, `user_skills` (unique on user_id+skill_id), `user_availability` (unique on user_id+day_type+start_time+end_time), `ratings` (unique on from_user_id+to_user_id+skill_id), `conversations` (unique on participant_1_id+participant_2_id), `messages`

### Code Patterns

- **DTOs as Java records** for request/response objects (`model/dto/`)
- **Entities** use Lombok `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (`model/entity/`)
- **Constructor injection** via `@RequiredArgsConstructor` everywhere
- **Global exception handling** via `@ControllerAdvice` in `GlobalExceptionHandler`
- **Mappers** in `mapper/` package with static `toResponse()`/`toEntity()` methods — no inline mapping in controllers/services
- **Availability is full-replace** — POST overwrites all existing availability for the user (not merge)
- **Bulk operations** in `UserSkillService` and `UserAvailabilityService`
- **Rating aggregation** — submitting a rating recalculates `UserSkill.rating` (per-skill avg) and `User.overallRating` (overall avg), which feed into recommendation scoring
- **`@EntityGraph`** used on repository queries to prevent N+1 (skills, user_skills, conversations, messages)

### API Documentation

Interactive API docs are available at `/swagger-ui.html` (springdoc-openapi). All REST endpoints are annotated with `@Tag`, `@Operation`, and `@Schema`. The Swagger UI includes a JWT "Authorize" button for testing authenticated endpoints. Config is in `OpenApiConfig.java`.

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
| `/api/ratings` | `RatingController` | Authenticated |
| `/api/chat/` | `ChatController` | Authenticated |
| `/ws` | `WebSocketChatController` | JWT via handshake interceptor |
