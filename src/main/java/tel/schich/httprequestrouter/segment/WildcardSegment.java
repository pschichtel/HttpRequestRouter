package tel.schich.httprequestrouter.segment;

import java.util.Objects;

public class WildcardSegment implements NamedSegment {
    private final String name;

    public WildcardSegment(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canHaveChildren() {
        return false;
    }

    @Override
    public int matches(String path, int start) {
        return path.length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardSegment)) return false;
        WildcardSegment that = (WildcardSegment) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "WildcardSegment(" + name + ')';
    }
}
