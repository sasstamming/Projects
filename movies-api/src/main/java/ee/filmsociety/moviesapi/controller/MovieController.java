package ee.filmsociety.moviesapi.controller;

import ee.filmsociety.moviesapi.dto.ActorResponse;
import ee.filmsociety.moviesapi.dto.MovieRequest;
import ee.filmsociety.moviesapi.dto.MovieResponse;
import ee.filmsociety.moviesapi.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        return movieService.create(request);
    }

    // GET /api/movies with optional filters ?genre= / ?year= / ?actor= and pagination.
    @GetMapping
    public Page<MovieResponse> getAll(@RequestParam(required = false) Long genre,
                                      @RequestParam(required = false) Integer year,
                                      @RequestParam(required = false) Long actor,
                                      @PageableDefault(size = 20) Pageable pageable) {
        if (genre != null) {
            return movieService.findByGenre(genre, pageable);
        }
        if (year != null) {
            return movieService.findByYear(year, pageable);
        }
        if (actor != null) {
            return movieService.findByActor(actor, pageable);
        }
        return movieService.findAll(pageable);
    }

    // GET /api/movies/search?title=...
    @GetMapping("/search")
    public Page<MovieResponse> search(@RequestParam String title,
                                      @PageableDefault(size = 20) Pageable pageable) {
        return movieService.searchByTitle(title, pageable);
    }

    @GetMapping("/{id}")
    public MovieResponse getById(@PathVariable Long id) {
        return movieService.findById(id);
    }

    // GET /api/movies/{movieId}/actors
    @GetMapping("/{movieId}/actors")
    public List<ActorResponse> getActors(@PathVariable Long movieId) {
        return movieService.getActors(movieId);
    }

    @PatchMapping("/{id}")
    public MovieResponse update(@PathVariable Long id, @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force) {
        movieService.delete(id, force);
    }
}
