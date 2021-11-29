package gov.mt.wris.util;

import java.lang.FunctionalInterface;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<T> extends Consumer<T> {
    @Override
    default void accept(T t) {
        try {
            acceptOrThrow(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void acceptOrThrow(T t) throws Exception;
}
