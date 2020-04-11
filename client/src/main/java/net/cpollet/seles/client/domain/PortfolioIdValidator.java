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
package net.cpollet.seles.client.domain;

import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.domain.IdValidator;

import java.util.Collection;
import java.util.Collections;

/**
 * This is an example of a IdValidator for the Portfolio context. If statically reports portfolioId 999999 as an invalid
 * portfolioId.
 */
public class PortfolioIdValidator implements IdValidator {
    @Override
    public Collection<Id> invalidIds(Collection<Id> ids) {
        if (ids.contains(new PortfolioId("999999"))) {
            return Collections.singleton(new PortfolioId("999999"));
        }

        return Collections.emptyList();
    }
}
