/*
 * The MIT License
 * Copyright © 2017 Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tel.schich.httprequestrouter.segment;

import tel.schich.httprequestrouter.RouteParser;
import tel.schich.httprequestrouter.segment.constraint.Constraint;
import tel.schich.httprequestrouter.segment.constraint.ConstraintLookup;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ConstrainedSegment implements NamedSegment, Comparator<ConstrainedSegment> {

    private final String name;
    private final Constraint constraint;
    private final ConstraintLookup lookup;

    public ConstrainedSegment(String name, Constraint constraint, ConstraintLookup lookup) {
        this.name = name;
        this.constraint = constraint;
        this.lookup = lookup;
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

    @Override
    public boolean isConsistentWith(List<Segment> segments) {
        return true;
    }

    @Override
    public int compare(ConstrainedSegment left, ConstrainedSegment right) {
        return Integer.compare(lookup.getPriority(left.constraint), lookup.getPriority(right.constraint));
    }
}
