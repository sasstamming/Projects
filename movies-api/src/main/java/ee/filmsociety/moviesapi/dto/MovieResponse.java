package ee.filmsociety.moviesapi.dto;

import java.util.List;

public record MovieResponse(
        Long id,
        String title,
        Integer releaseYear,
        Integer duration,
        List<GenreResponse> genres,
        List<ActorResponse> actors
) {
}
