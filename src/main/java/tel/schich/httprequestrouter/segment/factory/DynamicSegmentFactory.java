package tel.schich.httprequestrouter.segment.factory;

import tel.schich.httprequestrouter.segment.*;
import tel.schich.httprequestrouter.segment.constraint.Constraint;
import tel.schich.httprequestrouter.segment.constraint.ConstraintLookup;
import tel.schich.httprequestrouter.segment.constraint.UnknownConstrainException;

import java.util.Optional;

public class DynamicSegmentFactory implements SegmentFactory {

    private final ConstraintLookup constraints;

    public DynamicSegmentFactory(ConstraintLookup constraints) {
        this.constraints = constraints;
    }

    @Override
    public Segment toSegment(String raw) {
        if (raw.isEmpty()) {
            return StaticSegment.EMPTY;
        }

        switch (raw.charAt(0)) {
            case ':':
                return parseConstrained(raw, constraints);
            case '*':
                return parseWildcard(raw);
            default:
                return new StaticSegment(raw);
        }
    }

    private Segment parseConstrained(String raw, ConstraintLookup constraints) {
        int openingBrace = raw.indexOf('{', 1);
        if (openingBrace == -1) {
            return new ConstrainedSegment(raw.substring(1), constraints.getDefault());
        }
        int closingBrace = raw.indexOf('}', openingBrace);
        if (closingBrace == -1 || closingBrace != raw.length() - 1) {
            return new ConstrainedSegment(raw.substring(1), constraints.getDefault());
        }

        String name = raw.substring(0, openingBrace);
        String constraintName = raw.substring(openingBrace + 1, closingBrace);

        Optional<Constraint> constraint = constraints.lookup(constraintName);
        if (constraint.isPresent()) {
            return new ConstrainedSegment(name, constraint.get());
        } else {
            throw new UnknownConstrainException(constraintName);
        }
    }

    private Segment parseWildcard(String raw) {
        return new WildcardSegment(raw.substring(1));
    }
}
