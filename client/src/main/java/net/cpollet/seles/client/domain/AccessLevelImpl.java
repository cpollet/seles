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

import net.cpollet.seles.api.attribute.AccessLevel;

public class AccessLevelImpl implements AccessLevel {
    public static final AccessLevel PUBLIC = new AccessLevelImpl("PUBLIC");
    public static final AccessLevel PRIVATE = new AccessLevelImpl("PRIVATE");

    private final String level;

    private AccessLevelImpl(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return level;
    }
}
