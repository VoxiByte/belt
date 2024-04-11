package it.voxibyte.belt.document.utils.supplier;

import java.util.Set;


public interface SetSupplier {

    <T> Set<T> supply(int size);

}
