package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.impl.attribute.DirectAttributeStore;
import net.cpollet.seles.impl.execution.InternalRequestHelper;
import net.cpollet.seles.impl.testsupport.StringId;
import net.cpollet.seles.impl.testsupport.VoidAccessLevel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class ExpandStarStageTest {
    @Test
    void starIsExpanded_whenPresent() {
        // GIVEN
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>(Arrays.asList(
                new AttributeDef<>(
                        "attribute_1", VoidAccessLevel.INSTANCE, false, null, Collections.emptySet(), null, null
                ),
                new AttributeDef<>(
                        "attribute_2", VoidAccessLevel.INSTANCE, true, null, Collections.emptySet(), null, null
                )
        ));
        ExpandStarStage<StringId> stage = new ExpandStarStage<>(store, request -> {
            // THEN
            Assertions.assertThat("attribute_1").isIn(request.attributes());
            Assertions.assertThat("attribute_2").isIn(request.attributes());
            Assertions.assertThat(2).isEqualTo(request.attributes().size());

            return null; // we don't care about return value
        });

        // WHEN
        stage.execute(InternalRequestHelper.toStringInternalRequest(Collections.singletonList("*")));
    }

    @Test
    void starIsExpanded_withoutDuplicate_whenPresent() {
        // GIVEN
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>(Arrays.asList(
                new AttributeDef<>(
                        "attribute_1", VoidAccessLevel.INSTANCE, false, null, Collections.emptySet(), null, null
                ),
                new AttributeDef<>(
                        "attribute_2", VoidAccessLevel.INSTANCE, true, null, Collections.emptySet(), null, null
                )
        ));
        ExpandStarStage<StringId> stage = new ExpandStarStage<>(store, request -> {
            // THEN
            Assertions.assertThat("attribute_1").isIn(request.attributes());
            Assertions.assertThat("attribute_2").isIn(request.attributes());
            Assertions.assertThat(2).isEqualTo(request.attributes().size());

            return null; // we don't care about return value
        });

        // WHEN
        stage.execute(InternalRequestHelper.toStringInternalRequest(Arrays.asList("*", "attribute_1")));
    }

    @Test
    void nothingHappens_whenStarAbsent() {
        // GIVEN
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>(Arrays.asList(
                new AttributeDef<>(
                        "attribute_1", VoidAccessLevel.INSTANCE, false, null, Collections.emptySet(), null, null
                ),
                new AttributeDef<>(
                        "attribute_2", VoidAccessLevel.INSTANCE, true, null, Collections.emptySet(), null, null
                )
        ));
        ExpandStarStage<StringId> stage = new ExpandStarStage<>(store, request -> {
            // THEN
            Assertions.assertThat("attribute_1").isIn(request.attributes());
            Assertions.assertThat(1).isEqualTo(request.attributes().size());

            return null; // we don't care about return value
        });

        // WHEN
        stage.execute(InternalRequestHelper.toStringInternalRequest(Collections.singletonList("attribute_1")));
    }
}
