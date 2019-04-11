package net.cpollet.read.v2.api.methods;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CreateResult<T extends Id> {
    private final T id;
    private final Collection<String> errors;

    private CreateResult(T id, Collection<String> errors) {
        this.id = id;
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public CreateResult(T id) {
        this(id, Collections.emptySet());
    }

    public CreateResult(Collection<String> errors) {
        this(null, errors);
    }

    public Optional<T> id() {
        return Optional.ofNullable(id);
    }

    public Collection<String> errors() {
        return errors;
    }
}
