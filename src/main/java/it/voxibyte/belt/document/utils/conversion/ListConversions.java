package it.voxibyte.belt.document.utils.conversion;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public class ListConversions {

    public static Optional<List<String>> toStringList(List<?> value) {
        return construct(value, o -> Optional.ofNullable(o.toString()));
    }

    public static Optional<List<Integer>> toIntList(List<?> value) {
        return construct(value, PrimitiveConversions::toInt);
    }

    public static Optional<List<BigInteger>> toBigIntList(List<?> value) {
        return construct(value, PrimitiveConversions::toBigInt);
    }

    public static Optional<List<Byte>> toByteList(List<?> value) {
        return construct(value, PrimitiveConversions::toByte);
    }

    public static Optional<List<Long>> toLongList(List<?> value) {
        return construct(value, PrimitiveConversions::toLong);
    }

    public static Optional<List<Double>> toDoubleList(List<?> value) {
        return construct(value, PrimitiveConversions::toDouble);
    }

    public static Optional<List<Float>> toFloatList(List<?> value) {
        return construct(value, PrimitiveConversions::toFloat);
    }

    public static Optional<List<Short>> toShortList(List<?> value) {
        return construct(value, PrimitiveConversions::toShort);
    }

    public static Optional<List<Map<?, ?>>> toMapList(List<?> value) {
        return construct(value, o -> o instanceof Map ? Optional.of((Map<?, ?>) o) : Optional.empty());
    }

    private static <T> Optional<List<T>> construct(List<?> value, Function<Object, Optional<T>> mapper) {

        if (value == null)
            return Optional.empty();


        List<T> list = new ArrayList<>();

        for (Object element : value) {

            if (element == null)
                continue;


            mapper.apply(element).ifPresent(list::add);
        }


        return Optional.of(list);
    }

}
