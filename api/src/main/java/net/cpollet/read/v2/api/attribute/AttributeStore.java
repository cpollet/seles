package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Optional;

/**
 * Holds the collection of {@link AttributeDef} that are handled and allows to retrieve them.
 *
 * @param <T> the type of entity's ID, an implementation of {@link Id}
 */
public interface AttributeStore<T extends Id> {
    /**
     * Returns an attribute by its name.
     *
     * @param attributeName the name of the attribute to fetch
     * @return an optional containing the attribute if one is found, an empty optional otherwise
     */
    Optional<AttributeDef<T>> fetch(String attributeName);

    /**
     * The entity's ID attribute.
     *
     * @return an optional containing the attribute if one is found, an empty optional otherwise
     */
    Optional<AttributeDef<T>> idAttribute();

    /**
     * All the known attributes.
     *
     * @return the list of all known attributes
     */
    Collection<AttributeDef<T>> attributes();
}
