package net.cpollet.read.v2.impl.attribute;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.methods.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Groups a collection of {@link AttributeDef} by {@link Method}. The result is a map from method to a collection of
 * attributes using this method.
 *
 * @param <T>
 */
public class AttributesGrouper<T extends Id> implements Function<Collection<AttributeDef<T>>, Map<Method<T>, List<AttributeDef<T>>>> {
    @Override
    public Map<Method<T>, List<AttributeDef<T>>> apply(Collection<AttributeDef<T>> attributeDefs) {
        Map<Method<T>, List<AttributeDef<T>>> attributesGroupedByMethod = new HashMap<>();

        for (AttributeDef<T> attribute : attributeDefs) {
            attributesGroupedByMethod.putIfAbsent(attribute.method(), new ArrayList<>());
            attributesGroupedByMethod.get(attribute.method()).add(attribute);
        }

        return attributesGroupedByMethod;
    }
}
