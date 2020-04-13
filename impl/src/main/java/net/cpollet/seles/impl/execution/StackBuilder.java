package net.cpollet.seles.impl.execution;

import net.cpollet.seles.impl.stages.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public class StackBuilder {
    @SuppressWarnings("unchecked")
    public static Stage<String> build(Context context, List<Class<? extends Stage>> stages) {
        List<Class<? extends Stage>> localStages = new ArrayList<>(stages);
        Collections.reverse(localStages);
        Class<? extends Stage> firstStageClass = localStages.remove(0);

        return localStages.stream().reduce(
                (Stage) instantiate(firstStageClass, context, null),
                (s, c) -> instantiate(c, context, s),
                (s1, s2) -> s2
        );
    }

    @SuppressWarnings("unchecked")
    private static Stage<Object> instantiate(Class<? extends Stage> aClass, Context context, Stage<?> stage) {
        return Optional.ofNullable(
                findConstructor(aClass, Stage.class, Context.class)
                        .map(c -> newInstance(c, stage, context))
                        .orElseGet(
                                () -> findConstructor(aClass, Stage.class)
                                        .map(c -> newInstance(c, stage))
                                        .orElseGet(
                                                () -> findConstructor(aClass)
                                                        .map(c -> newInstance(c))
                                                        .orElse(null)
                                        )
                        )
        ).orElseThrow(() -> new IllegalArgumentException("no valid ctor found for " + aClass));
    }

    private static Optional<Constructor<? extends Stage>> findConstructor(Class<? extends Stage> aClass, Class<?>... parameterTypes) {
        try {
            return Optional.of(aClass.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private static Stage newInstance(Constructor<? extends Stage> ctor, Object... args) {
        try {
            return ctor.newInstance(args);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
