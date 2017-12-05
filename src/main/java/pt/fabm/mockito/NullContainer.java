package pt.fabm.mockito;

public class NullContainer {

    private static NullContainer nullContent(Class<?> type){
        NullContainer nullContainer = new NullContainer();
        nullContainer.type = type;
        return nullContainer;
    }
    private Class<?> type;

    public Class<?> getType() {
        return type;
    }

    private NullContainer() {

    }
}
