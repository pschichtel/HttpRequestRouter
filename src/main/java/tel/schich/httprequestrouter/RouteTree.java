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
import java.util.function.Function;

public class RouteTree<TMethod, TRequest, TResponse> {

    private final SegmentOrder<TMethod, TRequest, TResponse> order;
    private final List<RouteSegment<TMethod, TRequest, TResponse>> children;
    private final Map<TMethod, Function<TRequest, TResponse>> handlers;

    private RouteTree(SegmentOrder<TMethod, TRequest, TResponse> order, List<RouteSegment<TMethod, TRequest, TResponse>> children, Map<TMethod, Function<TRequest, TResponse>> handlers) {
        this.order = order;
        this.children = children;
        this.handlers = handlers;
    }

    public Optional<Match<TMethod, TRequest, TResponse>> matchChild(String path, int from) {
        if (from >= path.length()) {
            return Optional.empty();
        }

        for (RouteSegment<TMethod, TRequest, TResponse> next : children) {
            int match = next.segment.matches(path, from);
            if (match > -1) {
                return Optional.of(new Match<>(next.subTree, match));
            }
        }

        return Optional.empty();
    }

    public Optional<Function<TRequest, TResponse>> getHandler(TMethod method) {
        return Optional.ofNullable(handlers.get(method));
    }

    private Optional<RouteTree<TMethod, TRequest, TResponse>> findChildFor(Segment segment) {
        for (RouteSegment<TMethod, TRequest, TResponse> child : children) {
            if (child.segment.equals(segment)) {
                return Optional.of(child.subTree);
            }
        }
        return Optional.empty();
    }

    public RouteTree<TMethod, TRequest, TResponse> addHandler(TMethod method, List<Segment> route, Function<TRequest, TResponse> handler) {
        if (route.isEmpty()) {
            HashMap<TMethod, Function<TRequest, TResponse>> newHandlers = new HashMap<>(this.handlers);
            newHandlers.put(method, handler);
            return new RouteTree<>(order, children, newHandlers);
        } else {
            Segment head = route.get(0);
            List<Segment> rest = route.subList(1, route.size());

            List<RouteSegment<TMethod, TRequest, TResponse>> newChildren;
            Optional<RouteTree<TMethod, TRequest, TResponse>> optionalChild = findChildFor(head);
            if (optionalChild.isPresent()) {
                RouteTree<TMethod, TRequest, TResponse> child = optionalChild.get();
                RouteTree<TMethod, TRequest, TResponse> newSubTree = child.addHandler(method, rest, handler);
                newChildren = new ArrayList<>(children.size());
                for (RouteSegment<TMethod, TRequest, TResponse> routeSegment : children) {
                    if (routeSegment.subTree == child) {
                        newChildren.add(new RouteSegment<>(head, newSubTree));
                    } else {
                        newChildren.add(routeSegment);
                    }
                }
                // no sorting needed here, because the order is retained
            } else {
                newChildren = new ArrayList<>(children);
                RouteTree<TMethod, TRequest, TResponse> newRouteTree = RouteTree.<TMethod, TRequest, TResponse>create(order).addHandler(method, rest, handler);
                RouteSegment<TMethod, TRequest, TResponse> newRouteSegment = new RouteSegment<>(head, newRouteTree);
                newChildren.add(newRouteSegment);
                newChildren.sort(order);
            }

            return new RouteTree<>(order, newChildren, handlers);
        }
    }

    @SuppressWarnings("unchecked")
    public static <TMethod, TRequest, TResponse> RouteTree<TMethod, TRequest, TResponse> create(SegmentOrder order) {
        return new RouteTree<>(order, Collections.emptyList(), Collections.emptyMap());
    }

    public static class Match<TMethod, TRequest, TResponse> {
        public final RouteTree<TMethod, TRequest, TResponse> child;
        public final int endedAt;

        public Match(RouteTree<TMethod, TRequest, TResponse> child, int endedAt) {
            this.child = child;
            this.endedAt = endedAt;
        }
    }
}
