package it.voxibyte.belt.document.serialization;

import it.voxibyte.belt.document.utils.supplier.MapSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;


public interface YamlSerializer {


    @Nullable
    Object deserialize(Map<Object, Object> map);


    @Nullable
    <T> Map<Object, Object> serialize(T object, MapSupplier supplier);

    Set<Class<?>> getSupportedClasses();

    Set<Class<?>> getSupportedParentClasses();

}
