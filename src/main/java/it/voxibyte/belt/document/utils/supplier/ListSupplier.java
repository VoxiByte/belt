package it.voxibyte.belt.document.utils.supplier;

import java.util.List;


public interface ListSupplier {

    <T> List<T> supply(int size);

}
