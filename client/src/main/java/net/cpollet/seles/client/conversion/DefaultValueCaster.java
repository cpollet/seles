package net.cpollet.seles.client.conversion;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.conversion.ConversionException;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.Id;

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
