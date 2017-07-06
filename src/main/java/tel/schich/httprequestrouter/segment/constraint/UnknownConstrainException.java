package tel.schich.httprequestrouter.segment.constraint;

import tel.schich.httprequestrouter.segment.InvalidSegmentException;

public class UnknownConstrainException extends InvalidSegmentException {
    public UnknownConstrainException(String message) {
        super(message);
    }
}
