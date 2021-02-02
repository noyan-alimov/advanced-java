package synchronization;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TestRunner {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: TestRunner [test folder] [test name]");
            return;
        }
        Instant programStart = Instant.now();

        List<Method> testMethods = new ArrayList<>();
        Class<?> testClass = getTestClass(args[0], args[1]);
        for (Method method : testClass.getDeclaredMethods()) {
            Test annotation = method.getAnnotation(Test.class);
            if (annotation != null) {
                testMethods.add(method);
            }
        }
        // You should not have to change any code above this line.

        Object passedLock = new Object();
        List<String> passed = new ArrayList<>();
        Object failedLock = new Object();
        List<String> failed = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(testMethods.size());

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (Method method : testMethods) {
            executor.execute(() -> {
                try {
                    UnitTest test = (UnitTest) testClass.getConstructor().newInstance();
                    test.beforeEachTest();
                    method.invoke(test);
                    test.afterEachTest();
                    synchronized (passedLock) {
                        passed.add(getTestName(testClass, method));
                    }
                } catch (Throwable throwable) {
                    synchronized (failedLock) {
                        failed.add(getTestName(testClass, method));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        executor.shutdown();
        latch.await();

        Duration executionTime = Duration.between(programStart, Instant.now());

        // This code should remain unchanged.
        System.out.println("Passed tests: " + passed);
        System.out.println("FAILED tests: " + failed);
        System.out.println("Test execution took " + executionTime.toSeconds() + " second(s).");
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
