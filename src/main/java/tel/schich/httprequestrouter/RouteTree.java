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

import tel.schich.httprequestrouter.segment.Segment;
import tel.schich.httprequestrouter.segment.SegmentOrder;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class RouteTree<T> {

    private final SegmentOrder<T> order;
    private final List<RouteSegment<T>> children;
    private final Map<String, T> handlers;

    private RouteTree(SegmentOrder<T> order, List<RouteSegment<T>> children, Map<String, T> handlers) {
        this.order = order;
        this.children = children;
        this.handlers = handlers;
    }

    public Optional<Match<T>> matchChild(java.lang.String path, int from) {
        if (from >= path.length()) {
            return Optional.empty();
        }

        for (RouteSegment<T> next : children) {
            int match = next.segment.matches(path, from);
            if (match > -1) {
                return Optional.of(new Match<>(next.segment, next.subTree, match));
            }
        }

        return Optional.empty();
    }

    public Optional<T> getHandler(String method) {
        return Optional.ofNullable(handlers.get(method));
    }

    private Optional<RouteTree<T>> findChildFor(Segment segment) {
        for (RouteSegment<T> child : children) {
            if (child.segment.equals(segment)) {
                return Optional.of(child.subTree);
            }
        }
        return Optional.empty();
    }

    public RouteTree<T> addHandler(String method, List<Segment> route, T handler) {
        if (route.isEmpty()) {
            HashMap<String, T> newHandlers = new HashMap<>(this.handlers);
            newHandlers.put(method, handler);
            return new RouteTree<>(order, children, newHandlers);
        } else {
            Segment head = route.get(0);
            if (!order.isSupported(head)) {
                throw new IllegalArgumentException("The supplied segment order does not support segments of type " + head.getClass().getName() + " !");
            }

            List<Segment> rest = route.subList(1, route.size());
            Optional<RouteTree<T>> optionalChild = findChildFor(head);

            List<RouteSegment<T>> newChildren;
            if (optionalChild.isPresent()) {
                RouteTree<T> child = optionalChild.get();
                RouteTree<T> newSubTree = child.addHandler(method, rest, handler);
                newChildren = new ArrayList<>(children.size());
                for (RouteSegment<T> routeSegment : children) {
                    if (routeSegment.subTree == child) {
                        newChildren.add(new RouteSegment<>(head, newSubTree));
                    } else {
                        newChildren.add(routeSegment);
                    }
                }
                // no sorting needed here, because the order is retained
            } else {
                if (!head.isConsistentWith(children.stream().map(rs -> rs.segment).collect(toList()))) {
                    throw new IllegalArgumentException("One of the segments would not be consistent with its siblings!");
                }
                newChildren = new ArrayList<>(children);
                RouteTree<T> newRouteTree = RouteTree.<T>create(order).addHandler(method, rest, handler);
                RouteSegment<T> newRouteSegment = new RouteSegment<>(head, newRouteTree);
                newChildren.add(newRouteSegment);
                newChildren.sort(order);
            }

            return new RouteTree<>(order, newChildren, handlers);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> RouteTree<T> create(SegmentOrder order) {
        return new RouteTree<>(order, Collections.emptyList(), Collections.emptyMap());
    }

    public static class Match<T> {
        public final Segment segment;
        public final RouteTree<T> child;
        public final int endedAt;

        public Match(Segment segment, RouteTree<T> child, int endedAt) {
            this.segment = segment;
            this.child = child;
            this.endedAt = endedAt;
        }
    }
}
