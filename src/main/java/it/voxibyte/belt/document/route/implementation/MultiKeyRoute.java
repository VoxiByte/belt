package it.voxibyte.belt.document.route.implementation;

import it.voxibyte.belt.document.route.Route;

import java.util.Arrays;
import java.util.Objects;


public class MultiKeyRoute implements Route {


    private final Object[] route;


    public MultiKeyRoute(Object... route) {

        if (Objects.requireNonNull(route, "Route array cannot be null!").length == 0)
            throw new IllegalArgumentException("Empty routes are not allowed!");

        for (Object key : route)
            Objects.requireNonNull(key, "Route cannot contain null keys!");


        this.route = route;
    }

    @Override
public
    String join(char separator) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length(); i++)
            builder.append(get(i)).append(i + 1 < length() ? separator : "");

        return builder.toString();
    }

    @Override

    public int length() {
        return route.length;
    }

    @Override
public
    Object get(int i) {
        return route[i];
    }

    @Override
public
    Route add(Object key) {

        Object[] route = Arrays.copyOf(this.route, this.route.length + 1);

        route[route.length - 1] = Objects.requireNonNull(key, "Route cannot contain null keys!");

        return new MultiKeyRoute(route);
    }

    @Override
public
    Route parent() {
        return route.length == 2 ? Route.from(route[0]) : Route.from(Arrays.copyOf(route, route.length - 1));
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route route1)) return false;
        if (this.length() != route1.length()) return false;
        if (this.length() == 1 && route1.length() == 1) return Objects.equals(this.get(0), route1.get(0));
        if (!(route1 instanceof MultiKeyRoute)) return false;
        return Arrays.equals(route, ((MultiKeyRoute) route1).route);
    }

    @Override

    public int hashCode() {
        return length() > 1 ? Arrays.hashCode(route) : Objects.hashCode(route[0]);
    }

    @Override

    public String toString() {
        return "MultiKeyRoute{" +
                "route=" + Arrays.toString(route) +
                '}';
    }
}
