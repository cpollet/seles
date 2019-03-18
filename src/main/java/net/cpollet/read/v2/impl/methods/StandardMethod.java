package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StandardMethod<IdType extends Id> implements Method<IdType> {
    @Override
    public FetchResult<IdType> fetch(List<AttributeDef<IdType>> attributes, Collection<IdType> ids) {
        return new FetchResult<>(
                ids.stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> attributes.stream()
                                        .collect(Collectors.toMap(
                                                a -> a,
                                                a -> a.name() + ":" + id.get()
                                        ))
                                )

                        )
        );
    }
}
