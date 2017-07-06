package tel.schich.httprequestrouter.segment.constraint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConstraintLookup {

    private final Constraint defaultConstraint;
    private final Map<String, Constraint> constraints;

    private ConstraintLookup(Constraint defaultConstraint, Map<String, Constraint> constraints) {
        this.defaultConstraint = defaultConstraint;
        this.constraints = constraints;
    }

    public Constraint getDefault() {
        return defaultConstraint;
    }

    public Optional<Constraint> lookup(String name) {
        return Optional.ofNullable(constraints.get(name));
    }

    public ConstraintLookup with(String name, Constraint constraint) {
        Map<String, Constraint> constraints = new HashMap<>(this.constraints);
        constraints.put(name, constraint);
        return new ConstraintLookup(defaultConstraint, constraints);
    }

    public ConstraintLookup defaultingTo(Constraint constraint) {
        return new ConstraintLookup(constraint, constraints);
    }

    public static ConstraintLookup create(Constraint defaultConstraint) {
        return new ConstraintLookup(defaultConstraint, Collections.emptyMap());
    }
}
