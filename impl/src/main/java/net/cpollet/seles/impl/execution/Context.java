package net.cpollet.seles.impl.execution;

import net.cpollet.seles.api.attribute.AccessLevelPredicate;
import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.IdValidator;

import java.util.List;
import java.util.function.Function;

public class Context {
    public final AttributeDef.Mode mode;
    public final AttributeStore attributeStore;
    public final IdValidator idValidator;
    public final AccessLevelPredicate filteringPredicate;
    public final DefaultExecutorGuard guard;
    public final List<Function<AttributeDef, ValueConverter<AttributeDef>>> convertersProvider;

    public Context(
            AttributeDef.Mode mode,
            AttributeStore attributeStore,
            IdValidator idValidator,
            AccessLevelPredicate filteringPredicate,
            DefaultExecutorGuard guard,
            List<Function<AttributeDef, ValueConverter<AttributeDef>>> convertersProvider
    ) {
        this.mode = mode;
        this.attributeStore = attributeStore;
        this.idValidator = idValidator;
        this.filteringPredicate = filteringPredicate;
        this.guard = guard;
        this.convertersProvider = convertersProvider;
    }

    public Context withMode(AttributeDef.Mode mode) {
        return new Context(
                mode,
                attributeStore,
                idValidator,
                filteringPredicate,
                guard,
                convertersProvider
        );
    }
}
