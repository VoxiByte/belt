package it.voxibyte.belt.document.utils.supplier;

import java.util.Map;


public interface MapSupplier {

    <K, V> Map<K, V> supply(int size);

}
