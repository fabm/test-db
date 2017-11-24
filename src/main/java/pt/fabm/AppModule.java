package pt.fabm;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppModule extends AbstractModule {

    private static Injector INJECTOR = Guice.createInjector(new AppModule());

    public static Injector getInjector() {
        return INJECTOR;
    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<String,SqlBehavior>>(){}).annotatedWith(Names.named("sql-map")).toInstance(new <String,SqlBehavior>ConcurrentHashMap());
    }
}
