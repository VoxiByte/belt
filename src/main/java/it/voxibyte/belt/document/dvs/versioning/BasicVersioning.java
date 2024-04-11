package it.voxibyte.belt.document.dvs.versioning;

import it.voxibyte.belt.document.dvs.Pattern;
import it.voxibyte.belt.document.dvs.segment.Segment;


public class BasicVersioning extends AutomaticVersioning {


    public static final Pattern PATTERN = new Pattern(Segment.range(1, Integer.MAX_VALUE));


    public BasicVersioning(String route) {
        super(PATTERN, route);
    }
}
