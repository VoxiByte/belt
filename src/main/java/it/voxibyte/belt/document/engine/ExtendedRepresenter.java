package it.voxibyte.belt.document.engine;

import it.voxibyte.belt.document.YamlDocument;
import it.voxibyte.belt.document.block.Block;
import it.voxibyte.belt.document.block.Comments;
import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.settings.dumper.DumperSettings;
import it.voxibyte.belt.document.settings.general.GeneralSettings;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.representer.StandardRepresenter;

import java.util.Map;


public class ExtendedRepresenter extends StandardRepresenter {


    private final GeneralSettings generalSettings;

    private final DumperSettings dumperSettings;


    public ExtendedRepresenter(GeneralSettings generalSettings, DumperSettings dumperSettings, DumpSettings engineSettings) {

        super(engineSettings);

        this.generalSettings = generalSettings;
        this.dumperSettings = dumperSettings;


        RepresentToNode representSection = new RepresentSection(), representSerializable = new RepresentSerializable();

        super.representers.put(Section.class, representSection);
        super.representers.put(YamlDocument.class, representSection);
        super.representers.put(Enum.class, new RepresentEnum());
        super.representers.put(String.class, new RepresentString(super.representers.get(String.class)));

        for (Class<?> clazz : generalSettings.getSerializer().getSupportedClasses())
            super.representers.put(clazz, representSerializable);
        for (Class<?> clazz : generalSettings.getSerializer().getSupportedParentClasses())
            super.parentClassRepresenters.put(clazz, representSerializable);
    }


    public ExtendedRepresenter(GeneralSettings generalSettings, DumperSettings dumperSettings) {
        this(generalSettings, dumperSettings, dumperSettings.buildEngineSettings());
    }

    public Node applyKeyComments(Block<?> block, Node node) {

        if (block != null) {

            node.setBlockComments(Comments.get(block, Comments.NodeType.KEY, Comments.Position.BEFORE));
            node.setInLineComments(Comments.get(block, Comments.NodeType.KEY, Comments.Position.INLINE));
            node.setEndComments(Comments.get(block, Comments.NodeType.KEY, Comments.Position.AFTER));
        }

        return node;
    }

    public Node applyValueComments(Block<?> block, Node node) {

        if (block != null) {

            node.setBlockComments(Comments.get(block, Comments.NodeType.VALUE, Comments.Position.BEFORE));
            node.setInLineComments(Comments.get(block, Comments.NodeType.VALUE, Comments.Position.INLINE));
            node.setEndComments(Comments.get(block, Comments.NodeType.VALUE, Comments.Position.AFTER));
        }

        return node;
    }

    @Override

    protected NodeTuple representMappingEntry(Map.Entry<?, ?> entry) {

        Block<?> block = entry.getValue() instanceof Block ? (Block<?>) entry.getValue() : null;

        Node key = applyKeyComments(block, representData(entry.getKey()));
        Node value = applyValueComments(block, representData(block == null ? entry.getValue() : block.getStoredValue()));

        return new NodeTuple(key, value);
    }

    private class RepresentSerializable implements RepresentToNode {

        @Override

        public Node representData(Object data) {

            Object serialized = generalSettings.getSerializer().serialize(data, generalSettings.getDefaultMapSupplier());

            return ExtendedRepresenter.this.representData(serialized == null ? data : serialized);
        }

    }

    private class RepresentSection implements RepresentToNode {

        @Override

        public Node representData(Object data) {

            Section section = (Section) data;

            return applyKeyComments(section, ExtendedRepresenter.this.representData(section.getStoredValue()));
        }

    }

    private class RepresentEnum implements RepresentToNode {

        @Override

        public Node representData(Object data) {
            return ExtendedRepresenter.this.representData(((Enum<?>) data).name());
        }

    }

    private class RepresentString implements RepresentToNode {


        private final RepresentToNode previous;


        private RepresentString(RepresentToNode previous) {
            this.previous = previous;
        }

        @Override

        public Node representData(Object data) {

            ScalarStyle previousStyle = defaultScalarStyle;
            defaultScalarStyle = dumperSettings.getStringStyle();

            Node node = previous.representData(data);

            defaultScalarStyle = previousStyle;
            return node;
        }

    }

}
