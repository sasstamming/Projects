package ee.filmsociety.moviesapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Input DTO for creating/updating an Actor.
 * movieIds lets a client associate the actor with existing movies.
 */
public record ActorRequest(
        @NotBlank(message = "Actor name must not be blank")
        @Size(max = 150, message = "Actor name must be at most 150 characters")
        String name,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        Set<Long> movieIds
) {
}
