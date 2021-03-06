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
import tel.schich.httprequestrouter.segment.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static tel.schich.httprequestrouter.TestUtil.route;
import static tel.schich.httprequestrouter.TestUtil.stat;
import static tel.schich.httprequestrouter.TestUtil.unconstr;
import static tel.schich.httprequestrouter.TestUtil.GET;

class RouteTreeTest {
    @Test
    void matchChild() {
        RouteTree<Function<Object, Object>> tree = RouteTree.create(SegmentOrder.order(StaticSegment.class, UnboundedSegment.class));
        assertFalse(tree.matchChild("", 1).isPresent());
    }

    @Test
    void getHandler() {
        Function<Object, Object> handler = r -> null;
        RouteTree<Function<Object, Object>> tree = RouteTree.<Function<Object, Object>>create(SegmentOrder.order(StaticSegment.class))
                .addHandler(GET, route(), handler);

        Optional<Function<Object, Object>> optionalHandler = tree.getHandler(GET);
        assertTrue(optionalHandler.isPresent());
        assertSame(handler, optionalHandler.get());
    }

    @Test
    void addHandler() {
        SegmentOrder<Function<Object, Object>> order = SegmentOrder.order(StaticSegment.class, UnboundedSegment.class);
        RouteTree<Function<Object, Object>> tree = RouteTree.create(order);

        Function<Object, Object> handler = a -> null;
        List<Segment> route = route(stat("a"), stat("b"), stat("c"));

        RouteTree<Function<Object, Object>> newTree = tree.addHandler(GET, route, handler);
        assertNotSame(newTree, tree);
    }

    @Test
    void unsupportedSegment() {
        RouteTree<Function<Object, Object>> tree = RouteTree.create(SegmentOrder.order(UnconstrainedSegment.class));
        assertThrows(IllegalArgumentException.class, () -> tree.addHandler(GET, route(stat("a")), r -> null));
    }

    @Test
    void inconsistentSegment() {
        RouteTree<Function<Object, Object>> tree = RouteTree.<Function<Object, Object>>create(SegmentOrder.order(StaticSegment.class, UnconstrainedSegment.class))
                .addHandler(GET, route(stat("a"), unconstr("a")), r -> null);
        assertThrows(IllegalArgumentException.class, () -> tree.addHandler(GET, route(stat("a"), unconstr("b")), r -> null));
    }

    @Test
    void create() {
        SegmentOrder<Function<Object, Object>> order = SegmentOrder.order(UnconstrainedSegment.class, StaticSegment.class);
        RouteTree<Function<Object, Integer>> tree = RouteTree.<Function<Object, Integer>>create(order)
                .addHandler(GET, route(unconstr("a")), r -> 1)
                .addHandler(GET, route(stat("a")), r -> 2);

        Optional<RouteTree.Match<Function<Object, Integer>>> optionalChild = tree.matchChild("/a", 0);

        assertTrue(optionalChild.isPresent());
        Optional<Function<Object, Integer>> optionalHandler = optionalChild.get().child.getHandler(GET);
        assertTrue(optionalHandler.isPresent());
        assertEquals(Integer.valueOf(1), optionalHandler.get().apply(null));
    }

}