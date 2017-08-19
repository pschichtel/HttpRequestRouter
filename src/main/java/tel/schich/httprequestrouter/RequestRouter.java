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

import tel.schich.httprequestrouter.segment.NamedSegment;
import tel.schich.httprequestrouter.segment.Segment;
import tel.schich.httprequestrouter.segment.SegmentOrder;
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RequestRouter<Verb, In, Out> {

    private final SegmentFactory segmentFactory;
    private final RouteTree<Verb, In, Out> routeTree;

    public RequestRouter(SegmentFactory segmentFactory, SegmentOrder<Verb, In, Out> order) {
        this(segmentFactory, RouteTree.create(order));
    }

    public RequestRouter(SegmentFactory segmentFactory, RouteTree<Verb, In, Out> routeTree) {
        this.segmentFactory = segmentFactory;
        this.routeTree = routeTree;
    }

    public Function<In, CompletableFuture<Out>> routeRequest(Verb method, String path, RouteFallbackHandler<Verb, In, Out> fallbackHandler) {

        RouteTree<Verb, In, Out> subTree = routeTree;
        Map<String, String> namedValues = new HashMap<>();
        int routeOffset = 0;
        while (routeOffset < path.length()) {
            // skip char if it's a slash
            if (path.charAt(routeOffset) == RouteParser.SEPARATOR) {
                routeOffset++;
            } else {
                Optional<RouteTree.Match<Verb, In, Out>> optionalMatch = routeTree.matchChild(path, routeOffset);
                if (optionalMatch.isPresent()) {
                    RouteTree.Match<Verb, In, Out> match = optionalMatch.get();
                    if (match.segment instanceof NamedSegment) {
                        namedValues.put(((NamedSegment) match.segment).getName(), path.substring(routeOffset, match.endedAt));
                    }
                    routeOffset = match.endedAt;
                    subTree = match.child;
                } else {
                    break;
                }
            }
        }
        final RouteTree<Verb, In, Out> finalSubtree = subTree;
        final String matchedPrefix = path.substring(0, routeOffset);

        if (routeOffset >= path.length()) {
            return subTree
                    .getHandler(method)
                    .map((handler) -> ((Function<In, CompletableFuture<Out>>) req -> handler.handle(new RoutedRequest<>(namedValues, req))))
                    .orElseGet(() -> req -> fallbackHandler.handle(matchedPrefix, finalSubtree, req));
        } else {
            return req -> fallbackHandler.handle(matchedPrefix, finalSubtree, req);
        }
    }

    public RequestRouter<Verb, In, Out> withHandler(Verb method, String route, RouteHandler<In, Out> handler) {
        List<Segment> parsedRoute = RouteParser.parseRoute(route, segmentFactory);
        return new RequestRouter<>(segmentFactory, routeTree.addHandler(method, parsedRoute, handler));
    }

}
