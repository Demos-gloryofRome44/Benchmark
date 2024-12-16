package backend.academy;

import lombok.extern.log4j.Log4j2;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class MethodHandleCache {
    private static final Map<Class<?>, MethodHandle> cachedMethodHandles = new ConcurrentHashMap<>();

    static MethodHandle getCachedMethodHandle(Class<?> clazz, String methodName) throws Throwable {
        return cachedMethodHandles.computeIfAbsent(clazz, c -> {
            try {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                Method method = c.getMethod(methodName);
                MethodType mt = MethodType.methodType(String.class);
                return lookup.findVirtual(c, methodName, mt);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                log.error("Error caching method handle: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }
}
