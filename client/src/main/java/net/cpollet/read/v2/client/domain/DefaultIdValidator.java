package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Collections;

/**
 * An IdValidator that validates every Id passed
 */
public class DefaultIdValidator<T extends Id> implements IdValidator<T> {
    @Override
    public Collection<T> invalidIds(Collection<T> ids) {
        return Collections.emptySet();
    }
}
