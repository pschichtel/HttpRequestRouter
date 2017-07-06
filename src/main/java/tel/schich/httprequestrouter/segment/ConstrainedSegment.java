package tel.schich.httprequestrouter.segment;

import tel.schich.httprequestrouter.RouteParser;
import tel.schich.httprequestrouter.segment.constraint.Constraint;

import java.util.Objects;

public class ConstrainedSegment implements NamedSegment {

    private final String name;
    private final Constraint constraint;

    public ConstrainedSegment(String name, Constraint constraint) {
        this.name = name;
        this.constraint = constraint;
    }

    @Override
    public String getName() {
        return name;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    @Override
    public boolean canHaveChildren() {
        return false;
    }

    @Override
    public int matches(String path, int start) {
        int end = path.indexOf(RouteParser.SEPARATOR, start);
        if (end == -1) {
            end = path.length();
        }
        return constraint.test(path, start, end) ? end : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstrainedSegment)) return false;
        ConstrainedSegment that = (ConstrainedSegment) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, constraint);
    }

    @Override
    public String toString() {
        return "ConstrainedSegment(" + name + ", " + constraint + ')';
    }
}
