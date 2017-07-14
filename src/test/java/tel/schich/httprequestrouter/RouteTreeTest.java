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

import static org.junit.jupiter.api.Assertions.*;
import static tel.schich.httprequestrouter.TestUtil.route;
import static tel.schich.httprequestrouter.TestUtil.stat;
import static tel.schich.httprequestrouter.TestUtil.unconstr;

class RouteTreeTest {
    @Test
    void matchChild() {
        RouteTree<Object, Object, Object> tree = RouteTree.create(SegmentOrder.order(StaticSegment.class, UnboundedSegment.class));
        assertFalse(tree.matchChild("", 1).isPresent());
    }

    @Test
    void getHandler() {
        Object[] method = new Object[0];
        RouteHandler<Object, Object> handler = r -> null;
        RouteTree<Object, Object, Object> tree = RouteTree.create(SegmentOrder.order(StaticSegment.class))
                .addHandler(method, route(), handler);

        Optional<RouteHandler<Object, Object>> optionalHandler = tree.getHandler(method);
        assertTrue(optionalHandler.isPresent());
        assertSame(handler, optionalHandler.get());
    }

    @Test
    void addHandler() {
        SegmentOrder<Object, Object, Object> order = SegmentOrder.order(StaticSegment.class, UnboundedSegment.class);
        RouteTree<Object, Object, Object> tree = RouteTree.create(order);

        RouteHandler<Object, Object> handler = a -> null;
        List<Segment> route = route(stat("a"), stat("b"), stat("c"));
        Object method = new Object[0];

        RouteTree<Object, Object, Object> newTree = tree.addHandler(method, route, handler);
        assertNotSame(newTree, tree);
    }

    @Test
    void unsupportedSegment() {
        RouteTree<Object, Object, Object> tree = RouteTree.create(SegmentOrder.order(UnconstrainedSegment.class));
        assertThrows(IllegalArgumentException.class, () -> tree.addHandler(new Object[0], route(stat("a")), r -> null));
    }

    @Test
    void inconsistentSegment() {
        RouteTree<Object, Object, Object> tree = RouteTree.create(SegmentOrder.order(StaticSegment.class, UnconstrainedSegment.class))
                .addHandler(new Object[0], route(stat("a"), unconstr("a")), r -> null);
        assertThrows(IllegalArgumentException.class, () -> tree.addHandler(new Object[0], route(stat("a"), unconstr("b")), r -> null));
    }

    @Test
    void create() {
        SegmentOrder<Object, Object, Integer> order = SegmentOrder.order(UnconstrainedSegment.class, StaticSegment.class);
        Object[] method = new Object[0];
        RouteTree<Object, Object, Integer> tree = RouteTree.<Object, Object, Integer>create(order)
                .addHandler(method, route(unconstr("a")), r -> 1)
                .addHandler(method, route(stat("a")), r -> 2);

        Optional<RouteTree.Match<Object, Object, Integer>> optionalChild = tree.matchChild("/a", 0);

        assertTrue(optionalChild.isPresent());
        Optional<RouteHandler<Object, Integer>> optionalHandler = optionalChild.get().child.getHandler(method);
        assertTrue(optionalHandler.isPresent());
        assertEquals(Integer.valueOf(1), optionalHandler.get().handle(null));
    }

}