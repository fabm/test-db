package pt.fabm.mockito;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.function.Supplier;

@FunctionalInterface
public interface PredicateMethod {
    boolean when(int row, int line, Method methodCandidate, Class<?> candidateClass);
}
