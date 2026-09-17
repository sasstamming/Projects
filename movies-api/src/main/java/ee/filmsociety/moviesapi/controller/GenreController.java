package ee.filmsociety.moviesapi.controller;

import ee.filmsociety.moviesapi.dto.GenreRequest;
import ee.filmsociety.moviesapi.dto.GenreResponse;
import ee.filmsociety.moviesapi.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreResponse create(@Valid @RequestBody GenreRequest request) {
        return genreService.create(request);
    }

    @GetMapping
    public Page<GenreResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return genreService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public GenreResponse getById(@PathVariable Long id) {
        return genreService.findById(id);
    }

    // PATCH — no @Valid, since partial updates may omit required fields.
    @PatchMapping("/{id}")
    public GenreResponse update(@PathVariable Long id, @RequestBody GenreRequest request) {
        return genreService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force) {
        genreService.delete(id, force);
    }
}
