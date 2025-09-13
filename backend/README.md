[Русская версия](README.ru.md)

## API Endpoints

All endpoints are prefixed with `/api`.

| Method      | Endpoint               | Description                     |
|-------------|------------------------|---------------------------------|
| **Books**   |
| `GET`       | `/books`               | Get a list of all books         |
| `POST`      | `/books`               | Create a new book entry         |
| `GET`       | `/books/{id}`          | Get details of a single book    |
| `DELETE`    | `/books/{id}`          | Delete a book                   |
| `PUT`       | `/books/{id}`          | Update a book's details         |
| `POST`      | `/books/{id}/cover`    | Update a book's cover image     |
| **Genres**  |
| `GET`       | `/genres`              | Get a list of available genres  |
