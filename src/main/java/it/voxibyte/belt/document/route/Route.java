package it.voxibyte.belt.document.route;

import it.voxibyte.belt.document.route.implementation.MultiKeyRoute;
import it.voxibyte.belt.document.route.implementation.SingleKeyRoute;
import it.voxibyte.belt.document.settings.general.GeneralSettings;

import java.util.Objects;
import java.util.regex.Pattern;


public interface Route {

    static Route from(Object... route) {

        if (Objects.requireNonNull(route, "Route array cannot be null!").length == 0)
            throw new IllegalArgumentException("Empty routes are not allowed!");

        return route.length == 1 ? new SingleKeyRoute(route[0]) : new MultiKeyRoute(route);
    }

    static Route from(Object key) {
        return new SingleKeyRoute(key);
    }

    static Route fromSingleKey(Object key) {
        return new SingleKeyRoute(key);
    }

    static Route fromString(String route) {
        return fromString(route, GeneralSettings.DEFAULT_ROUTE_SEPARATOR);
    }

    static Route fromString(String route, char separator) {
        return route.indexOf(separator) != -1 ? new MultiKeyRoute((Object[]) route.split(Pattern.quote(String.valueOf(separator)))) : new SingleKeyRoute(route);
    }

    static Route fromString(String route, RouteFactory routeFactory) {
        return route.indexOf(routeFactory.getSeparator()) != -1 ? new MultiKeyRoute((Object[]) route.split(routeFactory.getEscapedSeparator())) : new SingleKeyRoute(route);
    }

    static Route addTo(Route route, Object key) {
        return route == null ? Route.fromSingleKey(key) : route.add(key);
    }

    String join(char separator);


    int length();

    Object get(int i);

    Route add(Object key);

    Route parent();

    @Override

    boolean equals(Object o);

    @Override

    int hashCode();
}
