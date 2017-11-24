package pt.fabm;

public class MockSqlException extends RuntimeException {
    public MockSqlException(String reason) {
        super(reason);
    }
}
