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
