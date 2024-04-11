package it.voxibyte.belt.document.block.implementation;

import it.voxibyte.belt.document.YamlDocument;
import it.voxibyte.belt.document.block.Block;
import it.voxibyte.belt.document.engine.ExtendedConstructor;
import it.voxibyte.belt.document.route.Route;
import it.voxibyte.belt.document.settings.general.GeneralSettings.KeyFormat;
import it.voxibyte.belt.document.utils.conversion.PrimitiveConversions;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;

import static it.voxibyte.belt.document.utils.conversion.ListConversions.*;
import static it.voxibyte.belt.document.utils.conversion.PrimitiveConversions.*;


@SuppressWarnings("unused UnusedReturnValue")
public class Section extends Block<Map<Object, Block<?>>> {


    @Getter
    private YamlDocument root;
    private Section defaults = null;

    @Getter
    private Section parent;

    private Object name;

    private Route route;


    public Section(YamlDocument root, Section parent, Route route, Node keyNode, MappingNode valueNode, ExtendedConstructor constructor) {

        super(keyNode, valueNode, root.getGeneralSettings().getDefaultMap());

        this.root = root;
        this.parent = parent;
        this.name = adaptKey(route.get(route.length() - 1));
        this.route = route;
        resetDefaults();

        init(root, keyNode, valueNode, constructor);
    }


    public Section(YamlDocument root, Section parent, Route route, Block<?> previous, Map<?, ?> mappings) {

        super(previous, root.getGeneralSettings().getDefaultMap());

        this.root = root;
        this.parent = parent;
        this.name = adaptKey(route.get(route.length() - 1));
        this.route = route;
        resetDefaults();

        for (Map.Entry<?, ?> entry : mappings.entrySet()) {

            Object key = adaptKey(entry.getKey()), value = entry.getValue();

            getStoredValue().put(key, value instanceof Map ? new Section(root, this, route.add(key), null, (Map<?, ?>) value) : new TerminatedBlock(null, value));
        }
    }


    protected Section(Map<Object, Block<?>> defaultMap) {

        super(defaultMap);

        this.root = null;
        this.parent = null;
        this.name = null;
        this.route = null;
        this.defaults = null;
    }


    protected void initEmpty(YamlDocument root) {

        if (!root.isRoot())
            throw new IllegalStateException("Cannot init non-root section!");

        super.init(null, null);

        this.root = root;
        resetDefaults();
    }


    protected void init(YamlDocument root, Node keyNode, MappingNode valueNode, ExtendedConstructor constructor) {

        super.init(keyNode, valueNode);

        this.root = root;
        resetDefaults();

        for (NodeTuple tuple : valueNode.getValue()) {

            Object key = adaptKey(constructor.getConstructed(tuple.getKeyNode())), value = constructor.getConstructed(tuple.getValueNode());

            getStoredValue().put(key, value instanceof Map ?
                    new Section(root, this, getSubRoute(key), tuple.getKeyNode(), (MappingNode) tuple.getValueNode(), constructor) :
                    new TerminatedBlock(tuple.getKeyNode(), tuple.getValueNode(), value));
        }
    }


    public boolean isEmpty(boolean deep) {

        if (getStoredValue().isEmpty())
            return true;

        if (!deep)
            return false;


        for (Block<?> value : getStoredValue().values()) {

            if (value instanceof TerminatedBlock || (value instanceof Section && !((Section) value).isEmpty(true)))
                return false;
        }


        return true;
    }

    @Override

    public boolean isSection() {
        return true;
    }


    public boolean isRoot() {
        return false;
    }


    @Nullable
    public Object getName() {
        return name;
    }


    @Nullable
    public String getNameAsString() {
        return name == null ? null : name.toString();
    }

    public Route getNameAsRoute() {
        return Route.from(name);
    }


    @Nullable
    public Route getRoute() {
        return route;
    }


    @Nullable
    public String getRouteAsString() {
        return route == null ? null : route.join(root.getGeneralSettings().getRouteSeparator());
    }

    public Route getSubRoute(Object key) {
        return Route.addTo(route, key);
    }


    @Nullable
    public Section getDefaults() {
        return defaults;
    }


    public boolean hasDefaults() {
        return defaults != null;
    }


    private void adapt(YamlDocument root, Section parent, Route route) {

        if (this.parent != null && this.parent != parent && this.parent.getStoredValue().get(name) == this)
            this.parent.removeInternal(this.parent, name);


        this.name = route.get(route.length() - 1);

        this.parent = parent;

        adapt(root, route);
    }


    private void adapt(YamlDocument root, Route route) {

        this.root = root;
        this.route = route;
        resetDefaults();

        for (Map.Entry<Object, Block<?>> entry : getStoredValue().entrySet())

            if (entry.getValue() instanceof Section)

                ((Section) entry.getValue()).adapt(root, route.add(entry.getKey()));
    }

    public Object adaptKey(Object key) {

        Objects.requireNonNull(key, "Sections cannot contain null keys!");
        return root.getGeneralSettings().getKeyFormat() == KeyFormat.OBJECT ? key : key.toString();
    }


    private void resetDefaults() {
        this.defaults = isRoot() ? root.getDefaults() : parent == null || parent.defaults == null ? null : parent.defaults.getSection(Route.fromSingleKey(name), null);
    }


    private boolean canUseDefaults() {
        return hasDefaults() && root.getGeneralSettings().isUseDefaults();
    }

    public Set<Route> getRoutes(boolean deep) {

        Set<Route> keys = root.getGeneralSettings().getDefaultSet();

        if (canUseDefaults())
            keys.addAll(defaults.getRoutes(deep));

        addData((route, entry) -> keys.add(route), null, deep);

        return keys;
    }

    public Set<String> getRoutesAsStrings(boolean deep) {

        Set<String> keys = root.getGeneralSettings().getDefaultSet();

        if (canUseDefaults())
            keys.addAll(defaults.getRoutesAsStrings(deep));

        addData((route, entry) -> keys.add(route), new StringBuilder(), root.getGeneralSettings().getRouteSeparator(), deep);

        return keys;
    }

    public Set<Object> getKeys() {

        Set<Object> keys = root.getGeneralSettings().getDefaultSet(getStoredValue().size());

        if (canUseDefaults())
            keys.addAll(defaults.getKeys());

        keys.addAll(getStoredValue().keySet());

        return keys;
    }

    public Map<Route, Object> getRouteMappedValues(boolean deep) {

        Map<Route, Object> values = root.getGeneralSettings().getDefaultMap();

        if (canUseDefaults())
            values.putAll(defaults.getRouteMappedValues(deep));

        addData((route, entry) -> values.put(route, entry.getValue() instanceof Section ? entry.getValue() : entry.getValue().getStoredValue()), null, deep);

        return values;
    }

    public Map<String, Object> getStringRouteMappedValues(boolean deep) {

        Map<String, Object> values = root.getGeneralSettings().getDefaultMap();

        if (canUseDefaults())
            values.putAll(defaults.getStringRouteMappedValues(deep));

        addData((route, entry) -> values.put(route, entry.getValue() instanceof Section ? entry.getValue() : entry.getValue().getStoredValue()), new StringBuilder(), root.getGeneralSettings().getRouteSeparator(), deep);

        return values;
    }

    public Map<Route, Block<?>> getRouteMappedBlocks(boolean deep) {

        Map<Route, Block<?>> blocks = root.getGeneralSettings().getDefaultMap();

        if (canUseDefaults())
            blocks.putAll(defaults.getRouteMappedBlocks(deep));

        addData((route, entry) -> blocks.put(route, entry.getValue()), null, deep);

        return blocks;
    }

    public Map<String, Block<?>> getStringRouteMappedBlocks(boolean deep) {

        Map<String, Block<?>> blocks = root.getGeneralSettings().getDefaultMap();

        if (canUseDefaults())
            blocks.putAll(defaults.getStringRouteMappedBlocks(deep));

        addData((route, entry) -> blocks.put(route, entry.getValue()), new StringBuilder(), root.getGeneralSettings().getRouteSeparator(), deep);

        return blocks;
    }


    private void addData(BiConsumer<Route, Map.Entry<?, Block<?>>> consumer, Route current, boolean deep) {

        for (Map.Entry<?, Block<?>> entry : getStoredValue().entrySet()) {

            Route entryRoute = Route.addTo(current, entry.getKey());

            consumer.accept(entryRoute, entry);

            if (deep && entry.getValue() instanceof Section)
                ((Section) entry.getValue()).addData(consumer, entryRoute, true);
        }
    }


    private void addData(BiConsumer<String, Map.Entry<?, Block<?>>> consumer, StringBuilder routeBuilder, char separator, boolean deep) {

        for (Map.Entry<?, Block<?>> entry : getStoredValue().entrySet()) {

            int length = routeBuilder.length();

            if (length > 0)
                routeBuilder.append(separator);

            consumer.accept(routeBuilder.append(entry.getKey().toString()).toString(), entry);

            if (deep && entry.getValue() instanceof Section)
                ((Section) entry.getValue()).addData(consumer, routeBuilder, separator, true);

            routeBuilder.setLength(length);
        }
    }


    public boolean contains(Route route) {
        return getBlock(route) != null;
    }


    public boolean contains(String route) {
        return getBlock(route) != null;
    }


    public Section createSection(Route route) {

        Section current = this;

        for (int i = 0; i < route.length(); i++)

            current = current.createSectionInternal(route.get(i), null);

        return current;
    }


    public Section createSection(String route) {

        int lastSeparator = 0;

        Section section = this;


        while (true) {

            int nextSeparator = route.indexOf(root.getGeneralSettings().getRouteSeparator(), lastSeparator);

            if (nextSeparator != -1)

                section = section.createSectionInternal(route.substring(lastSeparator, nextSeparator), null);
            else

                break;

            lastSeparator = nextSeparator + 1;
        }


        return section.createSectionInternal(route.substring(lastSeparator), null);
    }


    private Section createSectionInternal(Object key, Block<?> previous) {

        Object adapted = adaptKey(key);

        return getOptionalSection(Route.from(adapted)).orElseGet(() -> {

            Section section = new Section(root, Section.this, getSubRoute(adapted), previous, root.getGeneralSettings().getDefaultMap());

            getStoredValue().put(adapted, section);

            return section;
        });
    }


    public void repopulate(Map<Object, Block<?>> mappings) {
        clear();
        mappings.forEach(this::setInternal);
    }


    public void setAll(Map<Route, Object> mappings) {
        mappings.forEach(this::set);
    }


    public void set(Route route, Object value) {

        int i = -1;

        Section section = this;


        while (++i < route.length()) {

            if (i + 1 >= route.length()) {

                section.setInternal(adaptKey(route.get(i)), value);
                return;
            }


            Object key = adaptKey(route.get(i));

            Block<?> block = section.getStoredValue().getOrDefault(key, null);

            section = !(block instanceof Section) ? section.createSectionInternal(key, block) : (Section) block;
        }
    }


    public void set(String route, Object value) {

        int lastSeparator = 0;

        Section section = this;


        while (true) {

            int nextSeparator = route.indexOf(root.getGeneralSettings().getRouteSeparator(), lastSeparator);

            if (nextSeparator != -1) {

                section = section.createSection(route.substring(lastSeparator, nextSeparator));
            } else {

                section.setInternal(route.substring(lastSeparator), value);
                return;
            }

            lastSeparator = nextSeparator + 1;
        }
    }


    private void setInternal(Object key, Object value) {

        if (value instanceof Section section) {

            if (section.isRoot())
                throw new IllegalArgumentException("Cannot set root section as the value!");

            if (section.getRoot().getGeneralSettings().getKeyFormat() != getRoot().getGeneralSettings().getKeyFormat())
                throw new IllegalArgumentException("Cannot move sections between files with different key formats!");

            getStoredValue().put(key, section);


            section.adapt(root, this, getSubRoute(key));
            return;
        } else if (value instanceof TerminatedBlock) {

            getStoredValue().put(key, (TerminatedBlock) value);
            return;
        }


        if (value instanceof Map) {

            getStoredValue().put(key, new Section(root, this, getSubRoute(key), getStoredValue().getOrDefault(key, null), (Map<?, ?>) value));
            return;
        }


        Block<?> previous = getStoredValue().get(key);

        if (previous == null) {

            getStoredValue().put(key, new TerminatedBlock(null, null, value));
            return;
        }


        getStoredValue().put(key, new TerminatedBlock(previous, value));
    }


    public boolean remove(Route route) {
        return removeInternal(getParent(route).orElse(null), adaptKey(route.get(route.length() - 1)));
    }


    public boolean remove(String route) {
        return removeInternal(getParent(route).orElse(null), route.substring(route.lastIndexOf(root.getGeneralSettings().getRouteSeparator()) + 1));
    }


    private boolean removeInternal(Section parent, Object key) {

        if (parent == null)
            return false;

        return parent.getStoredValue().remove(key) != null;
    }


    public void clear() {
        getStoredValue().clear();
    }


    public Optional<Block<?>> getOptionalBlock(Route route) {
        return getBlockInternal(route, false);
    }


    private Optional<Block<?>> getDirectOptionalBlock(Object key) {
        return Optional.ofNullable(getStoredValue().get(adaptKey(key)));
    }


    public Optional<Block<?>> getOptionalBlock(String route) {
        return route.indexOf(root.getGeneralSettings().getRouteSeparator()) != -1 ? getBlockInternalString(route, false) : getDirectOptionalBlock(route);
    }


    public Block<?> getBlock(Route route) {
        return getOptionalBlock(route).orElseGet(() -> canUseDefaults() ? defaults.getBlock(route) : null);
    }


    public Block<?> getBlock(String route) {
        return getOptionalBlock(route).orElseGet(() -> canUseDefaults() ? defaults.getBlock(route) : null);
    }


    private Optional<Block<?>> getBlockInternalString(String route, boolean parent) {

        int lastSeparator = 0;

        Section section = this;


        while (true) {

            int nextSeparator = route.indexOf(root.getGeneralSettings().getRouteSeparator(), lastSeparator);

            if (nextSeparator == -1)
                break;


            Block<?> block = section.getStoredValue().getOrDefault(route.substring(lastSeparator, nextSeparator), null);

            if (!(block instanceof Section))
                return Optional.empty();

            section = (Section) block;

            lastSeparator = nextSeparator + 1;
        }


        return Optional.ofNullable(parent ? section : section.getStoredValue().get(route.substring(lastSeparator)));
    }


    private Optional<Block<?>> getBlockInternal(Route route, boolean parent) {

        int i = -1;

        Section section = this;


        while (++i < route.length() - 1) {

            Block<?> block = section.getStoredValue().getOrDefault(adaptKey(route.get(i)), null);

            if (!(block instanceof Section))
                return Optional.empty();

            section = (Section) block;
        }


        return Optional.ofNullable(parent ? section : section.getStoredValue().get(adaptKey(route.get(i))));
    }


    public Optional<Section> getParent(Route route) {
        return getBlockInternal(route, true).map(block -> block instanceof Section ? (Section) block : null);
    }


    public Optional<Section> getParent(String route) {
        return getBlockInternalString(route, true).map(block -> block instanceof Section ? (Section) block : null);
    }


    public Optional<Object> getOptional(Route route) {
        return getOptionalBlock(route).map(block -> block instanceof Section ? block : block.getStoredValue());
    }


    public Optional<Object> getOptional(String route) {
        return getOptionalBlock(route).map(block -> block instanceof Section ? block : block.getStoredValue());
    }


    public Object get(Route route) {
        return getOptional(route).orElseGet(() -> canUseDefaults() ? defaults.get(route) : root.getGeneralSettings().getDefaultObject());
    }


    public Object get(String route) {
        return getOptional(route).orElseGet(() -> canUseDefaults() ? defaults.get(route) : root.getGeneralSettings().getDefaultObject());
    }


    public Object get(Route route, Object def) {
        return getOptional(route).orElse(def);
    }


    public Object get(String route, Object def) {
        return getOptional(route).orElse(def);
    }


    @SuppressWarnings("unchecked")
    public <T> Optional<T> getAsOptional(Route route, Class<T> clazz) {
        return getOptional(route).map((object) -> clazz.isInstance(object) ? (T) object :
                PrimitiveConversions.isNumber(object.getClass()) && PrimitiveConversions.isNumber(clazz) ? (T) convertNumber(object, clazz) :
                        NON_NUMERIC_CONVERSIONS.containsKey(object.getClass()) && NON_NUMERIC_CONVERSIONS.containsKey(clazz) ? (T) object : null);
    }


    @SuppressWarnings("unchecked")
    public <T> Optional<T> getAsOptional(String route, Class<T> clazz) {
        return getOptional(route).map((object) -> clazz.isInstance(object) ? (T) object :
                PrimitiveConversions.isNumber(object.getClass()) && PrimitiveConversions.isNumber(clazz) ? (T) convertNumber(object, clazz) :
                        NON_NUMERIC_CONVERSIONS.containsKey(object.getClass()) && NON_NUMERIC_CONVERSIONS.containsKey(clazz) ? (T) object : null);
    }


    public <T> T getAs(Route route, Class<T> clazz) {
        return getAsOptional(route, clazz).orElseGet(() -> canUseDefaults() ? defaults.getAs(route, clazz) : null);
    }


    public <T> T getAs(String route, Class<T> clazz) {
        return getAsOptional(route, clazz).orElseGet(() -> canUseDefaults() ? defaults.getAs(route, clazz) : null);
    }


    public <T> T getAs(Route route, Class<T> clazz, T def) {
        return getAsOptional(route, clazz).orElse(def);
    }


    public <T> T getAs(String route, Class<T> clazz, T def) {
        return getAsOptional(route, clazz).orElse(def);
    }


    public <T> boolean is(Route route, Class<T> clazz) {
        Object o = get(route);
        return PRIMITIVES_TO_OBJECTS.containsKey(clazz) ? PRIMITIVES_TO_OBJECTS.get(clazz).isInstance(o) : clazz.isInstance(o);
    }


    public <T> boolean is(String route, Class<T> clazz) {
        Object o = get(route);
        return PRIMITIVES_TO_OBJECTS.containsKey(clazz) ? PRIMITIVES_TO_OBJECTS.get(clazz).isInstance(o) : clazz.isInstance(o);
    }


    public Optional<Section> getOptionalSection(Route route) {
        return getAsOptional(route, Section.class);
    }


    public Optional<Section> getOptionalSection(String route) {
        return getAsOptional(route, Section.class);
    }


    public Section getSection(Route route) {
        return getOptionalSection(route).orElseGet(() -> canUseDefaults() ? defaults.getSection(route) : null);
    }


    public Section getSection(String route) {
        return getOptionalSection(route).orElseGet(() -> canUseDefaults() ? defaults.getSection(route) : null);
    }


    public Section getSection(Route route, Section def) {
        return getOptionalSection(route).orElse(def);
    }


    public Section getSection(String route, Section def) {
        return getOptionalSection(route).orElse(def);
    }


    public boolean isSection(Route route) {
        return get(route) instanceof Section;
    }


    public boolean isSection(String route) {
        return get(route) instanceof Section;
    }


    public Optional<String> getOptionalString(Route route) {
        return getOptional(route).map(Object::toString);
    }


    public Optional<String> getOptionalString(String route) {
        return getOptional(route).map(Object::toString);
    }


    public String getString(Route route) {
        return getOptionalString(route).orElseGet(() -> canUseDefaults() ? defaults.getString(route) : root.getGeneralSettings().getDefaultString());
    }


    public String getString(String route) {
        return getOptionalString(route).orElseGet(() -> canUseDefaults() ? defaults.getString(route) : root.getGeneralSettings().getDefaultString());
    }


    public String getString(Route route, String def) {
        return getOptionalString(route).orElse(def);
    }


    public String getString(String route, String def) {
        return getOptionalString(route).orElse(def);
    }


    public boolean isString(Route route) {
        return get(route) instanceof String;
    }


    public boolean isString(String route) {
        return get(route) instanceof String;
    }


    public <T extends Enum<T>> Optional<T> getOptionalEnum(Route route, Class<T> clazz) {
        return getOptional(route).map(name -> toEnum(name, clazz));
    }


    public <T extends Enum<T>> Optional<T> getOptionalEnum(String route, Class<T> clazz) {
        return getOptionalString(route).map(name -> toEnum(name, clazz));
    }


    public <T extends Enum<T>> T getEnum(Route route, Class<T> clazz) {
        return getOptionalEnum(route, clazz).orElseGet(() -> canUseDefaults() ? defaults.getEnum(route, clazz) : null);
    }


    public <T extends Enum<T>> T getEnum(String route, Class<T> clazz) {
        return getOptionalEnum(route, clazz).orElseGet(() -> canUseDefaults() ? defaults.getEnum(route, clazz) : null);
    }


    public <T extends Enum<T>> T getEnum(Route route, Class<T> clazz, T def) {
        return getOptionalEnum(route, clazz).orElse(def);
    }


    public <T extends Enum<T>> T getEnum(String route, Class<T> clazz, T def) {
        return getOptionalEnum(route, clazz).orElse(def);
    }


    public <T extends Enum<T>> boolean isEnum(Route route, Class<T> clazz) {
        return toEnum(get(route), clazz) != null;
    }


    public <T extends Enum<T>> boolean isEnum(String route, Class<T> clazz) {
        return toEnum(get(route), clazz) != null;
    }


    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T toEnum(Object object, Class<T> clazz) {

        if (object == null)
            return null;

        if (clazz.isInstance(object))
            return (T) object;

        if (object instanceof Enum)
            return null;


        try {
            return Enum.valueOf(clazz, object.toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }


    public Optional<Character> getOptionalChar(Route route) {
        return getOptional(route).map(this::toChar);
    }


    public Optional<Character> getOptionalChar(String route) {
        return getOptional(route).map(this::toChar);
    }


    public Character getChar(Route route) {
        return getOptionalChar(route).orElseGet(() -> canUseDefaults() ? defaults.getChar(route) : root.getGeneralSettings().getDefaultChar());
    }


    public Character getChar(String route) {
        return getOptionalChar(route).orElseGet(() -> canUseDefaults() ? defaults.getChar(route) : root.getGeneralSettings().getDefaultChar());
    }


    public Character getChar(Route route, Character def) {
        return getOptionalChar(route).orElse(def);
    }


    public Character getChar(String route, Character def) {
        return getOptionalChar(route).orElse(def);
    }


    public boolean isChar(Route route) {
        return toChar(get(route)) != null;
    }


    public boolean isChar(String route) {
        return toChar(get(route)) != null;
    }


    private Character toChar(Object object) {
        if (object == null)
            return null;
        if (object instanceof Character)
            return (Character) object;
        if (object instanceof Integer)
            return (char) ((int) object);
        if (object instanceof String && object.toString().length() == 1)
            return object.toString().charAt(0);
        return null;
    }


    public Optional<Number> getOptionalNumber(Route route) {
        return getAsOptional(route, Number.class);
    }


    public Optional<Number> getOptionalNumber(String route) {
        return getAsOptional(route, Number.class);
    }


    public Number getNumber(Route route) {
        return getOptionalNumber(route).orElseGet(() -> canUseDefaults() ? defaults.getNumber(route) : root.getGeneralSettings().getDefaultNumber());
    }


    public Number getNumber(String route) {
        return getOptionalNumber(route).orElseGet(() -> canUseDefaults() ? defaults.getNumber(route) : root.getGeneralSettings().getDefaultNumber());
    }


    public Number getNumber(Route route, Number def) {
        return getOptionalNumber(route).orElse(def);
    }


    public Number getNumber(String route, Number def) {
        return getOptionalNumber(route).orElse(def);
    }


    public boolean isNumber(Route route) {
        return get(route) instanceof Number;
    }


    public boolean isNumber(String route) {
        return get(route) instanceof Number;
    }


    public Optional<Integer> getOptionalInt(Route route) {
        return toInt(getAs(route, Number.class));
    }


    public Optional<Integer> getOptionalInt(String route) {
        return toInt(getAs(route, Number.class));
    }


    public Integer getInt(Route route) {
        return getOptionalInt(route).orElseGet(() -> canUseDefaults() ? defaults.getInt(route) : root.getGeneralSettings().getDefaultNumber().intValue());
    }


    public Integer getInt(String route) {
        return getOptionalInt(route).orElseGet(() -> canUseDefaults() ? defaults.getInt(route) : root.getGeneralSettings().getDefaultNumber().intValue());
    }


    public Integer getInt(Route route, Integer def) {
        return getOptionalInt(route).orElse(def);
    }


    public Integer getInt(String route, Integer def) {
        return getOptionalInt(route).orElse(def);
    }


    public boolean isInt(Route route) {
        return get(route) instanceof Integer;
    }


    public boolean isInt(String route) {
        return get(route) instanceof Integer;
    }


    public Optional<BigInteger> getOptionalBigInt(Route route) {
        return toBigInt(getAs(route, Number.class));
    }


    public Optional<BigInteger> getOptionalBigInt(String route) {
        return toBigInt(getAs(route, Number.class));
    }


    public BigInteger getBigInt(Route route) {
        return getOptionalBigInt(route).orElseGet(() -> canUseDefaults() ? defaults.getBigInt(route) : BigInteger.valueOf(root.getGeneralSettings().getDefaultNumber().longValue()));
    }


    public BigInteger getBigInt(String route) {
        return getOptionalBigInt(route).orElseGet(() -> canUseDefaults() ? defaults.getBigInt(route) : BigInteger.valueOf(root.getGeneralSettings().getDefaultNumber().longValue()));
    }


    public BigInteger getBigInt(Route route, BigInteger def) {
        return getOptionalBigInt(route).orElse(def);
    }


    public BigInteger getBigInt(String route, BigInteger def) {
        return getOptionalBigInt(route).orElse(def);
    }


    public boolean isBigInt(Route route) {
        return get(route) instanceof BigInteger;
    }


    public boolean isBigInt(String route) {
        return get(route) instanceof BigInteger;
    }


    public Optional<Boolean> getOptionalBoolean(Route route) {
        return getAsOptional(route, Boolean.class);
    }


    public Optional<Boolean> getOptionalBoolean(String route) {
        return getAsOptional(route, Boolean.class);
    }


    public Boolean getBoolean(Route route) {
        return getOptionalBoolean(route).orElseGet(() -> canUseDefaults() ? defaults.getBoolean(route) : root.getGeneralSettings().getDefaultBoolean());
    }


    public Boolean getBoolean(String route) {
        return getOptionalBoolean(route).orElseGet(() -> canUseDefaults() ? defaults.getBoolean(route) : root.getGeneralSettings().getDefaultBoolean());
    }


    public Boolean getBoolean(Route route, Boolean def) {
        return getOptionalBoolean(route).orElse(def);
    }


    public Boolean getBoolean(String route, Boolean def) {
        return getOptionalBoolean(route).orElse(def);
    }


    public boolean isBoolean(Route route) {
        return get(route) instanceof Boolean;
    }


    public boolean isBoolean(String route) {
        return get(route) instanceof Boolean;
    }


    public Optional<Double> getOptionalDouble(Route route) {
        return toDouble(getAs(route, Number.class));
    }


    public Optional<Double> getOptionalDouble(String route) {
        return toDouble(getAs(route, Number.class));
    }


    public Double getDouble(Route route) {
        return getOptionalDouble(route).orElseGet(() -> canUseDefaults() ? defaults.getDouble(route) : root.getGeneralSettings().getDefaultNumber().doubleValue());
    }


    public Double getDouble(String route) {
        return getOptionalDouble(route).orElseGet(() -> canUseDefaults() ? defaults.getDouble(route) : root.getGeneralSettings().getDefaultNumber().doubleValue());
    }


    public Double getDouble(Route route, Double def) {
        return getOptionalDouble(route).orElse(def);
    }


    public Double getDouble(String route, Double def) {
        return getOptionalDouble(route).orElse(def);
    }


    public boolean isDouble(Route route) {
        return get(route) instanceof Double;
    }


    public boolean isDouble(String route) {
        return get(route) instanceof Double;
    }


    public Optional<Float> getOptionalFloat(Route route) {
        return toFloat(getAs(route, Number.class));
    }


    public Optional<Float> getOptionalFloat(String route) {
        return toFloat(getAs(route, Number.class));
    }


    public Float getFloat(Route route) {
        return getOptionalFloat(route).orElseGet(() -> canUseDefaults() ? defaults.getFloat(route) : root.getGeneralSettings().getDefaultNumber().floatValue());
    }


    public Float getFloat(String route) {
        return getOptionalFloat(route).orElseGet(() -> canUseDefaults() ? defaults.getFloat(route) : root.getGeneralSettings().getDefaultNumber().floatValue());
    }


    public Float getFloat(Route route, Float def) {
        return getOptionalFloat(route).orElse(def);
    }


    public Float getFloat(String route, Float def) {
        return getOptionalFloat(route).orElse(def);
    }


    public boolean isFloat(Route route) {
        return get(route) instanceof Float;
    }


    public boolean isFloat(String route) {
        return get(route) instanceof Float;
    }


    public Optional<Byte> getOptionalByte(Route route) {
        return toByte(getAs(route, Number.class));
    }


    public Optional<Byte> getOptionalByte(String route) {
        return toByte(getAs(route, Number.class));
    }


    public Byte getByte(Route route) {
        return getOptionalByte(route).orElseGet(() -> canUseDefaults() ? defaults.getByte(route) : root.getGeneralSettings().getDefaultNumber().byteValue());
    }


    public Byte getByte(String route) {
        return getOptionalByte(route).orElseGet(() -> canUseDefaults() ? defaults.getByte(route) : root.getGeneralSettings().getDefaultNumber().byteValue());
    }


    public Byte getByte(Route route, Byte def) {
        return getOptionalByte(route).orElse(def);
    }


    public Byte getByte(String route, Byte def) {
        return getOptionalByte(route).orElse(def);
    }


    public boolean isByte(Route route) {
        return get(route) instanceof Byte;
    }


    public boolean isByte(String route) {
        return get(route) instanceof Byte;
    }


    public Optional<Long> getOptionalLong(Route route) {
        return toLong(getAs(route, Number.class));
    }


    public Optional<Long> getOptionalLong(String route) {
        return toLong(getAs(route, Number.class));
    }


    public Long getLong(Route route) {
        return getOptionalLong(route).orElseGet(() -> canUseDefaults() ? defaults.getLong(route) : root.getGeneralSettings().getDefaultNumber().longValue());
    }


    public Long getLong(String route) {
        return getOptionalLong(route).orElseGet(() -> canUseDefaults() ? defaults.getLong(route) : root.getGeneralSettings().getDefaultNumber().longValue());
    }


    public Long getLong(Route route, Long def) {
        return getOptionalLong(route).orElse(def);
    }


    public Long getLong(String route, Long def) {
        return getOptionalLong(route).orElse(def);
    }


    public boolean isLong(Route route) {
        return get(route) instanceof Long;
    }


    public boolean isLong(String route) {
        return get(route) instanceof Long;
    }


    public Optional<Short> getOptionalShort(Route route) {
        return toShort(getAs(route, Number.class));
    }


    public Optional<Short> getOptionalShort(String route) {
        return toShort(getAs(route, Number.class));
    }


    public Short getShort(Route route) {
        return getOptionalShort(route).orElseGet(() -> canUseDefaults() ? defaults.getShort(route) : root.getGeneralSettings().getDefaultNumber().shortValue());
    }


    public Short getShort(String route) {
        return getOptionalShort(route).orElseGet(() -> canUseDefaults() ? defaults.getShort(route) : root.getGeneralSettings().getDefaultNumber().shortValue());
    }


    public Short getShort(Route route, Short def) {
        return getOptionalShort(route).orElse(def);
    }


    public Short getShort(String route, Short def) {
        return getOptionalShort(route).orElse(def);
    }


    public boolean isShort(Route route) {
        return get(route) instanceof Short;
    }


    public boolean isShort(String route) {
        return get(route) instanceof Short;
    }


    public boolean isDecimal(Route route) {
        Object o = get(route);
        return o instanceof Double || o instanceof Float;
    }


    public boolean isDecimal(String route) {
        Object o = get(route);
        return o instanceof Double || o instanceof Float;
    }


    public Optional<List<?>> getOptionalList(Route route) {
        return getAsOptional(route, List.class).map(list -> (List<?>) list);
    }


    public Optional<List<?>> getOptionalList(String route) {
        return getAsOptional(route, List.class).map(list -> (List<?>) list);
    }


    public List<?> getList(Route route) {
        return getOptionalList(route).orElseGet(() -> canUseDefaults() ? defaults.getList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<?> getList(String route) {
        return getOptionalList(route).orElseGet(() -> canUseDefaults() ? defaults.getList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<?> getList(Route route, List<?> def) {
        return getOptionalList(route).orElse(def);
    }


    public List<?> getList(String route, List<?> def) {
        return getOptionalList(route).orElse(def);
    }


    public boolean isList(Route route) {
        return get(route) instanceof List;
    }


    public boolean isList(String route) {
        return get(route) instanceof List;
    }


    public Optional<List<String>> getOptionalStringList(Route route) {
        return toStringList(getList(route, null));
    }


    public Optional<List<String>> getOptionalStringList(String route) {
        return toStringList(getList(route, null));
    }


    public List<String> getStringList(Route route, List<String> def) {
        return getOptionalStringList(route).orElse(def);
    }


    public List<String> getStringList(String route, List<String> def) {
        return getOptionalStringList(route).orElse(def);
    }


    public List<String> getStringList(Route route) {
        return getOptionalStringList(route).orElseGet(() -> canUseDefaults() ? defaults.getStringList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<String> getStringList(String route) {
        return getOptionalStringList(route).orElseGet(() -> canUseDefaults() ? defaults.getStringList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Integer>> getOptionalIntList(Route route) {
        return toIntList(getList(route, null));
    }


    public Optional<List<Integer>> getOptionalIntList(String route) {
        return toIntList(getList(route, null));
    }


    public List<Integer> getIntList(Route route, List<Integer> def) {
        return getOptionalIntList(route).orElse(def);
    }


    public List<Integer> getIntList(String route, List<Integer> def) {
        return getOptionalIntList(route).orElse(def);
    }


    public List<Integer> getIntList(Route route) {
        return getOptionalIntList(route).orElseGet(() -> canUseDefaults() ? defaults.getIntList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Integer> getIntList(String route) {
        return getOptionalIntList(route).orElseGet(() -> canUseDefaults() ? defaults.getIntList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<BigInteger>> getOptionalBigIntList(Route route) {
        return toBigIntList(getList(route, null));
    }


    public Optional<List<BigInteger>> getOptionalBigIntList(String route) {
        return toBigIntList(getList(route, null));
    }


    public List<BigInteger> getBigIntList(Route route, List<BigInteger> def) {
        return getOptionalBigIntList(route).orElse(def);
    }


    public List<BigInteger> getBigIntList(String route, List<BigInteger> def) {
        return getOptionalBigIntList(route).orElse(def);
    }


    public List<BigInteger> getBigIntList(Route route) {
        return getOptionalBigIntList(route).orElseGet(() -> canUseDefaults() ? defaults.getBigIntList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<BigInteger> getBigIntList(String route) {
        return getOptionalBigIntList(route).orElseGet(() -> canUseDefaults() ? defaults.getBigIntList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Byte>> getOptionalByteList(Route route) {
        return toByteList(getList(route, null));
    }


    public Optional<List<Byte>> getOptionalByteList(String route) {
        return toByteList(getList(route, null));
    }


    public List<Byte> getByteList(Route route, List<Byte> def) {
        return getOptionalByteList(route).orElse(def);
    }


    public List<Byte> getByteList(String route, List<Byte> def) {
        return getOptionalByteList(route).orElse(def);
    }


    public List<Byte> getByteList(Route route) {
        return getOptionalByteList(route).orElseGet(() -> canUseDefaults() ? defaults.getByteList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Byte> getByteList(String route) {
        return getOptionalByteList(route).orElseGet(() -> canUseDefaults() ? defaults.getByteList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Long>> getOptionalLongList(Route route) {
        return toLongList(getList(route, null));
    }


    public Optional<List<Long>> getOptionalLongList(String route) {
        return toLongList(getList(route, null));
    }


    public List<Long> getLongList(Route route, List<Long> def) {
        return getOptionalLongList(route).orElse(def);
    }


    public List<Long> getLongList(String route, List<Long> def) {
        return getOptionalLongList(route).orElse(def);
    }


    public List<Long> getLongList(Route route) {
        return getOptionalLongList(route).orElseGet(() -> canUseDefaults() ? defaults.getLongList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Long> getLongList(String route) {
        return getOptionalLongList(route).orElseGet(() -> canUseDefaults() ? defaults.getLongList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Double>> getOptionalDoubleList(Route route) {
        return toDoubleList(getList(route, null));
    }


    public Optional<List<Double>> getOptionalDoubleList(String route) {
        return toDoubleList(getList(route, null));
    }


    public List<Double> getDoubleList(Route route, List<Double> def) {
        return getOptionalDoubleList(route).orElse(def);
    }


    public List<Double> getDoubleList(String route, List<Double> def) {
        return getOptionalDoubleList(route).orElse(def);
    }


    public List<Double> getDoubleList(Route route) {
        return getOptionalDoubleList(route).orElseGet(() -> canUseDefaults() ? defaults.getDoubleList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Double> getDoubleList(String route) {
        return getOptionalDoubleList(route).orElseGet(() -> canUseDefaults() ? defaults.getDoubleList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Float>> getOptionalFloatList(Route route) {
        return toFloatList(getList(route, null));
    }


    public Optional<List<Float>> getOptionalFloatList(String route) {
        return toFloatList(getList(route, null));
    }


    public List<Float> getFloatList(Route route, List<Float> def) {
        return getOptionalFloatList(route).orElse(def);
    }


    public List<Float> getFloatList(String route, List<Float> def) {
        return getOptionalFloatList(route).orElse(def);
    }


    public List<Float> getFloatList(Route route) {
        return getOptionalFloatList(route).orElseGet(() -> canUseDefaults() ? defaults.getFloatList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Float> getFloatList(String route) {
        return getOptionalFloatList(route).orElseGet(() -> canUseDefaults() ? defaults.getFloatList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Short>> getOptionalShortList(Route route) {
        return toShortList(getList(route, null));
    }


    public Optional<List<Short>> getOptionalShortList(String route) {
        return toShortList(getList(route, null));
    }


    public List<Short> getShortList(Route route, List<Short> def) {
        return getOptionalShortList(route).orElse(def);
    }


    public List<Short> getShortList(String route, List<Short> def) {
        return getOptionalShortList(route).orElse(def);
    }


    public List<Short> getShortList(Route route) {
        return getOptionalShortList(route).orElseGet(() -> canUseDefaults() ? defaults.getShortList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Short> getShortList(String route) {
        return getOptionalShortList(route).orElseGet(() -> canUseDefaults() ? defaults.getShortList(route) : root.getGeneralSettings().getDefaultList());
    }


    public Optional<List<Map<?, ?>>> getOptionalMapList(Route route) {
        return toMapList(getList(route, null));
    }


    public Optional<List<Map<?, ?>>> getOptionalMapList(String route) {
        return toMapList(getList(route, null));
    }


    public List<Map<?, ?>> getMapList(Route route, List<Map<?, ?>> def) {
        return getOptionalMapList(route).orElse(def);
    }


    public List<Map<?, ?>> getMapList(String route, List<Map<?, ?>> def) {
        return getOptionalMapList(route).orElse(def);
    }


    public List<Map<?, ?>> getMapList(Route route) {
        return getOptionalMapList(route).orElseGet(() -> canUseDefaults() ? defaults.getMapList(route) : root.getGeneralSettings().getDefaultList());
    }


    public List<Map<?, ?>> getMapList(String route) {
        return getOptionalMapList(route).orElseGet(() -> canUseDefaults() ? defaults.getMapList(route) : root.getGeneralSettings().getDefaultList());
    }

}
