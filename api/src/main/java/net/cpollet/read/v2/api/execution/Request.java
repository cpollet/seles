package net.cpollet.read.v2.api.execution;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a request. The instance is immutable and once created, the ids and attributes collections are wrapped
 * in an unmodifiable collection.
 *
 * @param <IdType> the java type of the entity's primary key
 */
public class Request<IdType> {
    private final Collection<IdType> ids;
    private final Collection<String> attributes;
    private final Map<String, Object> attributesValues;

    private Request(Collection<IdType> ids, Collection<String> attributes, Map<String, Object> attributesValues) {
        this.ids = Collections.unmodifiableCollection(ids);
        this.attributes = Collections.unmodifiableCollection(attributes);
        this.attributesValues = Collections.unmodifiableMap(attributesValues);
    }

    public static <IdType> Request<IdType> read(Collection<IdType> ids, Collection<String> attributes) {
        return new Request<>(ids, attributes, Collections.emptyMap());
    }

    public static <IdType> Request<IdType> delete(Collection<IdType> ids, Collection<String> attributes) {
        return new Request<>(ids, attributes, Collections.emptyMap());
    }

    public static <IdType> Request<IdType> write(Collection<IdType> ids, Map<String, Object> attributesValues) {
        return new Request<>(ids, attributesValues.keySet(), attributesValues);
    }

    public static <IdType> Request<IdType> create(Map<String, Object> attributesValues) {
        return new Request<>(Collections.emptyList(), attributesValues.keySet(), attributesValues);
    }

    public Collection<IdType> getIds() {
        return ids;
    }

    public Collection<String> getAttributes() {
        return attributes;
    }

    public Map<String, Object> getAttributesValues() {
        return attributesValues;
    }
}
