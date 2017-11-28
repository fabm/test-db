package pt.fabm;

import com.google.inject.TypeLiteral;

public interface Type {
    static <T> TypeLiteral<T> get() {
        return new TypeLiteral<T>() {
        };
    }
}
