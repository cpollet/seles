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

@SuppressWarnings("squid:S00100")
class LogDeprecatedStageTest {
    @Test
    void insertsWarnMessage_whenAnAttributeIsDeprecated() {
        // GIVEN
        LogDeprecatedStage<StringId> stage = new LogDeprecatedStage<>(new NoopStage());
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("", Arrays.asList(
                new AttributeDef<>(
                        "non-deprecated", VoidAccessLevel.INSTANCE_1, false, null, Collections.emptySet(), null, null
                ),
                new AttributeDef<>(
                        "deprecated", VoidAccessLevel.INSTANCE_1, true, null, Collections.emptySet(), null, null
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
