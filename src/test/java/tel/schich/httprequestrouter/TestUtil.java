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
package tel.schich.httprequestrouter;

import tel.schich.httprequestrouter.segment.*;
import tel.schich.httprequestrouter.segment.constraint.Constraint;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TestUtil {

    public static Segment stat(String content) {
        return new StaticSegment(content);
    }

    public static Segment constr(String name, Constraint constraint) {
        return new ConstrainedSegment(name, constraint, null);
    }

    public static Segment unconstr(String name) {
        return new UnconstrainedSegment(name);
    }

    public static Segment unbound(String name) {
        return new UnboundedSegment(name);
    }

    public static List<Segment> route(Segment... segments) {
        List<Segment> route = new ArrayList<>(segments.length + 1);
        route.addAll(asList(segments));
        return route;
    }

}
