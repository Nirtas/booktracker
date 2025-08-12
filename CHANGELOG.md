# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2025-08-12

### Added

- **Backend Server:** `POST /api/books` endpoint to allow creating new book entries with a cover image.
- **Android App:** New screen `AddBookScreen` for creating and uploading new books.
- **Android App:** Implemented file picking from gallery and image upload logic.
- **Project:** Configured Docker Compose for development and production environments.

## [0.1.0] - 2025-08-09

### Added

- **Android App:** Initial screen to display a list of books.
- **Android App:** Implemented a multi-layered Clean Architecture (`Data`, `Domain`, `Presentation`) for scalability and testability.
- **Android App:** Asynchronous loading and display of book cover images from a URL.
- **Android App:** Setup of core dependencies including Jetpack Compose for UI, Hilt for dependency injection and Ktor Client for networking.
- **Backend Server:** `GET /api/books` endpoint to provide a list of all books from the database.
- **Backend Server:** PostgreSQL database integration using Exposed DSL for data persistence.
- **Backend Server:** Setup of core dependencies including Ktor as the web framework, Koin for dependency injection and Exposed for database access.
- **Project:** Initial Git repository structure (monorepo) for managing both Android and backend codebases.
- **Project:** First official release `v0.1.0` with versioning for both client and server applications.

### Changed

- **Optimization:** Significantly reduced the application's size.