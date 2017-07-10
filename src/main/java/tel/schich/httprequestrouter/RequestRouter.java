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
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RequestRouter<TMethod, TRequest, TResponse> {

    private final SegmentFactory segmentFactory;
    private final RouteTree<TMethod, TRequest, TResponse> routeTree;

    public RequestRouter(SegmentFactory segmentFactory, SegmentOrder<TMethod, TRequest, TResponse> order) {
        this(segmentFactory, RouteTree.create(order));
    }

    public RequestRouter(SegmentFactory segmentFactory, RouteTree<TMethod, TRequest, TResponse> routeTree) {
        this.segmentFactory = segmentFactory;
        this.routeTree = routeTree;
    }

    public Optional<Function<TRequest, TResponse>> routeRequest(TMethod method, String path) {

        RouteTree<TMethod, TRequest, TResponse> subTree = routeTree;
        int routeOffset = 0;
        while (routeOffset < path.length()) {
            // skip char if it's a slash
            if (path.charAt(routeOffset) == RouteParser.SEPARATOR) {
                routeOffset++;
            } else {
                Optional<RouteTree.Match<TMethod, TRequest, TResponse>> optionalMatch = routeTree.matchChild(path, routeOffset);
                if (optionalMatch.isPresent()) {
                    RouteTree.Match<TMethod, TRequest, TResponse> match = optionalMatch.get();
                    routeOffset = match.endedAt;
                    subTree = match.child;
                } else {
                    break;
                }
            }
        }

        if (routeOffset >= path.length()) {
            return subTree.getHandler(method);
        }

        return Optional.empty();
    }

    public RequestRouter<TMethod, TRequest, TResponse> withHandler(TMethod method, String route, Function<TRequest, TResponse> handler) {
        List<Segment> parsedRoute = RouteParser.parseRoute(route, segmentFactory);
        return new RequestRouter<>(segmentFactory, routeTree.addHandler(method, parsedRoute, handler));
    }

}
