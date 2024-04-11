package it.voxibyte.belt.document.engine;

import it.voxibyte.belt.document.serialization.YamlSerializer;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.constructor.StandardConstructor;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.Tag;

import java.util.HashMap;
import java.util.Map;


public class ExtendedConstructor extends StandardConstructor {


    private final YamlSerializer serializer;

    private final Map<Node, Object> constructed = new HashMap<>();


    public ExtendedConstructor(LoadSettings settings, YamlSerializer serializer) {

        super(settings);

        this.serializer = serializer;

        tagConstructors.put(Tag.MAP, new ConstructMap((ConstructYamlMap) tagConstructors.get(Tag.MAP)));
    }

    @Override

    protected Object construct(Node node) {

        Object o = super.construct(node);

        constructed.put(node, o);

        return o;
    }

    @Override

    protected Object constructObjectNoCheck(Node node) {

        Object o = super.constructObjectNoCheck(node);

        constructed.put(node, o);

        return o;
    }

    public Object getConstructed(Node node) {
        return constructed.get(node);
    }


    public void clear() {
        constructed.clear();
    }


    private class ConstructMap extends ConstructYamlMap {


        private final ConstructYamlMap previous;


        private ConstructMap(ConstructYamlMap previous) {
            this.previous = previous;
        }

        @Override

        public Object construct(Node node) {

            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) previous.construct(node);

            Object deserialized = serializer.deserialize(map);


            return deserialized == null ? map : deserialized;
        }

    }
}
