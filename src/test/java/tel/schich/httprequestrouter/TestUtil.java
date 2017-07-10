package tel.schich.httprequestrouter;

import tel.schich.httprequestrouter.segment.Segment;
import tel.schich.httprequestrouter.segment.StaticSegment;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static tel.schich.httprequestrouter.segment.RootSegment.ROOT;

public class TestUtil {

    public static Segment seg(String content) {
        return new StaticSegment(content);
    }

    public static List<Segment> route(Segment... segments) {
        List<Segment> route = new ArrayList<>(segments.length + 1);
        route.addAll(asList(segments));
        return route;
    }

}
