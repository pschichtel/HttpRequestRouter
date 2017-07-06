package tel.schich.httprequestrouter.segment;

import tel.schich.httprequestrouter.RouteSegment;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class SegmentOrder<TMethod, TRequest, TResponse> implements Comparator<RouteSegment<TMethod, TRequest, TResponse>> {

    @SafeVarargs
    public static <TMethod, TRequest, TResponse> SegmentOrder<TMethod, TRequest, TResponse> order(Class<? extends Segment>... segmentClasses) {
        final Map<Class<? extends Segment>, Integer> prios = new IdentityHashMap<>();
        for (int i = 1; i < segmentClasses.length; ++i) {
            prios.put(segmentClasses[i], i);
        }

        return new SegmentOrder<TMethod, TRequest, TResponse>() {
            @Override
            public int compare(RouteSegment<TMethod, TRequest, TResponse> left, RouteSegment<TMethod, TRequest, TResponse> right) {
                Integer leftPrio = prios.getOrDefault(left.segment.getClass(), Integer.MAX_VALUE);
                Integer rightPrio = prios.getOrDefault(right.segment.getClass(), Integer.MAX_VALUE);

                return leftPrio.compareTo(rightPrio);
            }
        };
    }

}
