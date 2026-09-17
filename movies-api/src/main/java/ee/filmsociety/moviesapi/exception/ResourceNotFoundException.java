package ee.filmsociety.moviesapi.exception;

/** Thrown when an entity cannot be found by id. Mapped to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " with id " + id + " not found");
    }
}
