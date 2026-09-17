package ee.filmsociety.moviesapi.service;

import ee.filmsociety.moviesapi.dto.ActorResponse;
import ee.filmsociety.moviesapi.dto.DtoMapper;
import ee.filmsociety.moviesapi.dto.MovieRequest;
import ee.filmsociety.moviesapi.dto.MovieResponse;
import ee.filmsociety.moviesapi.entity.Actor;
import ee.filmsociety.moviesapi.entity.Genre;
import ee.filmsociety.moviesapi.entity.Movie;
import ee.filmsociety.moviesapi.exception.DeletionNotAllowedException;
import ee.filmsociety.moviesapi.exception.ResourceNotFoundException;
import ee.filmsociety.moviesapi.repository.ActorRepository;
import ee.filmsociety.moviesapi.repository.GenreRepository;
import ee.filmsociety.moviesapi.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    public MovieService(MovieRepository movieRepository,
                        GenreRepository genreRepository,
                        ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
    }

    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie(request.title(), request.releaseYear(), request.duration());
        // Movie is the owning side, so setting these collections persists the join tables.
        applyGenres(movie, request.genreIds());
        applyActors(movie, request.actorIds());
        return DtoMapper.toMovieResponse(movieRepository.save(movie));
    }

    @Transactional
    public Page<MovieResponse> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable).map(DtoMapper::toMovieResponse);
    }

    @Transactional
    public MovieResponse findById(Long id) {
        return DtoMapper.toMovieResponse(getMovieOrThrow(id));
    }

    // GET /api/movies?genre={genreId}
    @Transactional
    public Page<MovieResponse> findByGenre(Long genreId, Pageable pageable) {
        return movieRepository.findByGenres_Id(genreId, pageable).map(DtoMapper::toMovieResponse);
    }

    // GET /api/movies?year={releaseYear}
    @Transactional
    public Page<MovieResponse> findByYear(Integer year, Pageable pageable) {
        return movieRepository.findByReleaseYear(year, pageable).map(DtoMapper::toMovieResponse);
    }

    // GET /api/movies?actor={actorId}
    @Transactional
    public Page<MovieResponse> findByActor(Long actorId, Pageable pageable) {
        return movieRepository.findByActors_Id(actorId, pageable).map(DtoMapper::toMovieResponse);
    }

    // GET /api/movies/search?title=...
    @Transactional
    public Page<MovieResponse> searchByTitle(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(DtoMapper::toMovieResponse);
    }

    // GET /api/movies/{movieId}/actors
    @Transactional
    public List<ActorResponse> getActors(Long movieId) {
        Movie movie = getMovieOrThrow(movieId);
        return movie.getActors().stream()
                .map(DtoMapper::toActorResponse)
                .toList();
    }

    // PATCH - only non-null fields are applied; collections are replaced when provided.
    @Transactional
    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = getMovieOrThrow(id);

        if (request.title() != null) {
            movie.setTitle(request.title());
        }
        if (request.releaseYear() != null) {
            movie.setReleaseYear(request.releaseYear());
        }
        if (request.duration() != null) {
            movie.setDuration(request.duration());
        }
        if (request.genreIds() != null) {
            for (Genre genre : new HashSet<>(movie.getGenres())) {
                movie.removeGenre(genre);
            }
            applyGenres(movie, request.genreIds());
        }
        if (request.actorIds() != null) {
            for (Actor actor : new HashSet<>(movie.getActors())) {
                movie.removeActor(actor);
            }
            applyActors(movie, request.actorIds());
        }
        return DtoMapper.toMovieResponse(movie);
    }

    // Blocked by default if the movie has genres or actors; force=true clears the links first.
    @Transactional
    public void delete(Long id, boolean force) {
        Movie movie = getMovieOrThrow(id);
        int relationshipCount = movie.getGenres().size() + movie.getActors().size();

        if (relationshipCount > 0 && !force) {
            throw new DeletionNotAllowedException(
                    "Cannot delete movie '" + movie.getTitle() + "' because it has "
                            + movie.getGenres().size() + " associated genres and "
                            + movie.getActors().size() + " associated actors");
        }

        for (Genre genre : new HashSet<>(movie.getGenres())) {
            movie.removeGenre(genre);
        }
        for (Actor actor : new HashSet<>(movie.getActors())) {
            movie.removeActor(actor);
        }
        movieRepository.delete(movie);
    }

    private void applyGenres(Movie movie, Set<Long> genreIds) {
        if (genreIds == null) {
            return;
        }
        for (Long genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", genreId));
            movie.addGenre(genre);
        }
    }

    private void applyActors(Movie movie, Set<Long> actorIds) {
        if (actorIds == null) {
            return;
        }
        for (Long actorId : actorIds) {
            Actor actor = actorRepository.findById(actorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Actor", actorId));
            movie.addActor(actor);
        }
    }

    private Movie getMovieOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
    }
}
