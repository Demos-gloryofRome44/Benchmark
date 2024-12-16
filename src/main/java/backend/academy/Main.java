package backend.academy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Log4j2
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 20, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 20, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Main {

    private static final String NAME_METHOD = "name";

    private Object student;
    private Method method;
    private MethodHandle methodHandle;
    private java.util.function.Supplier<String> lambdaGetter;


    @Setup
    public void setup() throws Throwable {
        student = new Student("Egor", "Dimitriev");
        method = Student.class.getMethod(NAME_METHOD);
        methodHandle = MethodHandleCache.getCachedMethodHandle(Student.class, NAME_METHOD);
        lambdaGetter = () -> {
            try {
                return (String) methodHandle.invoke(student);
            } catch (Throwable throwable) {
                log.error("Exception in lambdaMetafactory benchmark: {}", throwable.getMessage(), throwable);
                return "Error";
            }
        };
    }

    @Benchmark
    public void directAccess(Blackhole bh) {
        bh.consume(((Student) student).name());
    }

    @Benchmark
    public void reflection(Blackhole bh) {
        try {
            bh.consume((String) method.invoke(student));
        } catch (Exception e) {
            log.error("Exception in reflection benchmark: {}", e.getMessage(), e);
        }
    }

    @Benchmark
    public void methodHandles(Blackhole bh) {
        try {
            bh.consume((String) methodHandle.invoke(student));
        } catch (Throwable throwable) {
            log.error("Exception in methodHandles benchmark: {}", throwable.getMessage(), throwable);
        }
    }

    @Benchmark
    public void lambdaMetafactory(Blackhole bh) {
        bh.consume(lambdaGetter.get());
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
