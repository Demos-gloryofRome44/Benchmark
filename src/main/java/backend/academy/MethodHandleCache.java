package backend.academy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class MethodHandleCache {

    private static final Map<Class<?>, MethodHandle> CACHED_METHOD_HANDLE_MAP = new ConcurrentHashMap<>();

    /**
     * Возвращает кэшированный {@link MethodHandle} для указанного метода класса.
     * Если {@link MethodHandle} ещё не кэширован, он создаётся, кэшируется и возвращается.
     *
     * @param clas      Класс, содержащий метод.
     * @param methodName Имя метода.
     * @return Кэшированный {@link MethodHandle}.
     * @throws Throwable Если возникает ошибка при получении или кэшировании {@link MethodHandle}.
     */
    static MethodHandle getCachedMethodHandle(Class<?> clas, String methodName) throws Throwable {
        return CACHED_METHOD_HANDLE_MAP.computeIfAbsent(clas, c -> {
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
