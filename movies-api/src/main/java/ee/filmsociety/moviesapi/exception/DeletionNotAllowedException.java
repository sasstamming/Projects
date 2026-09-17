package ee.filmsociety.moviesapi.exception;

/**
 * Thrown when a non-forced delete is attempted on an entity that still has
 * relationships. Mapped to HTTP 400.
 */
public class DeletionNotAllowedException extends RuntimeException {

    public DeletionNotAllowedException(String message) {
        super(message);
    }
}
