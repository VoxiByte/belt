package it.voxibyte.belt.document.serialization.standard;

import it.voxibyte.belt.document.serialization.YamlSerializer;
import it.voxibyte.belt.document.utils.supplier.MapSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class StandardSerializer implements YamlSerializer {


    public static final String DEFAULT_SERIALIZED_TYPE_KEY = "==";


    private static final StandardSerializer defaultSerializer = new StandardSerializer(DEFAULT_SERIALIZED_TYPE_KEY);


    private final Map<Class<?>, TypeAdapter<?>> adapters = new HashMap<>();
    private final Map<String, Class<?>> aliases = new HashMap<>();

    private final Object serializedTypeKey;


    public StandardSerializer(Object serializedTypeKey) {
        this.serializedTypeKey = serializedTypeKey;
    }

    public static StandardSerializer getDefault() {
        return defaultSerializer;
    }

    public <T> void register(Class<T> clazz, TypeAdapter<T> adapter) {
        adapters.put(clazz, adapter);
        aliases.put(clazz.getCanonicalName(), clazz);
    }

    public <T> void register(String alias, Class<T> clazz) {

        if (!adapters.containsKey(clazz))
            throw new IllegalStateException("Cannot register an alias for yet unregistered type!");

        aliases.put(alias, clazz);
    }

    @Nullable
    @Override

    public Object deserialize(Map<Object, Object> map) {
        if (!map.containsKey(serializedTypeKey))
            return null;

        Class<?> type = aliases.get(map.get(serializedTypeKey).toString());

        if (type == null)
            return null;

        return adapters.get(type).deserialize(map);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override

    public <T> Map<Object, Object> serialize(T object, MapSupplier supplier) {

        if (!adapters.containsKey(object.getClass()))
            return null;

        Map<Object, Object> serialized = supplier.supply(1);

        serialized.putAll(((TypeAdapter<T>) adapters.get(object.getClass())).serialize(object));
        serialized.computeIfAbsent(serializedTypeKey, k -> object.getClass().getCanonicalName());

        return serialized;
    }

    @Override

    public Set<Class<?>> getSupportedClasses() {
        return adapters.keySet();
    }

    @Override

    public Set<Class<?>> getSupportedParentClasses() {
        return Collections.emptySet();
    }
}
