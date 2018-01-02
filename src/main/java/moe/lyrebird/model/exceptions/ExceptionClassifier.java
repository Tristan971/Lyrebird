package moe.lyrebird.model.exceptions;

import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ExceptionClassifier<X extends Throwable> implements Predicate<X>, Function<X, Pane> {

    private final Map<Class<? extends Throwable>, Function<X, Pane>> managedExceptions = new HashMap<>();

    @Override
    public boolean test(final X x) {
        return managedExceptions.containsKey(x.getCause().getClass());
    }

    @Override
    public Pane apply(final X x) {
        return managedExceptions.get(x.getCause().getClass()).apply(x);
    }
    
}
