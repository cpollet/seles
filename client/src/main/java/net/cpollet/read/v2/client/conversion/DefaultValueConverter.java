package net.cpollet.read.v2.client.conversion;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.conversion.ConversionException;
import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;

public class DefaultValueConverter<IdType extends Id> implements ValueConverter<AttributeDef<IdType>> {
    @Override
    public Object toExternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
        if (attribute.name().equals("currency") && value.equals("currency:100000")) {
            throw new ConversionException("attribute=currency; value=currency:100000");
        }

        return String.format("externalValue(%s)", value);
    }

    @Override
    public Object toInternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
        if (attribute.name().equals("currency") && value.equals("internalCast(CHF)")) {
            throw new ConversionException("attribute=currency; value=internalCast(CHF)");
        }

        return String.format("internalValue(%s)", value);
    }
}
