# Cloud & DevOps Event – Registration App

A small Java web app where attendees register for a Cloud & DevOps learning event.
After registering, the user sees a success banner and can open a list of everyone registered.

## Features
- Responsive, dark "glass" UI (`webapp/src/main/webapp/index.html`)
- Server-side validation (name, mobile, email, password) and duplicate-email detection
- Public attendee list with masked email/mobile numbers
- Multi-stage Docker build, non-root Tomcat runtime, health check
- Jenkins CI/CD pipeline (build, test, SonarQube, Docker push, Trivy, GitOps trigger)

## Project layout
| Path | Purpose |
|------|---------|
| `server/` | Registration logic (`RegistrationService`, validation, in-memory store) + unit tests |
| `webapp/` | WAR: `RegistrationServlet` (JSON API) and the static front end |
| `Dockerfile` | Builds the WAR with Maven, runs it on Tomcat 11 / JDK 17 |
| `Jenkinsfile` | CI/CD pipeline |

## API
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/webapp/api/register` | Form fields: `name`, `mobile`, `email`, `password`, `passwordRepeat`. Returns `201`, `400` (field errors) or `409` (duplicate email) |
| `GET` | `/webapp/api/registrations` | `{count, registrations[]}`, newest first, contact details masked |

## Run with Docker (no Java or Maven needed)
```bash
docker build -t register-app .
docker run -d --name register-app -p 8080:8080 register-app
```
Open http://localhost:8080/webapp/

Stop it with `docker rm -f register-app`.

## Build and test locally
Requires JDK 17+ and Maven 3.9+.
```bash
mvn clean package     # builds and runs tests; WAR at webapp/target/webapp.war
```

## Notes and limitations
- Registrations are kept **in memory** and are lost on restart. Swap `RegistrationService` for a database-backed store to persist them.
- Passwords are validated but **not stored**; there is no login yet.
- The app runs under the `/webapp` context path.
- The Jenkinsfile needs Jenkins credentials (`github`, `dockerhub`, `jenkins-sonarqube-token`, `JENKINS_API_TOKEN`) and a SonarQube server. None of that is needed to run locally.
