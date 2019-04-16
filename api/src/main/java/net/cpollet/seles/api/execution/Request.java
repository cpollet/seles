package net.cpollet.seles.api.execution;

import net.cpollet.seles.api.domain.Id;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a request. The instance is immutable and once created, the ids and attributes collections are wrapped
 * in an unmodifiable collection.
 *
 * @param <T> the java type of the entity's primary key
 */
public final class Request<T extends Id> {
    private final Collection<T> ids;
    private final Collection<String> attributes;
    private final Map<String, Object> attributesValues;
    private final Principal principal; // FIXME make immutable

    private Request(Principal principal, Collection<T> ids, Collection<String> attributes, Map<String, Object> attributesValues) {
        this.principal = principal;
        this.ids = Collections.unmodifiableCollection(ids);
        this.attributes = Collections.unmodifiableCollection(attributes);
        this.attributesValues = Collections.unmodifiableMap(attributesValues);
    }

    public static <T extends Id> Request<T> read(Principal principal, Collection<T> ids, Collection<String> attributes) {
        return new Request<>(principal, ids, attributes, Collections.emptyMap());
    }

    public static <T extends Id> Request<T> delete(Principal principal, Collection<T> ids, Collection<String> attributes) {
        return new Request<>(principal, ids, attributes, Collections.emptyMap());
    }

    public static <T extends Id> Request<T> write(Principal principal, Collection<T> ids, Map<String, Object> attributesValues) {
        return new Request<>(principal, ids, attributesValues.keySet(), attributesValues);
    }

    public static <T extends Id> Request<T> create(Principal principal, Map<String, Object> attributesValues) {
        return new Request<>(principal, Collections.emptyList(), attributesValues.keySet(), attributesValues);
    }

    public static <T extends Id> Request<T> search(Principal principal, Map<String, Object> attributesValues) {
        return new Request<>(principal, Collections.emptyList(), attributesValues.keySet(), attributesValues);
    }

    public Collection<T> getIds() {
        return ids;
    }

    public Collection<String> getAttributes() {
        return attributes;
    }

    public Map<String, Object> getAttributesValues() {
        return attributesValues;
    }

    public Principal principal() {
        return principal;
    }
}
