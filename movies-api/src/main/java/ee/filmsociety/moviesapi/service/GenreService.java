package ee.filmsociety.moviesapi.service;

import ee.filmsociety.moviesapi.dto.DtoMapper;
import ee.filmsociety.moviesapi.dto.GenreRequest;
import ee.filmsociety.moviesapi.dto.GenreResponse;
import ee.filmsociety.moviesapi.entity.Genre;
import ee.filmsociety.moviesapi.entity.Movie;
import ee.filmsociety.moviesapi.exception.DeletionNotAllowedException;
import ee.filmsociety.moviesapi.exception.ResourceNotFoundException;
import ee.filmsociety.moviesapi.repository.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional
    public GenreResponse create(GenreRequest request) {
        Genre genre = new Genre(request.name());
        return DtoMapper.toGenreResponse(genreRepository.save(genre));
    }

    @Transactional
    public Page<GenreResponse> findAll(Pageable pageable) {
        return genreRepository.findAll(pageable).map(DtoMapper::toGenreResponse);
    }

    @Transactional
    public GenreResponse findById(Long id) {
        return DtoMapper.toGenreResponse(getGenreOrThrow(id));
    }

    // PATCH - only fields present (non-null) in the request are applied.
    @Transactional
    public GenreResponse update(Long id, GenreRequest request) {
        Genre genre = getGenreOrThrow(id);
        if (request.name() != null) {
            genre.setName(request.name());
        }
        return DtoMapper.toGenreResponse(genreRepository.save(genre));
    }

    // Blocked by default if the genre has movies; force=true clears the links first.
    @Transactional
    public void delete(Long id, boolean force) {
        Genre genre = getGenreOrThrow(id);
        int movieCount = genre.getMovies().size();

        if (movieCount > 0 && !force) {
            throw new DeletionNotAllowedException(
                    "Cannot delete genre '" + genre.getName()
                            + "' because it has " + movieCount + " associated movies");
        }

        // Movie is the owning side, so we remove the genre from each movie to clear the join rows.
        for (Movie movie : new HashSet<>(genre.getMovies())) {
            movie.removeGenre(genre);
        }
        genreRepository.delete(genre);
    }

    private Genre getGenreOrThrow(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", id));
    }
}
