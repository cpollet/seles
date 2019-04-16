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
package net.cpollet.seles.impl.data;

import java.util.HashMap;
import java.util.Map;

public final class BiMap<L, R> {
    private final Map<L, R> leftToRight;
    private final Map<R, L> rightToLeft;

    private BiMap() {
        this.leftToRight = new HashMap<>();
        this.rightToLeft = new HashMap<>();
    }

    public BiMap(Map<L, R> map) {
        this();
        map.forEach((key, value) -> {
            leftToRight.put(key, value);
            rightToLeft.put(value, key);
        });
    }

    public void put(L left, R right) {
        if (leftToRight.containsKey(left) != rightToLeft.containsKey(right)) {
            throw new IllegalStateException();
        }

        leftToRight.put(left, right);
        rightToLeft.put(right, left);
    }

    public L getLeft(R right) {
        return rightToLeft.get(right);
    }

    public R getRight(L left) {
        return leftToRight.get(left);
    }

    public boolean rightContains(R right) {
        return rightToLeft.containsKey(right);
    }

    public boolean leftContains(L left) {
        return leftToRight.containsKey(left);
    }
}
