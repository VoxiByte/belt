package it.voxibyte.belt.document.updater.operators;

import it.voxibyte.belt.document.YamlDocument;
import it.voxibyte.belt.document.block.Block;
import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.block.implementation.TerminatedBlock;
import it.voxibyte.belt.document.engine.ExtendedConstructor;
import it.voxibyte.belt.document.engine.ExtendedRepresenter;
import it.voxibyte.belt.document.route.Route;
import it.voxibyte.belt.document.settings.general.GeneralSettings;
import it.voxibyte.belt.document.settings.updater.MergeRule;
import it.voxibyte.belt.document.settings.updater.UpdaterSettings;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.representer.BaseRepresenter;

import java.util.*;
import java.util.function.Supplier;


public class Merger {


    private static final Merger INSTANCE = new Merger();


    public static void merge(Section document, Section defaults, UpdaterSettings settings) {
        INSTANCE.iterate(document, defaults, settings);
    }


    private void iterate(Section document, Section defaults, UpdaterSettings settings) {

        Set<Object> documentKeys = new HashSet<>(document.getStoredValue().keySet());

        boolean sort = settings.getOptionSorting() == UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS;
        Map<Object, Block<?>> sorted = sort ? document.getRoot().getGeneralSettings().getDefaultMap() : null;


        for (Map.Entry<Object, Block<?>> entry : defaults.getStoredValue().entrySet()) {

            Object key = entry.getKey();
            Route route = Route.from(key);

            documentKeys.remove(key);

            Block<?> documentBlock = document.getOptionalBlock(route).orElse(null), defaultBlock = entry.getValue();

            if (documentBlock != null) {

                if (documentBlock.isIgnored()) {

                    documentBlock.setIgnored(false);

                    if (documentBlock instanceof Section)
                        resetIgnored((Section) documentBlock);


                    if (sort)
                        sorted.put(key, documentBlock);
                    continue;
                }


                boolean isDocumentBlockSection = documentBlock instanceof Section, isDefaultBlockSection = defaultBlock instanceof Section;

                if (isDefaultBlockSection && isDocumentBlockSection) {

                    iterate((Section) documentBlock, (Section) defaultBlock, settings);


                    if (sort)
                        sorted.put(key, documentBlock);
                    continue;
                }


                if (sort)
                    sorted.put(key, getPreservedValue(settings.getMergeRules(), documentBlock, () -> cloneBlock(defaultBlock, document), isDocumentBlockSection, isDefaultBlockSection));
                else
                    document.set(route, getPreservedValue(settings.getMergeRules(), documentBlock, () -> cloneBlock(defaultBlock, document), isDocumentBlockSection, isDefaultBlockSection));
                continue;
            }


            if (sort)
                sorted.put(key, cloneBlock(defaultBlock, document));
            else
                document.set(route, cloneBlock(defaultBlock, document));
        }


        if (settings.isKeepAll()) {

            if (sort) {

                documentKeys.forEach(key -> sorted.put(key, document.getStoredValue().get(key)));

                document.repopulate(sorted);
            }
            return;
        }


        for (Object key : documentKeys) {

            Route route = Route.fromSingleKey(key);

            Block<?> block = document.getOptionalBlock(route).orElse(null);

            if (block != null && block.isIgnored()) {

                block.setIgnored(false);

                if (block instanceof Section)
                    resetIgnored((Section) block);


                if (sort)
                    sorted.put(key, block);
                continue;
            }


            if (!sort)
                document.remove(route);
        }


        if (sort)
            document.repopulate(sorted);
    }


    private void resetIgnored(Section section) {

        section.getStoredValue().values().forEach(block -> {

            block.setIgnored(false);

            if (block instanceof Section)
                resetIgnored((Section) block);
        });
    }

    private Block<?> cloneBlock(Block<?> block, Section newParent) {
        return block instanceof Section ? cloneSection((Section) block, newParent) : cloneTerminated((TerminatedBlock) block, newParent);
    }

    private Section cloneSection(Section section, Section newParent) {

        if (section.getRoute() == null)
            throw new IllegalArgumentException("Cannot clone the root!");

        YamlDocument root = section.getRoot();

        GeneralSettings generalSettings = root.getGeneralSettings();


        BaseRepresenter representer = new ExtendedRepresenter(generalSettings, root.getDumperSettings());

        ExtendedConstructor constructor = new ExtendedConstructor(root.getLoaderSettings().buildEngineSettings(generalSettings), generalSettings.getSerializer());

        Node represented = representer.represent(section);

        constructor.constructSingleDocument(Optional.of(represented));


        section = new Section(newParent.getRoot(), newParent, section.getRoute(), moveComments(represented), (MappingNode) represented, constructor);

        constructor.clear();

        return section;
    }

    private TerminatedBlock cloneTerminated(TerminatedBlock entry, Section newParent) {

        YamlDocument root = newParent.getRoot();

        GeneralSettings generalSettings = root.getGeneralSettings();


        BaseRepresenter representer = new ExtendedRepresenter(generalSettings, root.getDumperSettings());

        ExtendedConstructor constructor = new ExtendedConstructor(root.getLoaderSettings().buildEngineSettings(generalSettings), generalSettings.getSerializer());

        Node represented = representer.represent(entry.getStoredValue());

        constructor.constructSingleDocument(Optional.of(represented));


        entry = new TerminatedBlock(entry, constructor.getConstructed(represented));

        constructor.clear();

        return entry;
    }


    private Node moveComments(Node node) {

        ScalarNode scalarNode = new ScalarNode(Tag.STR, "", ScalarStyle.PLAIN);

        scalarNode.setBlockComments(node.getBlockComments());
        scalarNode.setInLineComments(node.getInLineComments());
        scalarNode.setEndComments(node.getEndComments());

        node.setBlockComments(Collections.emptyList());
        node.setInLineComments(null);
        node.setEndComments(null);

        return scalarNode;
    }

    private Block<?> getPreservedValue(Map<MergeRule, Boolean> rules, Block<?> documentBlock, Supplier<Block<?>> defaultBlock, boolean documentBlockIsSection, boolean defaultBlockIsSection) {
        return rules.get(MergeRule.getFor(documentBlockIsSection, defaultBlockIsSection)) ? documentBlock : defaultBlock.get();
    }


}
