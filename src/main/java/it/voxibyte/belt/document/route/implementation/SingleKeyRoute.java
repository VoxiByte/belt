package it.voxibyte.belt.document.route.implementation;

import it.voxibyte.belt.document.route.Route;

import java.util.Objects;


public class SingleKeyRoute implements Route {


    private final Object key;


    public SingleKeyRoute(Object key) {
        this.key = Objects.requireNonNull(key, "Route cannot contain null keys!");
    }

    @Override
public
    String join(char separator) {
        return key.toString();
    }

    @Override

    public int length() {
        return 1;
    }

    @Override
public
    Object get(int i) {

        if (i != 0)
            throw new ArrayIndexOutOfBoundsException("Index " + i + " for single key route!");
        return key;
    }

    @Override
public
    Route parent() {
        throw new IllegalArgumentException("Empty routes are not allowed!");
    }

    @Override
public
    Route add(Object key) {
        return Route.from(this.key, Objects.requireNonNull(key, "Route cannot contain null keys!"));
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route that)) return false;
        if (that.length() != 1) return false;
        return Objects.equals(key, that.get(0));
    }

    @Override

    public int hashCode() {
        return Objects.hash(key);
    }

    @Override

    public String toString() {
        return "SingleKeyRoute{" +
                "key=" + key +
                '}';
    }
}
