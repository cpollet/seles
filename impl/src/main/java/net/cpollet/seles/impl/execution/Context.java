package net.cpollet.seles.impl.execution;

import net.cpollet.seles.api.attribute.AccessLevelPredicate;
import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.IdValidator;

public class Context {
    public final AttributeDef.Mode mode;
    public final AttributeStore attributeStore;
    public final IdValidator idValidator;
    public final AccessLevelPredicate filteringPredicate;
    public final DefaultExecutorGuard guard;

    public Context(AttributeDef.Mode mode, AttributeStore attributeStore, IdValidator idValidator, AccessLevelPredicate filteringPredicate, DefaultExecutorGuard guard) {
        this.mode = mode;
        this.attributeStore = attributeStore;
        this.idValidator = idValidator;
        this.filteringPredicate = filteringPredicate;
        this.guard = guard;
    }

    public ValueConverter<AttributeDef> valueConverter(AttributeDef attributeDef) {
        return attributeDef.converter();
    }
}
