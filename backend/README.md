[Русская версия](README.ru.md)

## API Endpoints

All endpoints are prefixed with `/api`.

### Authentication

To access protected endpoints, pass the JWT `accessToken` in the `Authorization` header.

```
Authorization: Bearer <accessToken>
```

The `accessToken` can be obtained after a successful login (`POST /tokens`). It is valid for **15 minutes** (this is configurable in the Ktor `application.yaml` file) and contains the `userId`, which the server uses to identify the user.

The `refreshToken` is valid for **30 days** (this value is also configurable).

| Method | Endpoint | Authentication | Description |
| --- | --- | --- | --- |
| **Books** |
| `GET` | `/books` | **Required** | Get the list of books for the current user |
| `POST` | `/books` | **Required** | Create a new book entry for the current user |
| `GET` | `/books/{id}` | **Required** | Get details of a single book |
| `DELETE` | `/books/{id}` | **Required** | Delete a book |
| `PUT` | `/books/{id}` | **Required** | Update a book's details |
| `POST` | `/books/{id}/cover` | **Required** | Update a book's cover image |
| **Genres** |
| `GET` | `/genres` | **Required** | Get the list of available genres |
| **Authentication & Users** |
| `POST` | `/tokens` | Not required | Login, get a pair of `accessToken` and `refreshToken` |
| `POST` | `/tokens/refresh` | Not required | Refresh the pair of `accessToken` and `refreshToken` |
| `POST` | `/users` | Not required | Register a new user |
| `POST` | `/verifications` | Not required | Email verification with a code |
| `POST` | `/verifications/resend` | Not required | Resend the verification code |
| `GET` | `/users/me` | **Required** | Get info about the current user |
| `DELETE` | `/users/me` | **Required** | Delete the current user's account |
| `PUT` | `/users/me/email` | **Required** | Update the current user's email |
| `PUT` | `/users/me/password` | **Required** | Update the current user's password |
