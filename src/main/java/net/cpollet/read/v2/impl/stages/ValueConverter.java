package net.cpollet.read.v2.impl.stages;

public interface ValueConverter<AttributeType> {
    Object convert(AttributeType attribute, Object value) throws ConversionException;
}
