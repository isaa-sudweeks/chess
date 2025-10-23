package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    public DataAccessException(final String message) {
        super(message);
    }

    public DataAccessException(final String message, final Throwable ex) {
        super(message, ex);
    }
}
