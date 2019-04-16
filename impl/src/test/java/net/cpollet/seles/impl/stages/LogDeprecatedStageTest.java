package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.execution.Response;
import net.cpollet.seles.impl.attribute.DirectAttributeStore;
import net.cpollet.seles.impl.execution.InternalRequestHelper;
import net.cpollet.seles.impl.execution.InternalResponseHelper;
import net.cpollet.seles.impl.testsupport.NoopStage;
import net.cpollet.seles.impl.testsupport.StringId;
import net.cpollet.seles.impl.testsupport.VoidAccessLevel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class LogDeprecatedStageTest {
    @Test
    void insertsWarnMessage_whenAnAttributeIsDeprecated() {
        // GIVEN
        LogDeprecatedStage<StringId> stage = new LogDeprecatedStage<>(new NoopStage());
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("", Arrays.asList(
                new AttributeDef<>(
                        "non-deprecated", VoidAccessLevel.INSTANCE, false, null, Collections.emptySet(), null, null
                ),
                new AttributeDef<>(
                        "deprecated", VoidAccessLevel.INSTANCE, true, null, Collections.emptySet(), null, null
                )
        ));

        // WHEN
        Response<StringId> res = InternalResponseHelper.toResponse(
                store,
                stage.execute(
                        InternalRequestHelper.toAttributeDefInternalRequest(store)
                )
        );

        // THEN
        Assertions.assertThat("[deprecated] is deprecated").isIn(res.messages());
        Assertions.assertThat("[non-deprecated] is deprecated").isNotIn(res.messages());
    }
}
