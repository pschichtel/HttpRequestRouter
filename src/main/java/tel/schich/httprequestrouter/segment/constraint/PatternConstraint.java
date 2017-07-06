package tel.schich.httprequestrouter.segment.constraint;

import java.util.Objects;
import java.util.regex.Pattern;

public class PatternConstraint implements Constraint {
    private final Pattern pattern;

    public PatternConstraint(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean test(String path, int start, int end) {
        return pattern.matcher(path).region(start, end).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatternConstraint)) return false;
        PatternConstraint that = (PatternConstraint) o;
        return Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern);
    }

    @Override
    public String toString() {
        return "PatternConstraint(" + pattern + ')';
    }
}
