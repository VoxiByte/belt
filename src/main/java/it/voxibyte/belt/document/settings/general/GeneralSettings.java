package it.voxibyte.belt.document.settings.general;

import it.voxibyte.belt.document.YamlDocument;
import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.serialization.YamlSerializer;
import it.voxibyte.belt.document.serialization.standard.StandardSerializer;
import it.voxibyte.belt.document.utils.supplier.ListSupplier;
import it.voxibyte.belt.document.utils.supplier.MapSupplier;
import it.voxibyte.belt.document.utils.supplier.SetSupplier;
import lombok.Getter;

import java.util.*;
import java.util.regex.Pattern;


@SuppressWarnings("unused")
public class GeneralSettings {


    public static final char DEFAULT_ROUTE_SEPARATOR = '.';
    public static final String DEFAULT_ESCAPED_SEPARATOR = Pattern.quote(String.valueOf(DEFAULT_ROUTE_SEPARATOR));
    public static final KeyFormat DEFAULT_KEY_FORMATTING = KeyFormat.STRING;
    public static final YamlSerializer DEFAULT_SERIALIZER = StandardSerializer.getDefault();
    public static final boolean DEFAULT_USE_DEFAULTS = true;
    public static final Object DEFAULT_OBJECT = null;
    public static final Number DEFAULT_NUMBER = 0;
    public static final String DEFAULT_STRING = null;
    public static final Character DEFAULT_CHAR = ' ';
    public static final Boolean DEFAULT_BOOLEAN = false;
    public static final ListSupplier DEFAULT_LIST = ArrayList::new;
    public static final SetSupplier DEFAULT_SET = LinkedHashSet::new;
    public static final MapSupplier DEFAULT_MAP = LinkedHashMap::new;
    public static final GeneralSettings DEFAULT = builder().build();
    @Getter
    private final KeyFormat keyFormat;
    private final char separator;
    @Getter
    private final String escapedSeparator;
    @Getter
    private final YamlSerializer serializer;
    @Getter
    private final boolean useDefaults;
    @Getter
    private final Object defaultObject;
    @Getter
    private final Number defaultNumber;
    @Getter
    private final String defaultString;
    @Getter
    private final Character defaultChar;
    @Getter
    private final Boolean defaultBoolean;
    private final ListSupplier defaultList;
    private final SetSupplier defaultSet;
    private final MapSupplier defaultMap;

    private GeneralSettings(Builder builder) {
        this.keyFormat = builder.keyFormat;
        this.separator = builder.routeSeparator;
        this.escapedSeparator = Pattern.quote(String.valueOf(separator));
        this.serializer = builder.serializer;
        this.defaultObject = builder.defaultObject;
        this.defaultNumber = builder.defaultNumber;
        this.defaultString = builder.defaultString;
        this.defaultChar = builder.defaultChar;
        this.defaultBoolean = builder.defaultBoolean;
        this.defaultList = builder.defaultList;
        this.defaultSet = builder.defaultSet;
        this.defaultMap = builder.defaultMap;
        this.useDefaults = builder.useDefaults;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GeneralSettings settings) {
        return builder()
                .setKeyFormat(settings.keyFormat)
                .setRouteSeparator(settings.separator)
                .setSerializer(settings.serializer)
                .setUseDefaults(settings.useDefaults)
                .setDefaultObject(settings.defaultObject)
                .setDefaultNumber(settings.defaultNumber)
                .setDefaultString(settings.defaultString)
                .setDefaultChar(settings.defaultChar)
                .setDefaultBoolean(settings.defaultBoolean)
                .setDefaultList(settings.defaultList)
                .setDefaultSet(settings.defaultSet)
                .setDefaultMap(settings.defaultMap);
    }

    public char getRouteSeparator() {
        return separator;
    }

    public <T> List<T> getDefaultList(int size) {
        return defaultList.supply(size);
    }

    public <T> List<T> getDefaultList() {
        return getDefaultList(0);
    }

    public <T> Set<T> getDefaultSet(int size) {
        return defaultSet.supply(size);
    }

    public <T> Set<T> getDefaultSet() {
        return getDefaultSet(0);
    }

    public <K, V> Map<K, V> getDefaultMap(int size) {
        return defaultMap.supply(size);
    }

    public <K, V> Map<K, V> getDefaultMap() {
        return getDefaultMap(0);
    }

    public MapSupplier getDefaultMapSupplier() {
        return defaultMap;
    }


    public enum KeyFormat {


        STRING,


        OBJECT
    }

    public static class Builder {

        private KeyFormat keyFormat = DEFAULT_KEY_FORMATTING;

        private char routeSeparator = DEFAULT_ROUTE_SEPARATOR;

        private YamlSerializer serializer = DEFAULT_SERIALIZER;

        private boolean useDefaults = DEFAULT_USE_DEFAULTS;

        private Object defaultObject = DEFAULT_OBJECT;

        private Number defaultNumber = DEFAULT_NUMBER;

        private String defaultString = DEFAULT_STRING;

        private Character defaultChar = DEFAULT_CHAR;

        private Boolean defaultBoolean = DEFAULT_BOOLEAN;

        private ListSupplier defaultList = DEFAULT_LIST;

        private SetSupplier defaultSet = DEFAULT_SET;

        private MapSupplier defaultMap = DEFAULT_MAP;


        private Builder() {
        }


        public Builder setKeyFormat(KeyFormat keyFormat) {
            this.keyFormat = keyFormat;
            return this;
        }


        public Builder setRouteSeparator(char separator) {
            this.routeSeparator = separator;
            return this;
        }


        public Builder setSerializer(YamlSerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        /**
         * Sets if to enable use of the defaults by {@link Section} methods (if any are present).
         * <p>
         * Not effective if there are no {@link YamlDocument#getDefaults() defaults associated} with the document.
         * <p>
         * <b>If enabled (<code>true</code>):</b>
         * <ul>
         *     <li>
         *         Bulk getter methods (which return a set/map of all keys, routes, values, blocks) will not only include
         *         content from the file, but also from the equivalent section in the defaults.
         *     </li>
         *     <li>
         *         Value getters with signature <code>getX(route)</code> will search the defaults as documented. You can also view the behaviour in the
         *         call stack below:
         *         <ol>
         *             <li>
         *                 Is there any value at the specified route?
         *                 <ul>
         *                     <li>
         *                         <b>1A.</b> Yes: Is it compatible with the return type (see method documentation)?
         *                         <ul>
         *                             <li><b>2A.</b> Yes: Return it.</li>
         *                             <li>
         *                                 <b>2B.</b> No: Is there an equivalent of this section in the defaults ({@link Section#hasDefaults()})?
         *                                 <ul>
         *                                     <li>
         *                                         <b>3A.</b> Yes: Return the value returned by calling the same method on the default section equivalent ({@link Section#getDefaults()}).
         *                                     </li>
         *                                     <li>
         *                                         <b>3B.</b> No: Return the default value defined by the settings (see method documentation).
         *                                     </li>
         *                                 </ul>
         *                             </li>
         *                         </ul>
         *                     </li>
         *                     <li>
         *                         <b>1B.</b> No: Continue with 2B.
         *                     </li>
         *                 </ul>
         *             </li>
         *         </ol>
         *     </li>
         * </ul>
         * <b>If disabled (<code>false</code>):</b>
         * <ul>
         *     <li>
         *         None of the {@link Section} methods will interact with the defaults.
         *     </li>
         *     <li>
         *         This is recommended if you would like to handle all value absences (present in the defaults, but not
         *         in the file) and invalid values manually - e.g. notifying the user and then using the default value defined within
         *         <code>final</code> fields, or obtained via {@link Section#getDefaults()}.
         *     </li>
         * </ul>
         * <p>
         * <b>Default: </b>{@link #DEFAULT_USE_DEFAULTS}
         *
         * @param useDefaults if to use defaults
         * @return the builder
         */
        public Builder setUseDefaults(boolean useDefaults) {
            this.useDefaults = useDefaults;
            return this;
        }

        /**
         * Sets default object used by section getters if the return type is object.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_OBJECT}
         *
         * @param defaultObject default object
         * @return the builder
         */
        public Builder setDefaultObject(Object defaultObject) {
            this.defaultObject = defaultObject;
            return this;
        }

        /**
         * Sets default number used by section getters if the return type is a number - integer, float, byte,
         * biginteger... (per the getter documentation).
         * <p>
         * <b>Default: </b>{@link #DEFAULT_NUMBER}
         * <p>
         * <i>The given default can not be <code>null</code> as multiple section getters derive their defaults from
         * this default (using {@link Number#intValue()}...).</i>
         *
         * @param defaultNumber default number
         * @return the builder
         */
        public Builder setDefaultNumber(Number defaultNumber) {
            this.defaultNumber = defaultNumber;
            return this;
        }

        /**
         * Sets default string used by section getters if the return type is string.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_STRING}
         *
         * @param defaultString default string
         * @return the builder
         */
        public Builder setDefaultString(String defaultString) {
            this.defaultString = defaultString;
            return this;
        }

        /**
         * Sets default char used by section getters if the return type is char.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_CHAR}
         * <p>
         * <i>The parameter is not of a primitive type, to allow for <code>null</code> values. Setting the default to
         * such value might produce unexpected issues, unless your program is adapted for it. On the other hand, there
         * are methods returning optionals, so having default value like this is rather pointless.</i>
         *
         * @param defaultChar default char
         * @return the builder
         */
        public Builder setDefaultChar(Character defaultChar) {
            this.defaultChar = defaultChar;
            return this;
        }

        /**
         * Sets default boolean used by section getters if the return type is boolean.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_BOOLEAN}
         * <p>
         * <i>The parameter is not of a primitive type, to allow for <code>null</code> values. Setting the default to
         * such value might produce unexpected issues, unless your program is adapted for it. On the other hand, there
         * are methods returning optionals, so having default value like this is rather pointless.</i>
         *
         * @param defaultBoolean default boolean
         * @return the builder
         */
        public Builder setDefaultBoolean(Boolean defaultBoolean) {
            this.defaultBoolean = defaultBoolean;
            return this;
        }

        /**
         * Sets default list supplier used to supply list instances during loading/when needed.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_LIST}
         *
         * @param defaultList the supplier
         * @return the builder
         */
        public Builder setDefaultList(ListSupplier defaultList) {
            this.defaultList = defaultList;
            return this;
        }

        /**
         * Sets default set supplier used to supply set instances during loading/when needed.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_SET}
         *
         * @param defaultSet the supplier
         * @return the builder
         */
        public Builder setDefaultSet(SetSupplier defaultSet) {
            this.defaultSet = defaultSet;
            return this;
        }

        /**
         * Sets default map supplier used to supply map instances during loading/creating new sections/when needed.
         * <p>
         * <b>Default: </b>{@link #DEFAULT_MAP}
         *
         * @param defaultMap the supplier
         * @return the builder
         */
        public Builder setDefaultMap(MapSupplier defaultMap) {
            this.defaultMap = defaultMap;
            return this;
        }

        /**
         * Builds the settings.
         *
         * @return the settings
         */
        public GeneralSettings build() {
            return new GeneralSettings(this);
        }

    }


}
