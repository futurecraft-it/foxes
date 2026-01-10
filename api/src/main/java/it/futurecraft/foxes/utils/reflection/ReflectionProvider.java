package it.futurecraft.foxes.utils.reflection;

import it.futurecraft.foxes.entities.Tamable;

import java.util.HashMap;
import java.util.Map;

public class ReflectionProvider {
    private final Map<Class<?>, Names> names = new HashMap<>();

    private ReflectionProvider() {}

    public <T extends Tamable> Names get(Class<T> type) {
        return names.get(type);
    }

    public <T extends Tamable> void set(Class<T> clazz, Names n) {
        names.put(clazz, n);
    }

    private static class _SingletonHelper {
        private static final ReflectionProvider INSTANCE = new ReflectionProvider();
    }

    public static ReflectionProvider instance() {
        return _SingletonHelper.INSTANCE;
    }

    public interface Names {
        String factory();
    }
}
