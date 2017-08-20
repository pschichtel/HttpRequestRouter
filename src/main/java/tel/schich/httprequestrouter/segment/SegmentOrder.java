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
package tel.schich.httprequestrouter.segment;

import tel.schich.httprequestrouter.RouteSegment;

import java.util.*;

public class SegmentOrder<T> implements Comparator<RouteSegment<T>> {

    private final Set<Class<? extends Segment>> supportedClasses = new HashSet<>();
    private final Map<Class<? extends Segment>, Integer> priorities = new IdentityHashMap<>();

    public SegmentOrder(Class<? extends Segment>[] segmentClasses) {
        for (int i = 0; i < segmentClasses.length; ++i) {
            supportedClasses.add(segmentClasses[i]);
            priorities.put(segmentClasses[i], i);
        }
    }

    public boolean isSupported(Segment segment) {
        return isSupported(segment.getClass());
    }

    public boolean isSupported(Class<? extends Segment> segmentClass) {
        return supportedClasses.contains(segmentClass);
    }

    @Override
    public int compare(RouteSegment<T> left, RouteSegment<T> right) {
        Class<? extends Segment> leftClass = left.segment.getClass();
        Class<? extends Segment> rightClass = right.segment.getClass();

        if (leftClass == rightClass) {
            if (Comparable.class.isAssignableFrom(leftClass)) {
                @SuppressWarnings("unchecked")
                Comparable<Segment> comparableLeft = (Comparable<Segment>)left.segment;
                return comparableLeft.compareTo(right.segment);
            } else {
                return 0;
            }
        } else {
            return Integer.compare(priorities.get(leftClass), priorities.get(rightClass));
        }
    }

    @SafeVarargs
    public static <T> SegmentOrder<T> order(Class<? extends Segment>... segmentClasses) {
        return new SegmentOrder<>(segmentClasses);
    }

}
