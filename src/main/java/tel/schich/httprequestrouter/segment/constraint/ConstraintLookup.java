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
package tel.schich.httprequestrouter.segment.constraint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConstraintLookup {

    private final Constraint defaultConstraint;
    private final Map<String, Constraint> constraints;

    private ConstraintLookup(Constraint defaultConstraint, Map<String, Constraint> constraints) {
        this.defaultConstraint = defaultConstraint;
        this.constraints = constraints;
    }

    public Constraint getDefault() {
        return defaultConstraint;
    }

    public Optional<Constraint> lookup(String name) {
        return Optional.ofNullable(constraints.get(name));
    }

    public ConstraintLookup with(String name, Constraint constraint) {
        Map<String, Constraint> constraints = new HashMap<>(this.constraints);
        constraints.put(name, constraint);
        return new ConstraintLookup(defaultConstraint, constraints);
    }

    public ConstraintLookup defaultingTo(Constraint constraint) {
        return new ConstraintLookup(constraint, constraints);
    }

    public static ConstraintLookup create(Constraint defaultConstraint) {
        return new ConstraintLookup(defaultConstraint, Collections.emptyMap());
    }
}
