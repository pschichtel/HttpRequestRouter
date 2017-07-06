package tel.schich.httprequestrouter;

import tel.schich.httprequestrouter.segment.Segment;

public class RouteSegment<TMethod, TRequest, TResponse> {
    public final Segment segment;
    public final RouteTree<TMethod, TRequest, TResponse> subTree;

    public RouteSegment(Segment segment, RouteTree<TMethod, TRequest, TResponse> subTree) {
        this.segment = segment;
        this.subTree = subTree;
    }
}
