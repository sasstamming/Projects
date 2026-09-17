package ee.filmsociety.moviesapi.service;

import ee.filmsociety.moviesapi.dto.ActorRequest;
import ee.filmsociety.moviesapi.dto.ActorResponse;
import ee.filmsociety.moviesapi.dto.DtoMapper;
import ee.filmsociety.moviesapi.entity.Actor;
import ee.filmsociety.moviesapi.entity.Movie;
import ee.filmsociety.moviesapi.exception.DeletionNotAllowedException;
import ee.filmsociety.moviesapi.exception.ResourceNotFoundException;
import ee.filmsociety.moviesapi.repository.ActorRepository;
import ee.filmsociety.moviesapi.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public ActorResponse create(ActorRequest request) {
        Actor actor = new Actor(request.name(), request.birthDate());
        actor = actorRepository.save(actor);
        // Actor is the inverse side, so associations are written through Movie (the owning side).
        applyMovieAssociations(actor, request.movieIds());
        return DtoMapper.toActorResponse(actor);
    }

    @Transactional
    public Page<ActorResponse> findAll(Pageable pageable) {
        return actorRepository.findAll(pageable).map(DtoMapper::toActorResponse);
    }

    @Transactional
    public ActorResponse findById(Long id) {
        return DtoMapper.toActorResponse(getActorOrThrow(id));
    }

    // GET /api/actors?name=...
    @Transactional
    public Page<ActorResponse> findByName(String name, Pageable pageable) {
        return actorRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(DtoMapper::toActorResponse);
    }

    // PATCH - only non-null fields are applied.
    @Transactional
    public ActorResponse update(Long id, ActorRequest request) {
        Actor actor = getActorOrThrow(id);

        if (request.name() != null) {
            actor.setName(request.name());
        }
        if (request.birthDate() != null) {
            actor.setBirthDate(request.birthDate());
        }
        if (request.movieIds() != null) {
            // Replace the whole set of associations when movieIds is provided.
            for (Movie movie : new HashSet<>(actor.getMovies())) {
                movie.removeActor(actor);
            }
            applyMovieAssociations(actor, request.movieIds());
        }
        return DtoMapper.toActorResponse(actor);
    }

    // Blocked by default if the actor is in movies; force=true clears the links first.
    @Transactional
    public void delete(Long id, boolean force) {
        Actor actor = getActorOrThrow(id);
        int movieCount = actor.getMovies().size();

        if (movieCount > 0 && !force) {
            throw new DeletionNotAllowedException(
                    "Unable to delete actor '" + actor.getName()
                            + "' as they are associated with " + movieCount + " movies");
        }

        for (Movie movie : new HashSet<>(actor.getMovies())) {
            movie.removeActor(actor);
        }
        actorRepository.delete(actor);
    }

    // Links the actor to existing movies through the owning side (Movie).
    private void applyMovieAssociations(Actor actor, Set<Long> movieIds) {
        if (movieIds == null) {
            return;
        }
        for (Long movieId : movieIds) {
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie", movieId));
            movie.addActor(actor);
        }
    }

    private Actor getActorOrThrow(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor", id));
    }
}
