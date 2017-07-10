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
package tel.schich.httprequestrouter.segment.factory;

import tel.schich.httprequestrouter.segment.*;
import tel.schich.httprequestrouter.segment.constraint.Constraint;
import tel.schich.httprequestrouter.segment.constraint.ConstraintLookup;
import tel.schich.httprequestrouter.segment.constraint.UnknownConstrainException;

import java.util.Optional;

public class DynamicSegmentFactory implements SegmentFactory {

    private final ConstraintLookup constraints;

    public DynamicSegmentFactory(ConstraintLookup constraints) {
        this.constraints = constraints;
    }

    @Override
    public Segment toSegment(String raw) {
        if (raw.isEmpty()) {
            return StaticSegment.EMPTY;
        }

        switch (raw.charAt(0)) {
            case ':':
                return parseConstrained(raw, constraints);
            case '*':
                return parseWildcard(raw);
            default:
                return new StaticSegment(raw);
        }
    }

    private Segment parseConstrained(String raw, ConstraintLookup constraints) {
        int openingBrace = raw.indexOf('{', 1);
        if (openingBrace == -1) {
            return new UnconstrainedSegment(raw.substring(1));
        }
        int closingBrace = raw.indexOf('}', openingBrace);
        if (closingBrace == -1 || closingBrace != raw.length() - 1) {
            return new UnconstrainedSegment(raw.substring(1));
        }

        String name = raw.substring(0, openingBrace);
        String constraintName = raw.substring(openingBrace + 1, closingBrace);

        Optional<Constraint> constraint = constraints.lookup(constraintName);
        if (constraint.isPresent()) {
            return new ConstrainedSegment(name, constraint.get());
        } else {
            throw new UnknownConstrainException(constraintName);
        }
    }

    private Segment parseWildcard(String raw) {
        return new UnboundedSegment(raw.substring(1));
    }
}
