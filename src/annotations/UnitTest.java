package annotations;

public interface UnitTest {
    default void beforeEachTest() {}
    default void afterEachTest() {}
}
