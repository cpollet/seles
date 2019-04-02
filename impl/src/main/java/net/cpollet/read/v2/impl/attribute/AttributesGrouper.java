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
 * Groups a collection of attributes by method. The result is a map from method to a collection of attributes using this method
 *
 * @param <IdType>
 */
public class AttributesGrouper<IdType extends Id> implements Function<Collection<AttributeDef<IdType>>, Map<Method<IdType>, List<AttributeDef<IdType>>>> {
    @Override
    public Map<Method<IdType>, List<AttributeDef<IdType>>> apply(Collection<AttributeDef<IdType>> attributeDefs) {
        Map<Method<IdType>, List<AttributeDef<IdType>>> attributesGroupedByMethod = new HashMap<>();

        for (AttributeDef<IdType> attribute : attributeDefs) {
            attributesGroupedByMethod.putIfAbsent(attribute.method(), new ArrayList<>());
            attributesGroupedByMethod.get(attribute.method()).add(attribute);
        }

        return attributesGroupedByMethod;
    }
}
