package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.api.execution.Response;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.testsupport.StringId;

import java.util.Collections;

public class InternalResponseHelper {
    /**
     * Translates the InternalResponse back to a Request
     * @param store the store to use to map attributes
     * @param response the InternalResponse to transform
     * @return the Response generated from the InternalResponse
     */
    public static Response<StringId> toResponse(AttributeStore<StringId> store, InternalResponse<StringId, AttributeDef<StringId>> response) {
        return InternalResponse.unwrap(
                new AttributeConversionStage<>(store, request -> response)
                        .execute(
                                InternalRequest.wrap(
                                        Request.read(Collections.emptyList(), Collections.emptyList())
                                )
                        )
        );
    }
}
