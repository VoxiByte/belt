package it.voxibyte.belt.document.serialization.standard;

import java.util.HashMap;
import java.util.Map;


public interface TypeAdapter<T> {

    Map<Object, Object> serialize(T object);

    T deserialize(Map<Object, Object> map);

    default Map<String, Object> toStringKeyedMap(Map<?, ?> map) {

        Map<String, Object> newMap = new HashMap<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {

            if (entry.getValue() instanceof Map)
                newMap.put(entry.getKey().toString(), toStringKeyedMap((Map<?, ?>) entry.getValue()));
            else
                newMap.put(entry.getKey().toString(), entry.getValue());
        }
        return newMap;
    }
}
