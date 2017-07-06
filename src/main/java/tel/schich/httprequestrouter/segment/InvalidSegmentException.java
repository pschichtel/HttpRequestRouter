package tel.schich.httprequestrouter.segment;

public class InvalidSegmentException extends RuntimeException {
    public InvalidSegmentException(String message) {
        super(message);
    }

    public InvalidSegmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSegmentException(Throwable cause) {
        super(cause);
    }
}
