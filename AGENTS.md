# Agent Notes (webtruyen)

This repo is a Spring Boot (v4.x) backend for a "web truyện" app: auth (JWT), genres, stories (`truyen`), chapters, comments, favorites, follows, ratings, reading history, plus OTP via email.

## Quick Start (Windows / PowerShell)

Prereqs:
- Java 17+ (project sets `java.version=17` in `pom.xml`; newer JDKs usually work).
- MySQL running locally.

Run:
```powershell
cd "D:\Tieng trung\webtruyen"
.\mvnw.cmd -q spring-boot:run
```

Service:
- App: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

Tests:
```powershell
.\mvnw.cmd test
```

## Database (MySQL)

Default config lives in `src/main/resources/application.properties`:
- JDBC URL: `jdbc:mysql://localhost:3306/Web_Truyen?...`
- Username/password: `root` / `root`
- `spring.jpa.hibernate.ddl-auto=none` (schema is not generated from entities)
- SQL init: `spring.sql.init.mode=always` and `spring.sql.init.continue-on-error=true`

Boot-time SQL:
- `src/main/resources/schema.sql` creates tables (mostly `CREATE TABLE IF NOT EXISTS`) and includes some "patch" `ALTER/UPDATE` statements.
- `src/main/resources/data.sql` seeds users/genres/stories/etc using `INSERT IGNORE` to stay re-runnable.

You must create the database itself once:
```sql
CREATE DATABASE Web_Truyen;
```

When editing `schema.sql`, keep it idempotent (safe to run repeatedly) because the app runs it on every boot and will ignore SQL init errors.

## Auth / Security

JWT:
- `Authorization: Bearer <token>`
- Secret: `app.jwt.secret` (base64) in `application.properties`
- Expiration: `app.jwt.expiration-ms` (default 1 day)

Public endpoints (see `SecurityConfig`):
- `/api/auth/**`
- `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`
- `GET /api/**` is generally public
- `POST /api/truyen/*/increment-view` is public

Protected endpoints (examples):
- `/api/admin/**` requires `ROLE_ADMIN`
- `POST|PUT|DELETE /api/genres/**` requires `ROLE_ADMIN`
- `POST|PUT|DELETE /api/users/**` requires `ROLE_ADMIN`
- `/api/favorites/**`, `/api/follows/**`, `/api/reading-history/**` require authentication

Seeded dev users are in `data.sql`. Two convenient ones for API testing:
- `postman_user` / `123456`
- `postman_admin` / `123456`

`AuthController` contains a legacy fallback: if password authentication fails, it will accept matching plain-text DB rows and then upgrade them to BCrypt on successful login.

## Email + OTP (Password Reset)

Email uses Gmail SMTP. Configure via environment variables (recommended) so secrets do not land in git:
- `MAIL_USERNAME` (gmail address)
- `MAIL_PASSWORD` (Gmail App Password, not your normal password)
- `OTP_PEPPER` (random secret used for OTP hashing)

Example (PowerShell):
```powershell
$env:MAIL_USERNAME="your_gmail@gmail.com"
$env:MAIL_PASSWORD="your_gmail_app_password"
$env:OTP_PEPPER="any-random-secret"
```

If these are unset, OTP flows will likely fail (email can't be sent), and `OTP_PEPPER` will fall back to `CHANGE_ME_TO_A_RANDOM_SECRET`.

## CORS

`WebConfig` currently allows all origins/headers/methods (`allowedOrigins("*")`). Keep this for local dev; tighten before production.

## Common Dev Warnings

- Thymeleaf template location warning can happen if `src/main/resources/templates/` is empty (Maven may not copy empty dirs into `target/classes`). Either add at least one template file or set `spring.thymeleaf.check-template-location=false` if the app is API-only.
- Hibernate warns that explicitly setting `hibernate.dialect` is unnecessary for MySQL; safe to remove if you want cleaner logs.
- `spring.jpa.open-in-view` warning is Spring's default; decide explicitly if you want it on/off.

