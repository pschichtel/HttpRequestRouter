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
import tel.schich.httprequestrouter.segment.factory.StaticSegmentFactory;
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static tel.schich.httprequestrouter.segment.RootSegment.ROOT;

public class RouteParser {
    public static final char SEPARATOR = '/';
    public static final SegmentFactory DEFAULT_FACTORY = new StaticSegmentFactory();
    private static final char ESCAPE = '\\';
    private static final List<Segment> ROOT_ROUTE = singletonList(ROOT);

    @NotNull
    public static List<Segment> parseRoute(@NotNull String route, @NotNull SegmentFactory factory) {

        if (route.charAt(0) != SEPARATOR) {
            throw new IllegalArgumentException("Routes must start with a " + SEPARATOR);
        }

        if (route.length() == 1) {
            return ROOT_ROUTE;
        }

        List<Segment> segments = new ArrayList<>(ROOT_ROUTE);

        int end = route.length();
        // initialize with 1 to skip leading slash
        int i = 1;
        int start;
        boolean escaped;
        char current;

        while (i < end) {
            start = i;
            escaped = false;

            while (i < end) {
                current = route.charAt(i);
                if (current == ESCAPE && i + 1 < end) {
                    i += 2;
                    escaped = true;
                } else if (current == SEPARATOR) {
                    break;
                } else {
                    i++;
                }
            }

            String content;
            if (escaped) {
                content = unescape(route, start, i);
            } else {
                content = route.substring(start, i);
            }
            i++;

            segments.add(factory.toSegment(content));
        }

        return segments;

    }

    private static String unescape(String s, int start, int end) {
        StringBuilder out = new StringBuilder(end - start);
        int i = start;
        char current;
        char next;
        while (i < end) {
            current = s.charAt(i);
            if (current == ESCAPE && i + 1 < end) {
                next = s.charAt(++i);
                if (next == ESCAPE || next == SEPARATOR) {
                    out.append(next);
                } else {
                    out.append(current).append(next);
                }
            } else {
                out.append(current);
            }
            i++;
        }

        return out.toString();
    }

}
