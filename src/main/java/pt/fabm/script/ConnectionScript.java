package pt.fabm.script;

import groovy.lang.Closure;

public interface ConnectionScript {
    void prepareCall(String sql, Closure closure);
}
