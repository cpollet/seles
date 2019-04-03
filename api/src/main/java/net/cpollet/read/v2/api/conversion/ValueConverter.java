package net.cpollet.read.v2.api.conversion;

public interface ValueConverter<AttributeType> {
    Object toExternalValue(AttributeType attribute, Object value) throws ConversionException;

    Object toInternalValue(AttributeType attribute, Object value) throws ConversionException;
}