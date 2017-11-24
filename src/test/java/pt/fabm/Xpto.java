package pt.fabm;

import groovy.lang.Closure;
import groovy.lang.Script;
import pt.fabm.closures.ResultSetClosure;

public abstract class Xpto extends Script {

    void helloWorld(Closure test){
        test.setDelegate(new ResultSetClosure());
        test.call();
    }
}
