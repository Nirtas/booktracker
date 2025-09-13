# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.0] - 2025-09-13

### Added

- **CI/CD:** Implemented a fully automated CI pipeline using GitHub Actions to run all Backend and Android tests on every pull request.
- **Android App:** Implemented full test coverage for Android app.
- **Backend Server:** Implemented full test coverage for Backend.
- **Backend Server:** Added server-side localization for genres, now providing names in both English and Russian.
- **Android App:** Implemented full client-side localization for English and Russian languages.

### Changed

- **Backend Server:** Refactored Backend error handling to use a `StatusPages` plugin, providing error responses.
- **Android App:** Improved client-side error handling to correctly process and display localized error messages from Backend.

## [0.3.0] - 2025-08-20

### Added

- **Backend Server:** `GET /api/books/{id}` endpoint to retrieve a single book by its ID.
- **Backend Server:** `PUT /api/books/{id}` and `DELETE /api/books/{id}` endpoints for updating and deleting books.
- **Android App:** New screen `BookDetailsScreen` to display detailed information about a book.
- **Android App:** New screen `BookEditScreen` for modifying existing book entries.
- **Android App:** Implemented functionality to delete books from the details or list screen.
- **Android App:** Implemented search, sort, and filter capabilities for the book list.

### Changed

- **Project:** Enhanced the core `Book` model across both backend and Android applications with new properties such as status, genres and a `createdAt` timestamp.

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