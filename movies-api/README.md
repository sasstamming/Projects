# Movies API

A REST API for managing a film society's movie database, built with **Spring Boot 3.4** and **Spring Data JPA**, backed by **SQLite**. It stores movies, genres, and actors, and the many-to-many relationships between them.

## Tech stack

- Java 17
- Spring Boot 3.4.1 (Web, Data JPA, Validation)
- Hibernate ORM with the community SQLite dialect
- SQLite (file-based, `movies.db` created on first run)
- Maven

## Setup & installation

**Prerequisites:** JDK 17+ and Maven (or use the Maven wrapper if present). SQLite itself needs no separate install — the `sqlite-jdbc` driver is bundled and the database file is created automatically.

```bash
# from the project root
mvn spring-boot:run
```

The app starts on `http://localhost:8080`. On the **first** run a `DataLoader` seeds sample data (8 genres, 22 movies, 20 actors). It only runs when the database is empty, so restarts won't duplicate anything. Delete `movies.db` if you want a clean reseed.

To build a runnable jar:

```bash
mvn clean package
java -jar target/movies-api-1.0.0.jar
```

## Data model

Three entities with two many-to-many relationships:

- **Movie** ↔ **Genre**
- **Movie** ↔ **Actor**

**Movie is the owning side of both relationships** — it declares the `@JoinTable`s (`movie_genres`, `movie_actors`). Genre and Actor are the inverse sides (`mappedBy`). This matters: association changes are only persisted when made through Movie's collections. Genre/Actor services that need to change associations do so by going through the Movie entity.

## API reference

Base path: `/api`

### Genres — `/api/genres`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/genres` | Create a genre |
| GET | `/api/genres` | List genres (paged) |
| GET | `/api/genres/{id}` | Get one genre |
| PATCH | `/api/genres/{id}` | Partial update |
| DELETE | `/api/genres/{id}` | Delete (blocked if it has movies) |
| DELETE | `/api/genres/{id}?force=true` | Force delete (clears associations first) |

### Actors — `/api/actors`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/actors` | Create an actor (optionally with `movieIds`) |
| GET | `/api/actors` | List actors (paged) |
| GET | `/api/actors?name={name}` | Filter by name (case-insensitive, partial) |
| GET | `/api/actors/{id}` | Get one actor |
| PATCH | `/api/actors/{id}` | Partial update |
| DELETE | `/api/actors/{id}` | Delete (blocked if cast in movies) |
| DELETE | `/api/actors/{id}?force=true` | Force delete |

### Movies — `/api/movies`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/movies` | Create a movie (with `genreIds`, `actorIds`) |
| GET | `/api/movies` | List movies (paged) |
| GET | `/api/movies?genre={genreId}` | Filter by genre |
| GET | `/api/movies?year={releaseYear}` | Filter by release year |
| GET | `/api/movies?actor={actorId}` | Filter by actor |
| GET | `/api/movies/search?title={q}` | Search by title (case-insensitive, partial) |
| GET | `/api/movies/{id}` | Get one movie |
| GET | `/api/movies/{movieId}/actors` | List actors in a movie |
| PATCH | `/api/movies/{id}` | Partial update |
| DELETE | `/api/movies/{id}` | Delete (blocked if it has genres/actors) |
| DELETE | `/api/movies/{id}?force=true` | Force delete |

### Example request bodies

Create a movie linked to existing genres and actors:
```json
POST /api/movies
{
  "title": "Interstellar",
  "releaseYear": 2014,
  "duration": 169,
  "genreIds": [1, 2],
  "actorIds": [4]
}
```

Partial update (PATCH only touches fields you send):
```json
PATCH /api/movies/1
{ "duration": 140 }
```

## Design decisions worth knowing

**DTOs instead of entities.** Controllers never return JPA entities. Because the relationships are bidirectional, serializing an entity directly would recurse infinitely (Movie → Actor → Movie → …) and leak the persistence schema. Request and response DTOs (Java `record`s) form a clean boundary. The response DTOs don't carry back-references — a `MovieResponse` embeds simple genre and actor DTOs that don't contain movies — which is what breaks the cycle.

**PATCH = partial update.** The update methods apply only the fields that are present (non-null) in the request body; anything omitted is left unchanged. `@Valid` runs on `POST` (create) only — running full-object validation on a partial `PATCH` would wrongly reject bodies that legitimately omit required fields.

**Force-delete.** The same rule is applied to all three entities: by default, deleting something that still has relationships is rejected with `400 Bad Request` and a clear message (e.g. `Cannot delete genre 'Action' because it has 15 associated movies`). Passing `?force=true` removes the associations first and then deletes the entity, returning `204 No Content`. Because Movie owns both join tables, clearing a link always goes through the Movie side.

**Error handling.** A single `@RestControllerAdvice` maps `ResourceNotFoundException` → 404, validation failures → 400, and blocked deletes → 400, each returning a small JSON error body with timestamp, status, and message.

## Extra features implemented

- **Pagination** on all list endpoints via `page`, `size`, and `sort` params (Spring `Pageable`); responses are `Page` objects with metadata.
- **Title search** at `GET /api/movies/search?title=` (case-insensitive, partial match).
- **Name filter** for actors at `GET /api/actors?name=`.
- **Sample dataset** seeded automatically, covering single- and multi-genre movies, actors in one vs. many movies, and release years spanning 1988–2015.

## Testing

Import `postman/Movie-Database-API.postman_collection.json` into Postman. It contains CRUD requests for all three entities, the filter/search endpoints, and error scenarios (non-existent id, invalid body, blocked delete). The collection uses a `baseUrl` variable defaulting to `http://localhost:8080`.
