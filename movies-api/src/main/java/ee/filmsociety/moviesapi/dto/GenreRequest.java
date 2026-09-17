package ee.filmsociety.moviesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Input DTO for creating/updating a Genre.
 * On PATCH the service applies only non-null fields, so validation is enforced
 * (via @Valid) on create only.
 */
public record GenreRequest(
        @NotBlank(message = "Genre name must not be blank")
        @Size(max = 100, message = "Genre name must be at most 100 characters")
        String name
) {
}
