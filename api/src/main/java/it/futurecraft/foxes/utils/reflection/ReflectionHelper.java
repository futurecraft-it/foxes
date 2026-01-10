package it.futurecraft.foxes.utils.reflection;

import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHelper {
    public static void set(final Field f, final Object obj, final Object val) {
        try {
            f.setAccessible(true);

            if (!Modifier.isFinal(f.getModifiers())) {
                f.set(obj, val);
            } else {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
                MethodHandle handle = lookup.unreflectSetter(f);

                handle.invoke(obj, val);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Class<?> innerClass(final Class<?> clazz, String name) {
        for (Class<?> c : clazz.getDeclaredClasses()) {
            if (c.getSimpleName().equals(name)) return c;
        }

        return null;
    }

    public static Object newInstance(final Class<?> clazz, String name, Object obj, Argument<?>... args) {
        try {
            Class<?> inner = innerClass(clazz, name);

            boolean isStatic = Modifier.isStatic(inner.getModifiers());

            int len = isStatic ? args.length : args.length + 1;
            Object[] vals = new Object[len];
            Class<?>[] types = new Class[len];

            for (int i = 0; i < args.length; i++) {
                Argument<?> arg = args[i];

                int j = isStatic ? i : i + 1;

                vals[j] = arg.val;
                types[j] = arg.type;
            }

            if (!isStatic) {
                vals[0] = obj;
                types[0] = clazz;
            }

            Constructor<?> constructor = inner.getDeclaredConstructor(types);
            constructor.setAccessible(true);

            Object instance = constructor.newInstance(vals);

            constructor.setAccessible(false);

            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    public record Argument<T> (Class<T> type, T val) {}
}
