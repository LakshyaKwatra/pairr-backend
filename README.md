# Pairr

A skill-based partner matching API. Discover and collaborate with like-minded people based on skills, proficiency levels, ratings, and availability.

**Target users:** Students, professionals, hobbyists, mentors, and learners.

## Core User Flows

1. **Register & set up profile** — sign up with email, declare skills with proficiency levels, set availability windows (weekday/weekend)
2. **Discover partners** — search by skill, proficiency range, and availability overlap; system returns ranked recommendations
3. **Communicate** — start a 1:1 chat or share an external meeting link *(planned)*
4. **Rate & feedback** — rate users on a skill (1–5 + optional text); ratings aggregate into per-skill and overall scores, feeding back into recommendation ranking

## Tech Stack

| Component | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.5.10 |
| Database | PostgreSQL 16 |
| Auth | JWT (stateless, role-based) |
| Migrations | Liquibase |
| Caching | Caffeine |
| Build | Maven (wrapper included) |
| Containers | Docker Compose (PostgreSQL) |

## Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose

### Run

```bash
# Start PostgreSQL
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. A default admin account is created on first startup:
- **Email:** `admin@pairr.com`
- **Password:** `admin123`

### Build & Test

```bash
./mvnw clean package              # Build
./mvnw test                       # Run all tests
./mvnw test -Dtest=ClassName      # Run a single test class
```

## API Overview

### Public

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user (returns JWT) |
| POST | `/api/auth/login` | Login (returns JWT) |

### Authenticated (Bearer token required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/categories` | List all skill categories |
| GET | `/api/skills` | List all skills |
| POST | `/api/user/skills` | Add skills to your profile (bulk) |
| GET | `/api/user/skills` | Get your skills |
| POST | `/api/user/availability` | Set availability windows (full replace) |
| GET | `/api/user/availability` | Get your availability |
| GET | `/api/recommendations?skillId=&dayType=&numberOfRecommendations=` | Get partner recommendations |
| POST | `/api/ratings` | Rate a user on a skill (1–5 + optional feedback) |
| GET | `/api/ratings?userId=` | Get all ratings for a user |
| GET | `/api/ratings?userId=&skillId=` | Get ratings for a user on a specific skill |

### Admin (ADMIN role required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/categories` | Create a skill category |
| POST | `/api/admin/skills` | Create a skill under a category |
| GET | `/api/admin/users` | List all users |
| GET | `/api/admin/dashboard` | Admin dashboard |

## How Recommendations Work

The recommendation engine uses rule-based weighted scoring (no ML):

| Factor | Weight | Description |
|---|---|---|
| Availability overlap | 50% | Sweep-line algorithm computes time overlap between windows |
| Proficiency similarity | 25% | Closer proficiency levels score higher |
| Skill rating | 15% | Peer-rated skill score similarity (from ratings system) |
| Overall user rating | 10% | Aggregate rating similarity (from ratings system) |

Top-N results are selected using a min-heap for efficient O(n log k) ranking.

### How Ratings Feed Into Recommendations

When a user submits a rating, the system recalculates:
- **Per-skill rating** (`UserSkill.rating`) — average of all ratings received for that user+skill
- **Overall rating** (`User.overallRating`) — average of all ratings received across all skills

These aggregated values are used by the recommendation engine's scoring formula above. Users with no ratings yet contribute 0 to the rating components.

## Configuration

Key environment variables (with defaults for local dev):

| Variable | Default | Purpose |
|---|---|---|
| `JWT_SECRET` | Dev key (change in prod) | JWT signing key |
| `ADMIN_EMAIL` | `admin@pairr.com` | Default admin email |
| `ADMIN_PASSWORD` | `admin123` | Default admin password |

## MVP Scope

### Built
- User authentication (register/login with JWT)
- Profile with skills and proficiency levels (BEGINNER, AMATEUR, INTERMEDIATE, EXPERT)
- Availability windows (weekday/weekend with time ranges)
- Recommendation engine with weighted scoring
- Ratings and feedback system (1–5 per skill, aggregated into per-skill and overall scores)
- Admin-managed skill categories (prevents user-generated chaos)
- Role-based access control (USER / ADMIN)

### Planned (not yet built)
- 1:1 real-time chat via WebSockets with stored message history
- Timezone support
- Meeting link sharing (Google Meet / Zoom — paste only, no generation)

### Post-MVP
- Group chats, video calls
- Redis for online status
- ElasticSearch for skill search
- AI/ML-powered recommendations
- Notifications, payments
