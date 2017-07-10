package tel.schich.httprequestrouter;

import org.junit.jupiter.api.Test;
import tel.schich.httprequestrouter.segment.Segment;
import tel.schich.httprequestrouter.segment.SegmentOrder;
import tel.schich.httprequestrouter.segment.StaticSegment;
import tel.schich.httprequestrouter.segment.UnboundedSegment;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static tel.schich.httprequestrouter.TestUtil.route;
import static tel.schich.httprequestrouter.TestUtil.seg;

class RouteTreeTest {
    @Test
    void matchChild() {
        RouteTree<Object, Object, Object> tree = RouteTree.create(SegmentOrder.order(StaticSegment.class, UnboundedSegment.class));
        assertFalse(tree.matchChild("", 1).isPresent());
    }

    @Test
    void getHandler() {
    }

    @Test
    void addHandler() {
        RouteTree<Object, Object, Object> tree = RouteTree.create(SegmentOrder.order(StaticSegment.class, UnboundedSegment.class));

        Function<Object, Object> handler = a -> null;
        List<Segment> route = route(seg("a"), seg("b"), seg("c"));
        Object method = new Object[0];

        RouteTree<Object, Object, Object> objectObjectObjectRouteTree = tree.addHandler(method, route, handler);
        System.out.println(objectObjectObjectRouteTree);
    }

    @Test
    void create() {
    }

}