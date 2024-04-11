package it.voxibyte.belt.document.settings.updater;

import it.voxibyte.belt.document.YamlDocument;
import it.voxibyte.belt.document.dvs.Pattern;
import it.voxibyte.belt.document.dvs.versioning.AutomaticVersioning;
import it.voxibyte.belt.document.dvs.versioning.ManualVersioning;
import it.voxibyte.belt.document.dvs.versioning.Versioning;
import it.voxibyte.belt.document.route.Route;
import it.voxibyte.belt.document.route.RouteFactory;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


@SuppressWarnings("unused")
public class UpdaterSettings {


    public static final boolean DEFAULT_AUTO_SAVE = true;
    public static final boolean DEFAULT_ENABLE_DOWNGRADING = true;
    public static final boolean DEFAULT_KEEP_ALL = false;
    public static final OptionSorting DEFAULT_OPTION_SORTING = OptionSorting.SORT_BY_DEFAULTS;
    public static final Map<MergeRule, Boolean> DEFAULT_MERGE_RULES = Collections.unmodifiableMap(new HashMap<MergeRule, Boolean>() {{
        put(MergeRule.MAPPINGS, true);
        put(MergeRule.MAPPING_AT_SECTION, false);
        put(MergeRule.SECTION_AT_MAPPING, false);
    }});
    public static final Versioning DEFAULT_VERSIONING = null;
    public static final UpdaterSettings DEFAULT = builder().build();
    @Getter
    private final boolean autoSave;
    @Getter
    private final boolean enableDowngrading;
    @Getter
    private final boolean keepAll;
    @Getter
    private final Map<MergeRule, Boolean> mergeRules;
    private final Map<String, RouteSet> ignored;
    private final Map<String, RouteMap<Route, String>> relocations;
    private final Map<String, Map<Route, ValueMapper>> mappers;
    private final Map<String, List<Consumer<YamlDocument>>> customLogic;
    @Getter
    private final Versioning versioning;
    @Getter
    private final OptionSorting optionSorting;

    public UpdaterSettings(Builder builder) {
        this.autoSave = builder.autoSave;
        this.enableDowngrading = builder.enableDowngrading;
        this.keepAll = builder.keepAll;
        this.optionSorting = builder.optionSorting;
        this.mergeRules = builder.mergeRules;
        this.ignored = builder.ignored;
        this.relocations = builder.relocations;
        this.mappers = builder.mappers;
        this.customLogic = builder.customLogic;
        this.versioning = builder.versioning;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UpdaterSettings settings) {
        return builder()
                .setAutoSave(settings.autoSave)
                .setEnableDowngrading(settings.enableDowngrading)
                .setKeepAll(settings.keepAll)
                .setOptionSorting(settings.optionSorting)
                .setMergeRules(settings.mergeRules)
                .setIgnoredRoutesInternal(settings.ignored)
                .setRelocationsInternal(settings.relocations)
                .addMappers(settings.mappers)
                .addCustomLogic(settings.customLogic)
                .setVersioning(settings.versioning);
    }

    public Set<Route> getIgnoredRoutes(String versionId, char separator) {
        RouteSet ignored = this.ignored.get(versionId);
        return ignored == null ? Collections.emptySet() : ignored.merge(separator);
    }

    public Map<Route, Route> getRelocations(String versionId, char separator) {
        RouteMap<Route, String> relocations = this.relocations.get(versionId);
        return relocations == null ? Collections.emptyMap() : relocations.merge(Function.identity(), route -> Route.fromString(route, separator), separator);
    }

    public Map<Route, ValueMapper> getMappers(String versionId, char separator) {
        return mappers.getOrDefault(versionId, Collections.emptyMap());
    }

    public List<Consumer<YamlDocument>> getCustomLogic(String versionId) {
        return customLogic.getOrDefault(versionId, Collections.emptyList());
    }


    public enum OptionSorting {


        NONE,


        SORT_BY_DEFAULTS
    }

    public static class Builder {


        private final Map<MergeRule, Boolean> mergeRules = new HashMap<>(DEFAULT_MERGE_RULES);
        private final Map<String, RouteSet> ignored = new HashMap<>();
        private final Map<String, RouteMap<Route, String>> relocations = new HashMap<>();
        private final Map<String, Map<Route, ValueMapper>> mappers = new HashMap<>();
        private final Map<String, List<Consumer<YamlDocument>>> customLogic = new HashMap<>();
        private boolean autoSave = DEFAULT_AUTO_SAVE;
        private boolean enableDowngrading = DEFAULT_ENABLE_DOWNGRADING;
        private boolean keepAll = DEFAULT_KEEP_ALL;
        private Versioning versioning = DEFAULT_VERSIONING;

        private OptionSorting optionSorting = DEFAULT_OPTION_SORTING;


        private Builder() {
        }


        public Builder setAutoSave(boolean autoSave) {
            this.autoSave = autoSave;
            return this;
        }


        public Builder setEnableDowngrading(boolean enableDowngrading) {
            this.enableDowngrading = enableDowngrading;
            return this;
        }


        public Builder setKeepAll(boolean keepAll) {
            this.keepAll = keepAll;
            return this;
        }


        public Builder setOptionSorting(OptionSorting optionSorting) {
            this.optionSorting = optionSorting;
            return this;
        }


        public Builder setMergeRules(Map<MergeRule, Boolean> mergeRules) {
            this.mergeRules.putAll(mergeRules);
            return this;
        }


        public Builder setMergeRule(MergeRule rule, boolean preserveDocument) {
            this.mergeRules.put(rule, preserveDocument);
            return this;
        }


        private Builder setIgnoredRoutesInternal(Map<String, RouteSet> routes) {
            this.ignored.putAll(routes);
            return this;
        }


        
        public Builder setIgnoredRoutes(Map<String, Set<Route>> routes) {
            routes.forEach((versionId, set) -> this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getRouteSet().addAll(set));
            return this;
        }


        
        public Builder setIgnoredRoutes(String versionId, Set<Route> routes) {
            this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getRouteSet().addAll(routes);
            return this;
        }


        
        public Builder setIgnoredStringRoutes(Map<String, Set<String>> routes) {
            routes.forEach((versionId, set) -> this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getStringSet().addAll(set));
            return this;
        }


        
        public Builder setIgnoredStringRoutes(String versionId, Set<String> routes) {
            this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getStringSet().addAll(routes);
            return this;
        }


        public Builder addIgnoredRoute(String versionId, Route route) {
            return addIgnoredRoutes(versionId, Collections.singleton(route));
        }


        public Builder addIgnoredRoutes(String versionId, Set<Route> routes) {
            return addIgnoredRoutes(Collections.singletonMap(versionId, routes));
        }


        public Builder addIgnoredRoutes(Map<String, Set<Route>> routes) {
            routes.forEach((versionId, set) -> this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getRouteSet().addAll(set));
            return this;
        }


        public Builder addIgnoredRoute(String versionId, String route, char separator) {
            return addIgnoredRoutes(versionId, Collections.singleton(route), separator);
        }


        public Builder addIgnoredRoutes(String versionId, Set<String> routes, char separator) {
            addIgnoredRoutes(versionId, routes, new RouteFactory(separator));
            return this;
        }


        public Builder addIgnoredRoutes(Map<String, Set<String>> routes, char separator) {
            RouteFactory factory = new RouteFactory(separator);
            routes.forEach((versionId, collection) -> addIgnoredRoutes(versionId, collection, factory));
            return this;
        }


        private void addIgnoredRoutes(String versionId, Set<String> routes, RouteFactory factory) {
            Set<Route> set = this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getRouteSet();
            routes.forEach(route -> set.add(factory.create(route)));
        }


        private Builder setRelocationsInternal(Map<String, RouteMap<Route, String>> relocations) {
            this.relocations.putAll(relocations);
            return this;
        }


        
        public Builder setRelocations(Map<String, Map<Route, Route>> relocations) {
            relocations.forEach((versionId, map) -> this.relocations.computeIfAbsent(versionId, key -> new RouteMap<>()).getRouteMap().putAll(map));
            return this;
        }


        
        public Builder setRelocations(String versionId, Map<Route, Route> relocations) {
            this.relocations.computeIfAbsent(versionId, key -> new RouteMap<>()).getRouteMap().putAll(relocations);
            return this;
        }


        
        public Builder setStringRelocations(Map<String, Map<String, String>> relocations) {
            relocations.forEach((versionId, map) -> this.relocations.computeIfAbsent(versionId, key -> new RouteMap<>()).getStringMap().putAll(map));
            return this;
        }


        
        public Builder setStringRelocations(String versionId, Map<String, String> relocations) {
            this.relocations.computeIfAbsent(versionId, key -> new RouteMap<>()).getStringMap().putAll(relocations);
            return this;
        }


        public Builder addRelocation(String versionId, Route fromRoute, Route toRoute) {
            return addRelocations(versionId, Collections.singletonMap(fromRoute, toRoute));
        }


        public Builder addRelocations(String versionId, Map<Route, Route> relocations) {
            return addRelocations(Collections.singletonMap(versionId, relocations));
        }


        public Builder addRelocations(Map<String, Map<Route, Route>> relocations) {
            relocations.forEach((versionId, map) -> this.relocations.computeIfAbsent(versionId, key -> new RouteMap<>()).getRouteMap().putAll(map));
            return this;
        }


        public Builder addRelocation(String versionId, String fromRoute, String toRoute, char separator) {
            return addRelocations(versionId, Collections.singletonMap(fromRoute, toRoute), separator);
        }


        public Builder addRelocations(String versionId, Map<String, String> relocations, char separator) {
            addRelocations(Collections.singletonMap(versionId, relocations), separator);
            return this;
        }


        public Builder addRelocations(Map<String, Map<String, String>> relocations, char separator) {
            RouteFactory factory = new RouteFactory(separator);
            relocations.forEach((versionId, collection) -> {
                Map<Route, Route> map = this.relocations.computeIfAbsent(versionId, key -> new RouteMap<>()).getRouteMap();
                collection.forEach((from, to) -> map.put(factory.create(from), factory.create(to)));
            });
            return this;
        }


        public Builder addMapper(String versionId, Route route, ValueMapper mapper) {
            return addMappers(versionId, Collections.singletonMap(route, mapper));
        }


        public Builder addMappers(String versionId, Map<Route, ValueMapper> mappers) {
            return addMappers(Collections.singletonMap(versionId, mappers));
        }


        public Builder addMappers(Map<String, Map<Route, ValueMapper>> mappers) {
            mappers.forEach((versionId, map) -> this.mappers.computeIfAbsent(versionId, key -> new HashMap<>()).putAll(map));
            return this;
        }


        public Builder addMapper(String versionId, String route, ValueMapper mapper, char separator) {
            return addMappers(versionId, Collections.singletonMap(route, mapper), separator);
        }


        public Builder addMappers(String versionId, Map<String, ValueMapper> mappers, char separator) {
            return addMappers(Collections.singletonMap(versionId, mappers), separator);
        }


        public Builder addMappers(Map<String, Map<String, ValueMapper>> mappers, char separator) {
            RouteFactory factory = new RouteFactory(separator);
            mappers.forEach((versionId, collection) -> {
                Map<Route, ValueMapper> map = this.mappers.computeIfAbsent(versionId, key -> new HashMap<>());
                collection.forEach((route, mapper) -> map.put(factory.create(route), mapper));
            });
            return this;
        }


        public Builder addCustomLogic(String versionId, Consumer<YamlDocument> consumer) {
            return addCustomLogic(versionId, Collections.singletonList(consumer));
        }


        public Builder addCustomLogic(Map<String, List<Consumer<YamlDocument>>> consumers) {
            consumers.forEach(this::addCustomLogic);
            return this;
        }


        public Builder addCustomLogic(String versionId, Collection<Consumer<YamlDocument>> consumers) {
            customLogic.computeIfAbsent(versionId, key -> new ArrayList<>()).addAll(consumers);
            return this;
        }


        public Builder setVersioning(Versioning versioning) {
            this.versioning = versioning;
            return this;
        }


        public Builder setVersioning(Pattern pattern, String documentVersionId, String defaultsVersionId) {
            return setVersioning(new ManualVersioning(pattern, documentVersionId, defaultsVersionId));
        }


        public Builder setVersioning(Pattern pattern, Route route) {
            return setVersioning(new AutomaticVersioning(pattern, route));
        }


        public Builder setVersioning(Pattern pattern, String route) {
            return setVersioning(new AutomaticVersioning(pattern, route));
        }


        public UpdaterSettings build() {
            return new UpdaterSettings(this);
        }
    }


    private static class RouteMap<R, S> {


        private Map<Route, R> routes = null;
        private Map<String, S> strings = null;


        public <T> Map<Route, T> merge(Function<R, T> routeMapper, Function<S, T> stringMapper, char separator) {
            if ((routes == null || routes.isEmpty()) && (strings == null || strings.isEmpty()))
                return Collections.emptyMap();
            Map<Route, T> map = new HashMap<>();
            if (strings != null)
                strings.forEach((key, value) -> map.put(Route.fromString(key, separator), stringMapper.apply(value)));
            if (routes != null)
                routes.forEach((key, value) -> map.put(key, routeMapper.apply(value)));
            return map;
        }


        public Map<Route, R> getRouteMap() {
            return routes == null ? routes = new HashMap<>() : routes;
        }


        public Map<String, S> getStringMap() {
            return strings == null ? strings = new HashMap<>() : strings;
        }
    }


    private static class RouteSet {


        private Set<Route> routes = null;
        private Set<String> strings = null;


        public Set<Route> merge(char separator) {
            if ((routes == null || routes.isEmpty()) && (strings == null || strings.isEmpty()))
                return Collections.emptySet();
            Set<Route> set = new HashSet<>();
            if (strings != null)
                strings.forEach(route -> set.add(Route.fromString(route, separator)));
            if (routes != null)
                set.addAll(routes);
            return set;
        }


        public Set<Route> getRouteSet() {
            return routes == null ? routes = new HashSet<>() : routes;
        }


        public Set<String> getStringSet() {
            return strings == null ? strings = new HashSet<>() : strings;
        }

    }
}
