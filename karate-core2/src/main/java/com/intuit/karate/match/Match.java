/*
 * The MIT License
 *
 * Copyright 2020 Intuit Inc.
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
package com.intuit.karate.match;

import com.intuit.karate.graal.JsEngine;
import static com.intuit.karate.match.MatchResult.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 * @author pthomas3
 */
public class Match {

    public interface Validator extends Function<MatchValue, MatchResult> {
        //
    }

    public static final Map<String, Validator> VALIDATORS = new HashMap(11);

    static {
        VALIDATORS.put("array", v -> v.isList() ? PASS : fail("not an array or list"));
        VALIDATORS.put("boolean", v -> v.isBoolean() ? PASS : fail("not a boolean"));
        VALIDATORS.put("ignore", v -> PASS);
        VALIDATORS.put("notnull", v -> v.isNull() ? fail("null") : PASS);
        VALIDATORS.put("null", v -> v.isNull() ? PASS : fail("not null"));
        VALIDATORS.put("number", v -> v.isNumber() ? PASS : fail("not a number"));
        VALIDATORS.put("object", v -> v.isMap() ? PASS : fail("not an object or map"));
        VALIDATORS.put("present", v -> v.isNotPresent() ? fail("not present") : PASS);
        VALIDATORS.put("notpresent", v -> v.isNotPresent() ? PASS : fail("present"));
        VALIDATORS.put("string", v -> v.isNotPresent() ? fail("not present") : v.isString() ? PASS : fail("not a string"));
        VALIDATORS.put("uuid", v -> {
            if (!v.isString()) {
                return fail("not a string");
            }
            try {
                UUID.fromString(v.getValue());
                return PASS;
            } catch (Exception e) {
                return fail("not a valid uuid");
            }
        });
    }

    public static MatchValue that(Object o) {
        return new MatchValue(MatchValue.parseIfJsonOrXml(o));
    }

    public static MatchResult execute(JsEngine js, MatchType matchType, MatchValue actual, MatchValue expected) {
        MatchOperation mo = new MatchOperation(js, matchType, actual, expected);
        mo.execute();
        if (mo.pass) {
            return PASS;
        } else {
            return fail(mo.getFailureReasons());
        }
    }

}