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
package net.cpollet.seles.api.attribute;

import java.util.Collection;
import java.util.Optional;

/**
 * Holds the collection of {@link AttributeDef} that are handled and allows to retrieve them.
 */
public interface AttributeStore {
    /**
     * Returns an attribute by its name.
     *
     * @param attributeName the name of the attribute to fetch
     * @return an optional containing the attribute if one is found, an empty optional otherwise
     */
    Optional<AttributeDef> fetch(String attributeName);

    /**
     * The entity's ID attribute.
     *
     * @return an optional containing the attribute if one is found, an empty optional otherwise
     */
    Optional<AttributeDef> idAttribute();

    /**
     * All the known attributes.
     *
     * @return the list of all known attributes
     */
    Collection<AttributeDef> attributes();
}
