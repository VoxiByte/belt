package it.voxibyte.belt.document.updater.operators;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.route.Route;
import it.voxibyte.belt.document.settings.updater.ValueMapper;

import java.util.Map;


public class Mapper {


    public static void apply(Section section, Map<Route, ValueMapper> mappers) {
        mappers.forEach(((route, mapper) -> section.getParent(route).ifPresent(parent -> {

            Route key = Route.fromSingleKey(route.get(route.length() - 1));

            if (!parent.getStoredValue().containsKey(key.get(0)))
                return;

            parent.set(key, mapper.map(parent, key));
        })));
    }

}
