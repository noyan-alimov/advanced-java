package customClassLoaders;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TestRunner {

    public static void main(String[] args) throws Exception {
        // TODO: Make sure the user has passed in two arguments: one for the test directory, and one
        //       with the name of the test class to run.

        Class<?> testClass = getTestClass(args[0], args[1]);

        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        // TODO: Rewrite this for-loop. Instead of using the TESTS list (which you should have deleted),
        //       locate the test using the command-line arguments and a custom ClassLoader. Then call
        //       Class.forName(className, true, loader) using the custom ClassLoader for the third
        //       parameter.
        //
        //       The code to record passed/failed tests should pretty much stay the same.
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.getAnnotation(Test.class) != null) {
                try {
                    UnitTest test = (UnitTest) testClass.getConstructor().newInstance();
                    test.beforeEachTest();
                    method.invoke(test);
                    test.afterEachTest();
                    passed.add(getTestName(testClass, method));
                } catch (Throwable throwable) {
                    failed.add(getTestName(testClass, method));
                }
            }
        }

        System.out.println("Passed tests: " + passed);
        System.out.println("FAILED tests: " + failed);
    }

    private static Class<?> getTestClass(String testFolder, String className) throws Exception {
        URL testDir = Path.of(testFolder).toUri().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{testDir});
        Class<?> klass = Class.forName(className, true, loader);
        if (!UnitTest.class.isAssignableFrom(klass)) {
            throw new IllegalArgumentException("Class " + klass.toString() + " must implement UnitTest");
        }
        return klass;
    }

    private static String getTestName(Class<?> klass, Method method) {
        return klass.getName() + "#" + method.getName();
    }
}
