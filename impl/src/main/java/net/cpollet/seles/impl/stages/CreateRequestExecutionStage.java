/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.methods.CreateResult;
import net.cpollet.seles.api.methods.Method;
import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.attribute.AttributesGrouper;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Executes a CREATE {@link InternalRequest} and returns the created entity's ID.
 */
public final class CreateRequestExecutionStage implements Stage<AttributeDef> {
    private final Stage<AttributeDef> next;
    private final Stage<AttributeDef> update;
    private final Method<Id> idAttributeMethod;
    private final Set<AttributeDef> requiredAttributes;

    public CreateRequestExecutionStage(AttributeStore store, Stage<AttributeDef> update, Stage<AttributeDef> next) {
        this.next = next;
        this.update = update;
        this.idAttributeMethod = store.idAttribute()
                .map(AttributeDef::method)
                .orElse(null);
        this.requiredAttributes = Collections.unmodifiableSet(
                store.attributes().stream()
                        .filter(a -> a.modes().contains(AttributeDef.Mode.CREATE))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public InternalResponse<AttributeDef> execute(InternalRequest<AttributeDef> request) {
        if (idAttributeMethod == null) {
            throw new IllegalStateException("creation not supported, no id-attribute method provided");
        }

        Optional<InternalResponse<AttributeDef>> errorResponse = validateRequiredAttributes(request.attributes());
        if (errorResponse.isPresent()) {
            return errorResponse.get();
        }

        List<AttributeDef> mainAttributes = mainAttributes(request);

        Result idOrError = createMainEntity(request, mainAttributes);
        if (!idOrError.isSuccessful()) {
            return idOrError.response();
        }

        InternalResponse<AttributeDef> updateResponse = updateSecondaryEntities(
                idOrError.id(),
                request,
                mainAttributes
        );

        return next
                .execute(
                        request
                                .withIds(Collections.singletonList(idOrError.id()))
                                .addGuardedFlagIf(updateResponse.hasErrors(), Guarded.Flag.UPDATE_ERROR)
                )
                .mergeErrors(updateResponse)
                .mergeMessages(updateResponse);
    }

    private Optional<InternalResponse<AttributeDef>> validateRequiredAttributes(Collection<AttributeDef> attributes) {
        Set<AttributeDef> missingAttributes = requiredAttributes.stream()
                .filter(a -> !attributes.contains(a))
                .collect(Collectors.toSet());

        if (!missingAttributes.isEmpty()) {
            return Optional.of(
                    new InternalResponse<AttributeDef>()
                            .withErrors(
                                    missingAttributes.stream()
                                            .map(a -> String.format("[%s] is required for creation", a))
                                            .collect(Collectors.toList())
                            )
            );
        }

        return Optional.empty();
    }

    /**
     * Returns the list of all attributes that are handled by the same method as the id-attribute, i.e. the ones
     * for which we don't want to execute an update afterwards.
     */
    private List<AttributeDef> mainAttributes(InternalRequest<AttributeDef> request) {
        return request
                .attributes(new AttributesGrouper())
                .get(idAttributeMethod);
    }

    /**
     * Create the "main" entity instance with its attributes (i.e. the ones that are using the same method as the id
     * attribute)
     */
    private Result createMainEntity(InternalRequest<AttributeDef> request, List<AttributeDef> attributes) {
        CreateResult createResult = idAttributeMethod.create(
                request.principal(),
                request.values(attributes)
        );

        return createResult.id()
                .map(Result::success)
                .orElseGet(() -> Result.error(
                        new InternalResponse<AttributeDef>()
                                .withErrors(createResult.errors())
                ));
    }

    /**
     * Inserts the remaining attributes (i.e. the ones that are not using the same method as the id-attribute). This
     * action actually becomes an update, as the main entity already exists.
     */
    private InternalResponse<AttributeDef> updateSecondaryEntities(Id id, InternalRequest<AttributeDef> request, List<AttributeDef> alreadyInsertedAttributes) {
        return update.execute(
                request
                        .withIds(Collections.singletonList(id))
                        .withoutAttributes(alreadyInsertedAttributes)
        );
    }

    private static final class Result {
        private final Id id;
        private final InternalResponse<AttributeDef> response;

        Result(Id id, InternalResponse<AttributeDef> response) {
            this.id = id;
            this.response = response;
        }

        static Result success(Id id) {
            if (id == null) {
                throw new NullPointerException("id cannot be null");
            }
            return new Result(id, null);
        }

        static Result error(InternalResponse<AttributeDef> response) {
            if (response == null) {
                throw new NullPointerException("error cannot be null");
            }
            return new Result(null, response);
        }

        boolean isSuccessful() {
            return id != null;
        }

        InternalResponse<AttributeDef> response() {
            return response;
        }

        public Id id() {
            return id;
        }
    }
}
