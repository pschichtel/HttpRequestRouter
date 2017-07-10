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

import org.junit.jupiter.api.Test;
import tel.schich.httprequestrouter.segment.Segment;
import tel.schich.httprequestrouter.segment.StaticSegment;
import tel.schich.httprequestrouter.segment.constraint.ConstraintLookup;
import tel.schich.httprequestrouter.segment.constraint.PatternConstraint;
import tel.schich.httprequestrouter.segment.constraint.UnknownConstrainException;
import tel.schich.httprequestrouter.segment.factory.DynamicSegmentFactory;
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

import java.util.List;

import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.*;
import static tel.schich.httprequestrouter.RouteParser.DEFAULT_FACTORY;
import static tel.schich.httprequestrouter.RouteParser.parseRoute;
import static tel.schich.httprequestrouter.TestUtil.*;

class RouteParserTest {

    @Test
    void testParseRoute() {
        assertEquals(route(), parseRoute("/", DEFAULT_FACTORY));
        assertEquals(route(stat("a")), parseRoute("/a", DEFAULT_FACTORY));
        assertEquals(route(stat("a"), stat("b")), parseRoute("/a/b", DEFAULT_FACTORY));
        assertEquals(route(stat("a/b")), parseRoute("/a\\/b", DEFAULT_FACTORY));
        assertEquals(route(stat("a\\"), stat("b")), parseRoute("/a\\\\/b", DEFAULT_FACTORY));
        assertEquals(route(stat("a\\b")), parseRoute("/a\\b", DEFAULT_FACTORY));
    }

    @Test
    void testRouteValidation() {
        assertThrows(IllegalArgumentException.class, () -> parseRoute("a", DEFAULT_FACTORY));
    }

    @Test
    void emptySegment() {
        List<Segment> staticRoute = parseRoute("///", DEFAULT_FACTORY);
        assertEquals(2, staticRoute.size());
        assertSame(StaticSegment.EMPTY, staticRoute.get(0));
        assertSame(StaticSegment.EMPTY, staticRoute.get(1));

        List<Segment> dynamicRoute = parseRoute("///", new DynamicSegmentFactory(ConstraintLookup.create()));
        assertEquals(2, dynamicRoute.size());
        assertSame(StaticSegment.EMPTY, dynamicRoute.get(0));
        assertSame(StaticSegment.EMPTY, dynamicRoute.get(1));
    }

    @Test
    void constrainedSegments() {
        PatternConstraint ic = new PatternConstraint(compile("0|[1-9]\\d+"));
        PatternConstraint nc = new PatternConstraint(compile("\\d+"));
        PatternConstraint tc = new PatternConstraint(compile("test[123]"));

        ConstraintLookup constraints = ConstraintLookup.create()
                .with("integer", ic)
                .with("numeric", nc)
                .with("test", tc);

        final SegmentFactory factory = new DynamicSegmentFactory(constraints);

        assertEquals(route(stat("pre"), constr("a", ic)), parseRoute("/pre/:a{integer}", factory));
        assertEquals(route(stat("pre"), constr("b", nc)), parseRoute("/pre/:b{numeric}", factory));
        assertEquals(route(stat("pre"), constr("c", tc), constr("d", ic)), parseRoute("/pre/:c{test}/:d{integer}", factory));
        assertThrows(UnknownConstrainException.class, () -> parseRoute("/pre/:e{unknown}", factory));
    }

    @Test
    void unconstrainedSegments() {
        final SegmentFactory factory = new DynamicSegmentFactory(ConstraintLookup.create());

        assertEquals(route(stat("pre"), unconstr("a")), parseRoute("/pre/:a", factory));
        assertEquals(route(stat("pre"), unconstr("a}{")), parseRoute("/pre/:a}{", factory));
        assertEquals(route(stat("pre"), unconstr("a}")), parseRoute("/pre/:a}", factory));
    }

    @Test
    void unbouncedSegments() {
        final SegmentFactory factory = new DynamicSegmentFactory(ConstraintLookup.create());

        assertEquals(route(stat("pre"), unbound("a")), parseRoute("/pre/*a", factory));
        assertEquals(route(stat("pre"), unbound("a}{")), parseRoute("/pre/*a}{", factory));
        assertEquals(route(stat("pre"), unbound("a}")), parseRoute("/pre/*a}", factory));
    }
}