package net.cpollet.seles.impl.execution;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.api.execution.Response;
import net.cpollet.seles.impl.stages.AttributeConversionStage;
import net.cpollet.seles.impl.testsupport.StringId;
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
    public static Response<StringId> toResponse(AttributeStore<StringId> store, InternalResponse<StringId, AttributeDef<StringId>> response) {
        return InternalResponse.unwrap(
                new AttributeConversionStage<>(store, request -> response)
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
