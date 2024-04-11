package it.voxibyte.belt.document.utils.conversion;

import java.math.BigInteger;
import java.util.*;


public class PrimitiveConversions {


    public static final Map<Class<?>, Class<?>> NUMERIC_PRIMITIVES = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {{
        put(int.class, Integer.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
    }});


    public static final Map<Class<?>, Class<?>> PRIMITIVES_TO_OBJECTS = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {{
        putAll(NUMERIC_PRIMITIVES);
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
    }});


    public static final Map<Class<?>, Class<?>> NON_NUMERIC_CONVERSIONS = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
        put(Boolean.class, boolean.class);
        put(Character.class, char.class);
    }});


    public static final Set<Class<?>> NUMERIC_CLASSES = Collections.unmodifiableSet(new HashSet<Class<?>>() {{
        add(int.class);
        add(byte.class);
        add(short.class);
        add(long.class);
        add(float.class);
        add(double.class);
        add(Integer.class);
        add(Byte.class);
        add(Short.class);
        add(Long.class);
        add(Float.class);
        add(Double.class);
    }});


    public static boolean isNumber(Class<?> clazz) {
        return NUMERIC_CLASSES.contains(clazz);
    }


    public static Object convertNumber(Object value, Class<?> target) {

        Number number = (Number) value;

        boolean primitive = target.isPrimitive();

        if (primitive)
            target = NUMERIC_PRIMITIVES.get(target);


        if (target == Integer.class)
            return number.intValue();
        else if (target == Byte.class)
            return number.byteValue();
        else if (target == Short.class)
            return number.shortValue();
        else if (target == Long.class)
            return number.longValue();
        else if (target == Float.class)
            return number.floatValue();
        else
            return number.doubleValue();
    }


    public static Optional<Integer> toInt(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof Number)
            return Optional.of(((Number) value).intValue());


        try {
            return Optional.of(Integer.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }


    public static Optional<Byte> toByte(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof Number)
            return Optional.of(((Number) value).byteValue());


        try {
            return Optional.of(Byte.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }


    public static Optional<Long> toLong(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof Number)
            return Optional.of(((Number) value).longValue());


        try {
            return Optional.of(Long.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }


    public static Optional<Double> toDouble(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof Number)
            return Optional.of(((Number) value).doubleValue());


        try {
            return Optional.of(Double.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }


    public static Optional<Float> toFloat(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof Number)
            return Optional.of(((Number) value).floatValue());


        try {
            return Optional.of(Float.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }


    public static Optional<Short> toShort(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof Number)
            return Optional.of(((Number) value).shortValue());


        try {
            return Optional.of(Short.valueOf(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }


    public static Optional<BigInteger> toBigInt(Object value) {

        if (value == null)
            return Optional.empty();

        if (value instanceof BigInteger)
            return Optional.of((BigInteger) value);

        if (value instanceof Number)
            return Optional.of(BigInteger.valueOf(((Number) value).longValue()));


        try {
            return Optional.of(new BigInteger(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

}
