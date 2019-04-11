package net.cpollet.read.v2.client.conversion;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.conversion.ConversionException;
import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;

public class DefaultValueCaster<T extends Id> implements ValueConverter<AttributeDef<T>> {
    @Override
    public Object toExternalValue(AttributeDef<T> attribute, Object value) throws ConversionException {
        return String.format("externalCast(%s)", value);
    }

    @Override
    public Object toInternalValue(AttributeDef<T> attribute, Object value) throws ConversionException {
        return String.format("internalCast(%s)", value);
    }
}
