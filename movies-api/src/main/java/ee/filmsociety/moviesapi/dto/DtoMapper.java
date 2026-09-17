package ee.filmsociety.moviesapi.dto;

import ee.filmsociety.moviesapi.entity.Actor;
import ee.filmsociety.moviesapi.entity.Genre;
import ee.filmsociety.moviesapi.entity.Movie;

import java.util.List;

// Converts entities into the response DTOs returned by the controllers.
public class DtoMapper {

    public static GenreResponse toGenreResponse(Genre genre) {
        return new GenreResponse(genre.getId(), genre.getName());
    }

    public static ActorResponse toActorResponse(Actor actor) {
        return new ActorResponse(actor.getId(), actor.getName(), actor.getBirthDate());
    }

    public static MovieResponse toMovieResponse(Movie movie) {
        List<GenreResponse> genres = movie.getGenres().stream()
                .map(DtoMapper::toGenreResponse)
                .toList();

        List<ActorResponse> actors = movie.getActors().stream()
                .map(DtoMapper::toActorResponse)
                .toList();

        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getDuration(),
                genres,
                actors
        );
    }
}
