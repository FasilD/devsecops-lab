# Spring Boot DevSecOps Security Lab

A hands-on DevSecOps lab built around a Spring Boot payment REST API.

The project demonstrates how application security controls, automated testing, static analysis, software composition analysis, secret scanning, container hardening, vulnerability scanning, dynamic application security testing, and CI security gates can be integrated into the software development lifecycle.

---

## Project Overview

This project contains a secured payment REST API developed with Spring Boot and Java 21.

The application supports payment creation, retrieval, update, and deletion while applying authentication, role-based authorization, input validation, structured error handling, automated testing, and container security controls.

The project also implements a DevSecOps workflow that automatically builds, tests, scans, packages, starts, and validates the application using GitHub Actions, including baseline and authenticated OpenAPI-driven DAST.

The lab demonstrates the security feedback cycle:

```text
Build
  |
  v
Test
  |
  v
Scan
  |
  v
Identify Risk
  |
  v
Remediate
  |
  v
Rebuild
  |
  v
Verify
```

---

## Objectives

The main objectives of this lab are to:

- Build a functional Spring Boot REST API.
- Apply secure coding practices.
- Implement authentication and role-based access control.
- Manage application credentials through environment variables.
- Run automated application and authorization tests.
- Perform Static Application Security Testing.
- Detect exposed secrets in source code and Git history.
- Analyze third-party dependencies for known vulnerabilities.
- Build and harden a Docker container.
- Scan the final container image for vulnerabilities.
- Start and validate the application inside the CI pipeline.
- Perform baseline and authenticated API Dynamic Application Security Testing.
- Automate security checks using GitHub Actions.
- Enforce security gates before application artifacts are accepted.

---

## Application Features

The application provides a payment management API with the following capabilities:

- Create a payment.
- Retrieve all payments.
- Retrieve a payment by ID.
- Update an existing payment.
- Delete a payment.
- Validate incoming requests.
- Return structured API error responses.
- Store development data in an H2 database.
- Expose an application health endpoint.
- Restrict sensitive operations based on user roles.

---

## Technology Stack

| Component | Technology |
|---|---|
| Programming language | Java 21 |
| Application framework | Spring Boot |
| Embedded server | Apache Tomcat |
| Build tool | Maven Wrapper |
| Database | H2 |
| Authentication | HTTP Basic |
| Authorization | Spring Security RBAC |
| Password hashing | BCrypt |
| Testing | JUnit and MockMvc |
| Containerization | Docker |
| Container orchestration | Docker Compose |
| CI platform | GitHub Actions |
| SAST | Semgrep |
| Secret scanning | Gitleaks |
| Software composition analysis | OWASP Dependency-Check |
| Container scanning | Trivy |
| DAST | OWASP ZAP Baseline and Authenticated API Scans |

---

## Architecture

```text
Client
  |
  | HTTP Basic Authentication
  v
Spring Security
  |
  | Authentication and authorization
  v
Payment REST Controller
  |
  | Request validation
  v
Payment Service
  |
  | Business logic
  v
Payment Repository
  |
  v
H2 Database
```

The application is packaged as a Spring Boot JAR and deployed inside a hardened Docker container.

```text
Source Code
    |
    v
Maven Build and Tests
    |
    v
Dependency and Source Scanning
    |
    v
Secret Scanning
    |
    v
Docker Image Build
    |
    v
Container Vulnerability Scan
    |
    v
Start Application Container
    |
    v
OWASP ZAP Baseline DAST
    |
    v
OpenAPI Specification Retrieval
    |
    v
Authenticated OWASP ZAP API Scan
    |
    v
Authenticated ZAP Security Gate
    |
    v
Security Reports and Artifacts
```

---

## Security Controls

### Authentication

The API uses HTTP Basic authentication.

Two users are configured:

| Username | Role |
|---|---|
| `merchant-user` | USER |
| `admin-user` | ADMIN |

Passwords are supplied through environment variables and are not stored directly in the source code.

### Authorization

Authenticated users can access standard payment operations.

Deleting a payment requires the `ADMIN` role.

The authorization model is:

```text
USER
  ├── Retrieve payments
  ├── Create payments
  └── Update payments

ADMIN
  ├── Retrieve payments
  ├── Create payments
  ├── Update payments
  └── Delete payments
```

### Password Handling

Passwords are processed using BCrypt.

The application does not contain hardcoded fallback passwords. Required credentials must be supplied through environment variables.

### Environment-Based Credentials

The application requires:

```bash
MERCHANT_USER_PASSWORD
ADMIN_USER_PASSWORD
```

These values are provided separately for local development, Docker execution, automated testing, and CI runs.

Real credentials must not be committed to Git.

### Input Validation

Incoming payment requests are validated before business logic is executed.

Invalid requests return structured JSON validation responses rather than uncontrolled framework exceptions.

### Error Handling

The application uses centralized exception handling to provide:

- Consistent JSON responses.
- Appropriate HTTP status codes.
- Predictable client behavior.
- Reduced accidental exception-detail exposure.

### Health Endpoint

The following endpoint is publicly accessible:

```text
GET /actuator/health
```

This allows infrastructure, containers, and the CI pipeline to verify whether the application is available.

Business API endpoints require authentication.

### CSRF

CSRF protection is disabled because the application is implemented as a REST API using HTTP Basic authentication rather than browser session-based form authentication.

This configuration is specific to the lab architecture and should be reassessed for applications using browser sessions or cookies for authentication.

---

## API Endpoints

| Method | Endpoint | Required access | Description |
|---|---|---|---|
| GET | `/api/payments` | USER or ADMIN | Retrieve all payments |
| GET | `/api/payments/{id}` | USER or ADMIN | Retrieve a payment by ID |
| POST | `/api/payments` | USER or ADMIN | Create a payment |
| PUT | `/api/payments/{id}` | USER or ADMIN | Update a payment |
| DELETE | `/api/payments/{id}` | ADMIN | Delete a payment |
| GET | `/actuator/health` | Public | Application health check |
| GET | `/v3/api-docs` | USER or ADMIN | Generated OpenAPI JSON |

---

## Prerequisites

Install the following tools:

- Java 21
- Git
- Docker
- Docker Compose

The project includes the Maven Wrapper, so a separate Maven installation is not required.

Verify the installed tools:

```bash
java -version
git --version
docker --version
docker compose version
```

---

## Running the Application Locally

Set the required environment variables:

```bash
export MERCHANT_USER_PASSWORD='<MERCHANT_PASSWORD>'
export ADMIN_USER_PASSWORD='<ADMIN_PASSWORD>'
```

Run the application:

```bash
./mvnw spring-boot:run
```

Check the health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Retrieve payments as the merchant user:

```bash
curl -u merchant-user:<MERCHANT_PASSWORD> \
  http://localhost:8080/api/payments
```

Retrieve payments as the administrator:

```bash
curl -u admin-user:<ADMIN_PASSWORD> \
  http://localhost:8080/api/payments
```

---

## Running Tests

Set test credentials:

```bash
export MERCHANT_USER_PASSWORD='<MERCHANT_PASSWORD>'
export ADMIN_USER_PASSWORD='<ADMIN_PASSWORD>'
```

Run the automated tests:

```bash
./mvnw clean test
```

The test suite validates areas including:

- Public health endpoint access.
- Authentication requirements.
- Authorized USER access.
- Authorized ADMIN access.
- Payment retrieval.
- Payment creation.
- Payment updates.
- Payment deletion restrictions.
- Request validation.
- Role-based authorization.

---

## Building the Application

Build the Spring Boot JAR:

```bash
./mvnw clean package
```

The generated artifact is placed under:

```text
target/
```

---

## Docker Image

The final image uses an Alpine-based Java runtime.

The Dockerfile applies the following security controls:

- Minimal Java Runtime Environment.
- Alpine Linux base image.
- Package upgrades during the image build.
- Dedicated application group.
- Dedicated non-root user.
- Explicit application working directory.
- Controlled JAR ownership.
- Application execution as an unprivileged user.
- No compiler or development tooling in the runtime image.

Build the image:

```bash
docker build -t devsecops-lab:local .
```

Run the image:

```bash
docker run --rm \
  -p 8080:8080 \
  -e MERCHANT_USER_PASSWORD='<MERCHANT_PASSWORD>' \
  -e ADMIN_USER_PASSWORD='<ADMIN_PASSWORD>' \
  devsecops-lab:local
```

---

## Docker Compose

Start the application:

```bash
MERCHANT_USER_PASSWORD='<MERCHANT_PASSWORD>' \
ADMIN_USER_PASSWORD='<ADMIN_PASSWORD>' \
docker compose up -d
```

Check the service:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f
```

Stop the application:

```bash
docker compose down
```

When Docker Compose is executed with `sudo`, exported shell variables may not be preserved.

Use:

```bash
sudo MERCHANT_USER_PASSWORD='<MERCHANT_PASSWORD>' \
     ADMIN_USER_PASSWORD='<ADMIN_PASSWORD>' \
     docker compose up -d
```

---

## DevSecOps Pipeline

The GitHub Actions workflow is located at:

```text
.github/workflows/devsecops.yml
```

The pipeline is triggered by:

- Pushes to `main`.
- Pull requests targeting `main`.
- Manual workflow execution.

The pipeline performs:

```text
Checkout Repository
        |
        v
Set Up Java 21
        |
        v
Run Maven Tests
        |
        v
Build Spring Boot JAR
        |
        v
OWASP Dependency-Check
        |
        v
Semgrep SAST
        |
        v
Gitleaks Secret Scan
        |
        v
Build Hardened Docker Image
        |
        v
Trivy Container Scan
        |
        v
Enforce Container Security Gate
        |
        v
Start Application Container
        |
        v
Verify Health Endpoint
        |
        v
OWASP ZAP Baseline DAST
        |
        v
Download OpenAPI Specification
        |
        v
Authenticated OWASP ZAP API Scan
        |
        v
Enforce Authenticated ZAP Security Gate
        |
        v
Upload JAR and Security Reports
```

---

## Security Testing

### Semgrep SAST

Semgrep is used for Static Application Security Testing.

It analyzes source code, configuration files, shell commands, YAML files, and the Dockerfile for insecure patterns.

Example scan:

```bash
docker run --rm \
  -v "$PWD:/src" \
  semgrep/semgrep:latest \
  semgrep scan \
  --config auto \
  --json \
  --output /src/reports/semgrep/semgrep.json \
  /src
```

An initial Semgrep scan identified that the Docker container did not explicitly configure a non-root user.

The issue was remediated by:

- Creating `appgroup`.
- Creating `appuser`.
- Assigning ownership of the application JAR.
- Adding `USER appuser` to the Dockerfile.

The follow-up scan confirmed that the missing-user issue had been resolved.

### Gitleaks Secret Scanning

Gitleaks scans both the working repository and Git history for exposed secrets.

It searches for patterns associated with:

- Passwords.
- API keys.
- Access tokens.
- Cloud credentials.
- Private keys.
- Database credentials.
- Service credentials.

Example scan:

```bash
docker run --rm \
  -v "$PWD:/repo" \
  ghcr.io/gitleaks/gitleaks:latest \
  git /repo \
  --report-path=/repo/reports/gitleaks/gitleaks.json \
  --report-format=json \
  --no-banner
```

The completed scan did not identify committed secrets.

Application passwords remain externalized through environment variables.

### OWASP Dependency-Check

OWASP Dependency-Check performs Software Composition Analysis.

It analyzes third-party application dependencies and attempts to identify known vulnerabilities associated with those components.

The CI pipeline runs:

```bash
./mvnw --batch-mode \
  org.owasp:dependency-check-maven:check \
  -Dformat=JSON \
  -Dodc.outputDirectory=reports/dependency-check \
  -DfailBuildOnCVSS=8 \
  -DfailOnError=true
```

The dependency security gate fails when a detected dependency vulnerability has a CVSS score of 8.0 or higher.

The CI workflow supplies an NVD API key through the `NVD_API_KEY` GitHub Actions repository secret. The key is read from an environment variable and is not committed to the repository.

The Dependency-Check data directory is cached between workflow runs to reduce repeated NVD downloads and lower the likelihood of API rate limiting.


#### Dependency Vulnerability Detected

During the first GitHub Actions execution, Dependency-Check detected multiple critical vulnerabilities in:

```text
tomcat-embed-core 11.0.22
```

The findings included CVEs with a CVSS score of 9.1.

Because the configured security threshold was 8.0, the pipeline correctly failed.

#### Remediation

The embedded Tomcat version was upgraded to a patched release through the Maven configuration.

After the upgrade:

- Maven tests passed.
- The application built successfully.
- OWASP Dependency-Check passed.
- The GitHub Actions pipeline returned to a green state.

This demonstrates that the dependency security gate is actively enforcing vulnerability requirements rather than only generating informational reports.

### Trivy Container Scanning

Trivy scans the final Docker image for vulnerabilities in:

- Operating system packages.
- Java application dependencies.
- Installed runtime components.
- Known CVEs.

Example scan:

```bash
docker run --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$PWD/reports/trivy:/reports" \
  aquasec/trivy:0.72.0 \
  image \
  --scanners vuln \
  --format json \
  --output /reports/trivy-image.json \
  devsecops-lab:local
```

#### Initial Ubuntu-Based Image

The first Java runtime image was based on Ubuntu and contained multiple HIGH-severity vulnerabilities.

#### Alpine Migration

The runtime image was changed to an Alpine-based Java image.

The first Alpine scan still detected HIGH-severity vulnerabilities in system packages.

#### Package Upgrade Remediation

The Dockerfile was updated to include:

```dockerfile
RUN apk upgrade --no-cache
```

The image was rebuilt and rescanned.

The final hardened image returned:

```text
0 HIGH vulnerabilities
0 CRITICAL vulnerabilities
```

The CI pipeline separately parses the Trivy JSON report and fails when HIGH or CRITICAL vulnerabilities are detected.

### OWASP ZAP Baseline DAST

OWASP ZAP is used for baseline Dynamic Application Security Testing.

The CI pipeline:

1. Builds the Docker image.
2. Starts the application container.
3. Binds the application to the GitHub runner loopback interface.
4. Waits for the health endpoint to become available.
5. Runs an OWASP ZAP baseline scan.
6. Generates JSON, HTML, and Markdown reports.
7. Uploads the reports as GitHub Actions artifacts.
8. Stops and removes the application container.

The initial ZAP scan targets:

```text
http://127.0.0.1:8080/actuator/health
```

The baseline scan performs passive analysis and validates that ZAP is correctly integrated into the pipeline.

The initial OWASP ZAP baseline scan produced:

- High: 0
- Medium: 1
- Low: 2
- Informational: 2
- False positives: 0

The alerts were:

| Alert | Risk | Instances |
|---|---:|---:|
| Weak Authentication Method | Medium | 4 |
| Cookie without SameSite Attribute | Low | 4 |
| Cross-Origin-Resource-Policy Header Missing or Invalid | Low | 1 |
| Non-Storable Content | Informational | 5 |
| Session Management Response Identified | Informational | 3 |

The Medium finding was `Weak Authentication Method`, caused by the intentional use of HTTP Basic authentication in the educational lab.

The finding is not classified as a false positive. It is documented as an accepted lab limitation.

The baseline scan primarily validates the public health endpoint. The pipeline also performs a separate authenticated API scan against the protected payment endpoints by importing the generated OpenAPI specification.

### Authenticated OWASP ZAP API Scan

The CI pipeline performs an authenticated API security scan after the application becomes healthy.

The authenticated scan:

1. Downloads the generated OpenAPI document from `/v3/api-docs`.
2. Validates the OpenAPI JSON.
3. Imports the API definition into OWASP ZAP.
4. Adds an HTTP Basic authorization header for `merchant-user`.
5. Actively tests the protected payment endpoints.
6. Generates JSON, HTML, and Markdown reports.
7. Applies a dedicated authenticated DAST security gate.

The imported API paths include:

```text
/api/payments
/api/payments/merchant/{merchantId}
/api/payments/{id}
```

During the first authenticated scan, ZAP triggered a `500 Internal Server Error` on `POST /api/payments`.

The root cause was that the create operation accepted a client-supplied entity ID and passed the request object directly to the JPA repository. Hibernate interpreted the supplied ID as an existing detached entity and produced an optimistic-locking failure.

The remediation forces new payment records to use a server-generated identifier by clearing any client-supplied ID before persistence.

The scan also identified a missing `Cross-Origin-Resource-Policy` response header. The application was updated to return:

```http
Cross-Origin-Resource-Policy: same-origin
```

After remediation, the authenticated scan no longer reported:

- Server error responses.
- Application error disclosure.
- Debug error-message disclosure.
- Missing or invalid Cross-Origin-Resource-Policy headers.

The final authenticated scan reported:

| Alert | Risk | Status |
|---|---:|---|
| Authentication Credentials Captured | High | Accepted HTTP Basic over HTTP limitation |
| A Client Error response code was returned by the server | Informational | Expected during active fuzzing |
| Non-Storable Content | Informational | Expected API behavior |

The active scan passed checks for SQL injection, command injection, cross-site scripting, path traversal, server-side template injection, XML external entity attacks, Spring4Shell, Log4Shell, and other enabled ZAP rules.


---

## Security Gates

The project currently applies security gates at multiple stages.

### Automated Test Gate

The pipeline stops when Maven or MockMvc tests fail.

### Dependency Vulnerability Gate

OWASP Dependency-Check fails the pipeline when a dependency has a CVSS score of 8.0 or higher.

### Secret Scanning Gate

Gitleaks returns a failure when exposed credentials or secrets are identified.

### Container Vulnerability Gate

The Trivy report is parsed, and the workflow fails when HIGH or CRITICAL container vulnerabilities are present.

### Baseline DAST Handling

The ZAP baseline scan generates and uploads findings without failing the pipeline on warnings. This preserves the baseline results for review while the authenticated API scan provides deeper coverage of protected business endpoints.

### Authenticated DAST Gate

The authenticated ZAP report is parsed by the CI workflow.

The gate:

- Fails when an unexpected High-risk authenticated ZAP finding is present.
- Allows only ZAP rule `10105`, `Authentication Credentials Captured`, as a documented accepted risk.
- Does not fail on informational findings.
- Fails when the authenticated ZAP report is missing.

The accepted rule exists because the educational lab intentionally uses HTTP Basic authentication over HTTP on the GitHub runner loopback interface. It is not classified as a false positive.

---

## Security Reports

Security reports are generated under:

```text
reports/
├── dependency-check/
├── semgrep/
├── gitleaks/
├── trivy/
├── openapi/
├── zap/
└── zap-api/
```

The GitHub Actions workflow uploads:

```text
dependency-check-report.json
semgrep.json
gitleaks.json
trivy-image.json
zap-report.json
zap-report.html
zap-report.md
openapi.json
zap-api-report.json
zap-api-report.html
zap-api-report.md
```

The reports are available from the completed workflow run under the GitHub Actions **Artifacts** section.

Security reports should be reviewed before being published because they may contain:

- Local paths.
- Dependency information.
- Application metadata.
- Scanner configuration.
- Repository information.
- Runtime headers.
- Environment details.

---

## Reviewing GitHub Actions Artifacts

Open:

```text
GitHub Repository
→ Actions
→ DevSecOps Pipeline
→ Completed Workflow Run
→ Artifacts
→ security-reports
```

Download and extract the ZIP file.

Example:

```bash
unzip security-reports.zip -d security-reports
```

List the reports:

```bash
find security-reports -type f
```

Open the ZAP HTML report:

```bash
find security-reports -name "zap-report.html"
```

Then:

```bash
xdg-open security-reports/zap/zap-report.html
```

The exact path may vary depending on the artifact directory structure.

---

## Local GitHub Actions Testing

The workflow can be partially tested locally using `act`.

List available jobs:

```bash
act -l
```

Run the workflow:

```bash
sudo act push \
  -j build-test-security \
  -P ubuntu-latest=catthehacker/ubuntu:act-latest
```

The local runner successfully validated stages including:

- Java setup.
- Maven Wrapper execution.
- Maven tests.
- Application packaging.
- Semgrep.
- Gitleaks.
- Docker image creation.

The local run was cancelled while Trivy was downloading vulnerability databases because the system ran out of disk space.

This was a local resource limitation rather than a project security failure.

The complete workflow was subsequently executed successfully using a GitHub-hosted runner.

---

## Findings and Remediation Summary

| Finding | Tool | Severity | Remediation or status |
|---|---|---:|---|
| Container did not explicitly run as non-root | Semgrep | Hardening issue | Added dedicated `appuser` and `USER appuser` |
| Ubuntu runtime contained vulnerable components | Trivy | HIGH | Replaced the runtime with Alpine |
| Alpine system packages required security updates | Trivy | HIGH | Added `apk upgrade --no-cache` |
| Embedded Tomcat contained critical vulnerabilities | Dependency-Check | CRITICAL | Upgraded embedded Tomcat |
| Password fallback values weakened secret handling | Code review | Configuration risk | Required environment-only passwords |
| Potential committed credentials | Gitleaks | None found | Continued environment-based secret handling |
| Weak Authentication Method | OWASP ZAP baseline | Medium | Accepted educational-lab limitation |
| Cookie without SameSite Attribute | OWASP ZAP baseline | Low | Documented for future hardening |
| Client-controlled payment ID caused server error | Authenticated OWASP ZAP | Low | Cleared client-supplied IDs before JPA persistence |
| Application and debug error disclosure | Authenticated OWASP ZAP | Low | Eliminated by fixing the underlying server error |
| Authentication Credentials Captured | Authenticated OWASP ZAP | High | Accepted rule `10105` for HTTP Basic over HTTP in CI |
| Cross-Origin-Resource-Policy header missing | OWASP ZAP | Low | Added `Cross-Origin-Resource-Policy: same-origin` |

---

## Project Structure

```text
devsecops-lab/
├── .github/
│   └── workflows/
│       └── devsecops.yml
├── reports/
│   ├── dependency-check/
│   ├── semgrep/
│   ├── gitleaks/
│   ├── trivy/
│   ├── openapi/
│   ├── zap/
│   └── zap-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── .gitignore
```

---

## Current Project Status

### Completed

- Spring Boot payment REST API.
- CRUD operations.
- Input validation.
- Structured exception handling.
- H2 database.
- HTTP Basic authentication.
- USER and ADMIN roles.
- BCrypt password processing.
- Environment-based credentials.
- Automated MockMvc tests.
- Hardened non-root Docker image.
- Docker Compose configuration.
- Semgrep SAST.
- Gitleaks secret scanning.
- Automated OWASP Dependency-Check.
- Dependency CVSS security gate.
- Tomcat vulnerability remediation.
- Trivy container scanning.
- Trivy HIGH and CRITICAL vulnerability gate.
- Application startup inside GitHub Actions.
- Application health validation.
- OWASP ZAP baseline DAST.
- OpenAPI document generation.
- Authenticated OWASP ZAP API scanning.
- Authenticated ZAP JSON, HTML, and Markdown reports.
- Remediation of the client-controlled payment ID server error.
- Cross-Origin-Resource-Policy response-header hardening.
- Authenticated ZAP High-risk security gate.
- Documented acceptance of ZAP rule `10105`.
- ZAP JSON, HTML, and Markdown reports.
- GitHub Actions artifact uploads.
- Successful end-to-end GitHub Actions execution.

### Pending

- Optional HTTPS implementation.
- Optional replacement of HTTP Basic authentication.
- Optional container registry publishing.
- Optional automated deployment stage.
- Optional Software Bill of Materials generation.
- Optional image signing and provenance.
- Optional production database integration.

---

## Security Limitations

This repository is an educational DevSecOps security lab.

It is not a production payment platform.

The application does not implement:

- Real financial transactions.
- Payment card processing.
- PCI DSS compliance.
- Production identity management.
- Production secrets management.
- High availability.
- Full audit logging.
- Production database security controls.
- TLS termination.
- Production-grade authenticated DAST coverage.
- Production infrastructure monitoring.
- Disaster recovery.

### HTTP Basic Authentication

The lab currently uses HTTP Basic authentication.

OWASP ZAP reports this as a Medium-risk `Weak Authentication Method` finding because Basic authentication transmits reusable credentials with each request.

During CI testing, the application is bound only to the GitHub runner's loopback interface. However, a production deployment would require HTTPS and should preferably replace HTTP Basic with OAuth 2.0, OpenID Connect, or short-lived token-based authentication.

HTTPS was deliberately not introduced into the current lab scope because doing so would require certificate generation, trust configuration, keystore or PEM management, secret handling, and certificate lifecycle management.

This finding is currently treated as an accepted lab limitation rather than a false positive.

### DAST Coverage

The OWASP ZAP baseline scan targets the public health endpoint.

This confirms that the following workflow operates successfully:

```text
Build image
    |
    v
Start application
    |
    v
Verify health
    |
    v
Run ZAP
    |
    v
Generate reports
    |
    v
Upload artifacts
```

The pipeline also performs an authenticated OpenAPI-driven active scan against the protected payment endpoints.

This materially improves runtime coverage, but it does not represent complete production-grade DAST because the lab uses a single authenticated USER context, synthetic data, an H2 database, and a limited CI runtime environment.

### Self-Contained Development Database

The application uses H2 for development and lab testing.

H2 is not intended to represent the access controls, encryption, backup, monitoring, and resilience requirements of a production payment database.

### Scanner Limitations

A passing security scan does not prove that an application is free from vulnerabilities.

Security scanner results depend on:

- Scanner rule coverage.
- Vulnerability database freshness.
- Tool versions.
- Configuration.
- Authentication coverage.
- Runtime execution paths.
- Test data.
- Suppression rules.
- Network accessibility.
- False-positive and false-negative behavior.

---

## Future Improvements

Potential future enhancements include:

- Add the SameSite attribute to relevant cookies.
- Replace HTTP Basic with OAuth 2.0 or OpenID Connect.
- Add HTTPS and certificate lifecycle management.
- Generate an SBOM using Trivy or Syft.
- Sign container images with Cosign.
- Generate supply-chain provenance.
- Publish images to a private container registry.
- Add branch-protection requirements.
- Require security checks before pull-request merging.
- Add code-coverage reporting.
- Replace H2 with PostgreSQL.
- Add centralized application logging.
- Add runtime monitoring.
- Add Kubernetes manifests.
- Add policy-as-code scanning.
- Add infrastructure-as-code scanning.
- Pin GitHub Actions and scanner images to immutable versions or digests.

---

## Skills Demonstrated

This project demonstrates practical experience with:

- Java 21.
- Spring Boot.
- REST API development.
- Spring Security.
- Role-based access control.
- HTTP Basic authentication.
- BCrypt.
- Request validation.
- Structured exception handling.
- JUnit.
- MockMvc.
- Maven Wrapper.
- Git.
- GitHub.
- GitHub Actions.
- Docker.
- Docker Compose.
- Container hardening.
- Semgrep.
- Gitleaks.
- OWASP Dependency-Check.
- Trivy.
- OWASP ZAP.
- CI security gates.
- Security report generation.
- Vulnerability remediation.
- DAST finding analysis.
- Accepted-risk documentation.

---

## Disclaimer

This project is intended for authorized education, testing, and DevSecOps practice.

Do not use real payment data, real customer information, production passwords, API keys, or other sensitive information with this application.

Security findings should be reviewed in context. Scanner output should not be treated as proof of security or as a substitute for architecture review, threat modeling, manual testing, code review, penetration testing, and operational security controls.
