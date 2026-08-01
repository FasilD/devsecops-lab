# Spring Boot DevSecOps Security Lab

A hands-on DevSecOps lab built around a Spring Boot payment REST API.

The project demonstrates how application security controls, automated testing, static analysis, secret scanning, dependency analysis, container hardening, vulnerability scanning, and CI security gates can be integrated into a software development lifecycle.

---

## Project Overview

This project contains a secured payment REST API developed with Spring Boot and Java 21.

The application supports payment creation, retrieval, update, and deletion while applying authentication, role-based authorization, input validation, structured error handling, automated tests, and container security controls.

The project also demonstrates a DevSecOps workflow that automatically builds, tests, scans, and validates the application before it can be considered ready for deployment.

---

## Objectives

The main objectives of this lab are to:

- Build a functional Spring Boot REST API.
- Apply secure coding practices.
- Implement authentication and role-based access control.
- Manage application credentials through environment variables.
- Run automated application tests.
- perform Static Application Security Testing.
- Detect exposed secrets in source code and Git history.
- Analyze software dependencies for known vulnerabilities.
- Build and harden a Docker container.
- Scan the container image for vulnerabilities.
- Automate security checks using GitHub Actions.
- Enforce security gates before deployment.

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

---

## Technology Stack

| Component | Technology |
|---|---|
| Programming language | Java 21 |
| Application framework | Spring Boot |
| Build tool | Maven |
| Database | H2 |
| Authentication | HTTP Basic |
| Password hashing | BCrypt |
| Testing | JUnit and MockMvc |
| Containerization | Docker |
| Container orchestration | Docker Compose |
| CI platform | GitHub Actions |
| SAST | Semgrep |
| Secret scanning | Gitleaks |
| Dependency scanning | OWASP Dependency-Check |
| Container scanning | Trivy |
| DAST | OWASP ZAP planned |

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