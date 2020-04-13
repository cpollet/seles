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
package net.cpollet.seles.impl.execution;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.api.execution.Response;
import net.cpollet.seles.impl.stages.AttributeConversionStage;
import net.cpollet.seles.impl.testsupport.VoidPrincipal;

import java.util.Collections;

public class InternalResponseHelper {
    private InternalResponseHelper() {
        // nothing
    }

    /**
     * Translates the InternalResponse back to a Request
     *
     * @param store    the store to use to map attributes
     * @param response the InternalResponse to transform
     * @return the Response generated from the InternalResponse
     */
    public static <T extends Id> Response<T> toResponse(AttributeStore store, InternalResponse<AttributeDef> response) {
        return InternalResponse.unwrap(
                new AttributeConversionStage(
                        request -> response,
                        new Context(null, store, null, null, null, null)
                )
                        .execute(
                                InternalRequest.wrap(
                                        InternalRequest.RequestType.READ,
                                        Request.read(
                                                new VoidPrincipal(),
                                                Collections.emptyList(),
                                                Collections.emptyList()
                                        )
                                )
                        )
        );
    }
}
