package it.voxibyte.belt.document.dvs.versioning;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.dvs.Pattern;
import it.voxibyte.belt.document.dvs.Version;
import it.voxibyte.belt.document.route.Route;
import org.jetbrains.annotations.Nullable;


public class AutomaticVersioning implements Versioning {


    private final Pattern pattern;

    private final Route route;
    private final String strRoute;


    public AutomaticVersioning(Pattern pattern, Route route) {
        this.pattern = pattern;
        this.route = route;
        this.strRoute = null;
    }


    public AutomaticVersioning(Pattern pattern, String route) {
        this.pattern = pattern;
        this.route = null;
        this.strRoute = route;
    }

    @Nullable
    @Override

    @SuppressWarnings("ConstantConditions")
    public Version getDocumentVersion(Section document, boolean defaults) {
        return (route != null ? document.getOptionalString(route) : document.getOptionalString(strRoute)).map(pattern::getVersion).orElse(null);

    }

    @Override

    public Version getFirstVersion() {
        return pattern.getFirstVersion();
    }

    @Override

    @SuppressWarnings("ConstantConditions")
    public void updateVersionID(Section updated, Section def) {

        if (route != null)
            updated.set(route, def.getString(route));
        else
            updated.set(strRoute, def.getString(strRoute));
    }

    @Override

    public String toString() {
        return "AutomaticVersioning{" +
                "pattern=" + pattern +
                ", route='" + (route == null ? strRoute : route) + '\'' +
                '}';
    }
}
