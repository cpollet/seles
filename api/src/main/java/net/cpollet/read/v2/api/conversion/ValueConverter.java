package net.cpollet.read.v2.api.conversion;

public interface ValueConverter<A> {
    Object toExternalValue(A attribute, Object value) throws ConversionException;

    Object toInternalValue(A attribute, Object value) throws ConversionException;
}
