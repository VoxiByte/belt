package it.voxibyte.belt.document.route;

import it.voxibyte.belt.document.settings.general.GeneralSettings;
import lombok.Getter;

import java.util.regex.Pattern;


@Getter
public class RouteFactory {
    private final char separator;
    private final String escapedSeparator;

    public RouteFactory(GeneralSettings generalSettings) {
        this.separator = generalSettings.getRouteSeparator();
        this.escapedSeparator = generalSettings.getEscapedSeparator();
    }


    public RouteFactory(char separator) {
        this.separator = separator;
        this.escapedSeparator = Pattern.quote(String.valueOf(separator));
    }


    public RouteFactory() {
        this.separator = GeneralSettings.DEFAULT_ROUTE_SEPARATOR;
        this.escapedSeparator = GeneralSettings.DEFAULT_ESCAPED_SEPARATOR;
    }

    public Route create(String route) {
        return Route.fromString(route, this);
    }


}
