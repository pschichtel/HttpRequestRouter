package tel.schich.httprequestrouter.segment.constraint;

import java.util.function.Predicate;

@FunctionalInterface
public interface Constraint {
    boolean test(String path, int start, int end);
}
