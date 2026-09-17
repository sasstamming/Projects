package ee.filmsociety.moviesapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Input DTO for creating/updating a Movie.
 * genreIds / actorIds reference existing genres/actors to associate.
 */
public record MovieRequest(
        @NotBlank(message = "Movie title must not be blank")
        @Size(max = 255, message = "Movie title must be at most 255 characters")
        String title,

        @Min(value = 1888, message = "Release year looks implausible")
        Integer releaseYear,

        @Min(value = 1, message = "Duration must be positive")
        Integer duration,

        Set<Long> genreIds,

        Set<Long> actorIds
) {
}
