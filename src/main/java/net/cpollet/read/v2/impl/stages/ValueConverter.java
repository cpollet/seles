package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;

public interface ValueConverter<IdType extends Id, AttributeType> {
    Object convert(AttributeType attribute, Object value) throws ConversionException;
}
