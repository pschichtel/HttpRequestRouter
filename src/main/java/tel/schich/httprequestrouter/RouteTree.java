package tel.schich.httprequestrouter;

import tel.schich.httprequestrouter.segment.SegmentOrder;

import java.util.*;
import java.util.function.Function;

public class RouteTree<TMethod, TRequest, TResponse> {

    private final SegmentOrder order;
    private final SortedSet<RouteSegment<TMethod, TRequest, TResponse>> children;
    private final Map<TMethod, Function<TRequest, TResponse>> handlers;

    private RouteTree(SegmentOrder order, SortedSet<RouteSegment<TMethod, TRequest, TResponse>> children, Map<TMethod, Function<TRequest, TResponse>> handlers) {
        this.order = order;
        this.children = children;
        this.handlers = handlers;
    }

    public MatchResult matchChild(String path, int from) {
        if (from >= path.length()) {
            return NoMatch.NO_MATCH;
        }

        for (RouteSegment<TMethod, TRequest, TResponse> next : children) {
            int match = next.segment.matches(path, from);
            if (match > -1) {
                return new Match<>(next.subTree, match);
            }
        }

        return NoMatch.NO_MATCH;
    }

    public Optional<Function<TRequest, TResponse>> getHandler(TMethod method) {
        return Optional.ofNullable(handlers.get(method));
    }

    @SuppressWarnings("unchecked")
    public static <TMethod, TRequest, TResponse> RouteTree<TMethod, TRequest, TResponse> create(SegmentOrder order) {
        return new RouteTree<>(order, new TreeSet<>(order), Collections.emptyMap());
    }

    public static abstract class MatchResult {

        public boolean hasMatched() {
            return this instanceof RouteTree.Match<?, ?, ?>;
        }

        @SuppressWarnings("unchecked")
        public <TMethod, TRequest, TResponse> Optional<Match<TMethod, TRequest, TResponse>> getMatch() {
            if (hasMatched()) {
                Match<TMethod, TRequest, TResponse> match = (Match<TMethod, TRequest, TResponse>) this;
                Optional.of(match);
            }
            return Optional.empty();
        }

    }

    public static class Match<TMethod, TRequest, TResponse> extends MatchResult {
        public final RouteTree<TMethod, TRequest, TResponse> child;
        public final int endedAt;

        public Match(RouteTree<TMethod, TRequest, TResponse> child, int endedAt) {
            this.child = child;
            this.endedAt = endedAt;
        }
    }

    public static class NoMatch extends MatchResult {
        public static final MatchResult NO_MATCH = new NoMatch();
    }
}
