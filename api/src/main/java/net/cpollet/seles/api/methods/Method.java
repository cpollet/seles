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
package net.cpollet.seles.api.methods;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Method<T extends Id> {
    FetchResult<T> fetch(Principal principal, List<AttributeDef<T>> attributes, Collection<T> ids);

    Collection<String> update(Principal principal, Map<AttributeDef<T>, Object> attributeValues, Collection<T> ids);

    Collection<String> delete(Principal principal, List<AttributeDef<T>> attributes, Collection<T> ids);

    CreateResult<T> create(Principal principal, Map<AttributeDef<T>, Object> values);

    SearchResult<T> search(Principal principal, Map<AttributeDef<T>, Object> values);
}
