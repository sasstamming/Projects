package ee.filmsociety.moviesapi.repository;

import ee.filmsociety.moviesapi.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // GET /api/movies?year=...
    Page<Movie> findByReleaseYear(Integer releaseYear, Pageable pageable);

    // GET /api/movies?genre={genreId} — traverses the movie_genres join table
    Page<Movie> findByGenres_Id(Long genreId, Pageable pageable);

    // GET /api/movies?actor={actorId} — traverses the movie_actors join table
    Page<Movie> findByActors_Id(Long actorId, Pageable pageable);

    // GET /api/movies/search?title=... (case-insensitive partial match)
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
