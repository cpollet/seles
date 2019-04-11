package net.cpollet.read.v2.api.execution;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a request. The instance is immutable and once created, the ids and attributes collections are wrapped
 * in an unmodifiable collection.
 *
 * @param <T> the java type of the entity's primary key
 */
public class Request<T extends Id> {
    private final Collection<T> ids;
    private final Collection<String> attributes;
    private final Map<String, Object> attributesValues;

    private Request(Collection<T> ids, Collection<String> attributes, Map<String, Object> attributesValues) {
        this.ids = Collections.unmodifiableCollection(ids);
        this.attributes = Collections.unmodifiableCollection(attributes);
        this.attributesValues = Collections.unmodifiableMap(attributesValues);
    }

    public static <T extends Id> Request<T> read(Collection<T> ids, Collection<String> attributes) {
        return new Request<>(ids, attributes, Collections.emptyMap());
    }

    public static <T extends Id> Request<T> delete(Collection<T> ids, Collection<String> attributes) {
        return new Request<>(ids, attributes, Collections.emptyMap());
    }

    public static <T extends Id> Request<T> write(Collection<T> ids, Map<String, Object> attributesValues) {
        return new Request<>(ids, attributesValues.keySet(), attributesValues);
    }

    public static <T extends Id> Request<T> create(Map<String, Object> attributesValues) {
        return new Request<>(Collections.emptyList(), attributesValues.keySet(), attributesValues);
    }

    public static <T extends Id> Request<T> search(Map<String, Object> attributesValues) {
        return new Request<>(Collections.emptyList(), attributesValues.keySet(), attributesValues);
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
}
