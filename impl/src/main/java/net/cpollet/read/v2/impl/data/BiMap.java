package net.cpollet.read.v2.impl.data;

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
