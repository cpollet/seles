package net.cpollet.seles.impl.conversion;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.conversion.ConversionException;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.Id;

public final class NoopValueConverter<T extends Id> implements ValueConverter<AttributeDef<T>> {
    private static final NoopValueConverter<Id> instance = new NoopValueConverter<>();

    @SuppressWarnings("unchecked")
    public static <T> ValueConverter<T> instance() {
        return (ValueConverter<T>) instance;
    }

    private NoopValueConverter() {
        // nothing
    }

    @Override
    public Object toExternalValue(AttributeDef<T> attribute, Object value) throws ConversionException {
        return value;
    }

    @Override
    public Object toInternalValue(AttributeDef<T> attribute, Object value) throws ConversionException {
        return value;
    }
}
