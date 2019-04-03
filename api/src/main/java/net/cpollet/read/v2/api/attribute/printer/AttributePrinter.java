package net.cpollet.read.v2.api.attribute.printer;

public interface AttributePrinter<T> {
    T print();

    AttributePrinter<T> name(String name);

    AttributePrinter<T> filtered(boolean filtered);

    AttributePrinter<T> deprecated(boolean deprecated);
}
