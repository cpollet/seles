/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.impl.attribute;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.methods.Method;

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
public final class AttributesGrouper<T extends Id> implements Function<Collection<AttributeDef<T>>, Map<Method<T>, List<AttributeDef<T>>>> {
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
