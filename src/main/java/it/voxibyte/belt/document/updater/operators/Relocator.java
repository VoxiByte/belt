package it.voxibyte.belt.document.updater.operators;

import it.voxibyte.belt.document.block.Block;
import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.route.Route;

import java.util.Map;
import java.util.Optional;


public class Relocator {


    private static final Relocator INSTANCE = new Relocator();


    public static void apply(Section section, Map<Route, Route> relocations) {

        while (relocations.size() > 0)

            INSTANCE.apply(section, relocations, relocations.keySet().iterator().next());
    }


    private void apply(Section section, Map<Route, Route> relocations, Route from) {

        if (from == null || !relocations.containsKey(from))
            return;

        Optional<Section> parent = section.getParent(from);

        if (!parent.isPresent()) {
            relocations.remove(from);
            return;
        }


        Object lastKey = from.get(from.length() - 1);

        Block<?> block = parent.get().getStoredValue().get(lastKey);

        if (block == null) {
            relocations.remove(from);
            return;
        }


        Route to = relocations.get(from);


        relocations.remove(from);
        parent.get().getStoredValue().remove(lastKey);
        removeParents(parent.get());


        apply(section, relocations, to);


        section.set(to, block);
    }


    private void removeParents(Section section) {

        if (section.isEmpty(false) && !section.isRoot()) {

            section.getParent().getStoredValue().remove(section.getName());

            removeParents(section.getParent());
        }
    }

}
