package net.cpollet.read.v2.impl.conversion;

import java.util.Collection;
import java.util.Collections;

public class ConversionResult<T> {
    private final T result;
    private final Collection<String> errors;

    public ConversionResult(T result, Collection<String> errors) {
        this.result = result;
        this.errors = errors;
    }

    public ConversionResult(T result) {
        this(result, Collections.emptyList());
    }

    public T result() {
        return result;
    }

    public Collection<String> errors() {
        return errors;
    }
}
