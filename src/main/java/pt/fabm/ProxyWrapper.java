package pt.fabm;

import java.lang.reflect.InvocationHandler;

public interface ProxyWrapper<T> extends InvocationHandler {

    void setProxy(T proxy);

    T getProxy();
}
