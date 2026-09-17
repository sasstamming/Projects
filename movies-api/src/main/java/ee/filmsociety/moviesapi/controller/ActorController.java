package ee.filmsociety.moviesapi.controller;

import ee.filmsociety.moviesapi.dto.ActorRequest;
import ee.filmsociety.moviesapi.dto.ActorResponse;
import ee.filmsociety.moviesapi.service.ActorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActorResponse create(@Valid @RequestBody ActorRequest request) {
        return actorService.create(request);
    }

    // GET /api/actors  and  GET /api/actors?name=... (case-insensitive partial)
    @GetMapping
    public Page<ActorResponse> getAll(@RequestParam(required = false) String name,
                                      @PageableDefault(size = 20) Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return actorService.findByName(name, pageable);
        }
        return actorService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ActorResponse getById(@PathVariable Long id) {
        return actorService.findById(id);
    }

    @PatchMapping("/{id}")
    public ActorResponse update(@PathVariable Long id, @RequestBody ActorRequest request) {
        return actorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force) {
        actorService.delete(id, force);
    }
}
