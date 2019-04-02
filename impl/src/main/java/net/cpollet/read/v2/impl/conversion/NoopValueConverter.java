package net.cpollet.read.v2.impl.conversion;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.conversion.ConversionException;
import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;

public class NoopValueConverter<IdType extends Id> implements ValueConverter<AttributeDef<IdType>> {
    private static final NoopValueConverter<Id> instance = new NoopValueConverter<>();

    @SuppressWarnings("unchecked")
    public static <IdType> ValueConverter<IdType> instance() {
        return (ValueConverter<IdType>) instance;
    }

    private NoopValueConverter() {
        // nothing
    }

    @Override
    public Object toExternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
        return value;
    }

    @Override
    public Object toInternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
        return value;
    }
}
