package it.voxibyte.belt.document.settings.loader;

import it.voxibyte.belt.document.settings.general.GeneralSettings;
import lombok.Getter;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.LoadSettingsBuilder;
import org.snakeyaml.engine.v2.env.EnvConfig;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;

import java.util.Map;
import java.util.Optional;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LoaderSettings {


    public static final LoaderSettings DEFAULT = builder().build();


    private final LoadSettingsBuilder builder;

    @Getter
    private final boolean createFileIfAbsent, autoUpdate;


    private LoaderSettings(Builder builder) {
        this.builder = builder.builder;
        this.autoUpdate = builder.autoUpdate;
        this.createFileIfAbsent = builder.createFileIfAbsent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LoadSettingsBuilder builder) {
        return new Builder(builder);
    }

    public static Builder builder(LoaderSettings settings) {
        return builder(settings.builder)
                .setAutoUpdate(settings.autoUpdate)
                .setCreateFileIfAbsent(settings.createFileIfAbsent);
    }

    public LoadSettings buildEngineSettings(GeneralSettings generalSettings) {
        return this.builder.setParseComments(true).setDefaultList(generalSettings::getDefaultList).setDefaultSet(generalSettings::getDefaultSet).setDefaultMap(generalSettings::getDefaultMap).build();
    }

    public static class Builder {


        public static final boolean DEFAULT_CREATE_FILE_IF_ABSENT = true;

        public static final boolean DEFAULT_AUTO_UPDATE = false;

        public static final boolean DEFAULT_DETAILED_ERRORS = true;

        public static final boolean DEFAULT_ALLOW_DUPLICATE_KEYS = true;


        private final LoadSettingsBuilder builder;

        private boolean autoUpdate = DEFAULT_AUTO_UPDATE, createFileIfAbsent = DEFAULT_CREATE_FILE_IF_ABSENT;


        private Builder(LoadSettingsBuilder builder) {
            this.builder = builder;
        }


        private Builder() {

            this.builder = LoadSettings.builder();

            setDetailedErrors(DEFAULT_DETAILED_ERRORS);
            setAllowDuplicateKeys(DEFAULT_ALLOW_DUPLICATE_KEYS);
        }


        public Builder setCreateFileIfAbsent(boolean createFileIfAbsent) {
            this.createFileIfAbsent = createFileIfAbsent;
            return this;
        }


        public Builder setAutoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
            return this;
        }


        public Builder setErrorLabel(String label) {
            builder.setLabel(label);
            return this;
        }


        public Builder setDetailedErrors(boolean detailedErrors) {
            builder.setUseMarks(detailedErrors);
            return this;
        }


        public Builder setAllowDuplicateKeys(boolean allowDuplicateKeys) {
            builder.setAllowDuplicateKeys(allowDuplicateKeys);
            return this;
        }


        public Builder setMaxCollectionAliases(int maxCollectionAliases) {
            builder.setMaxAliasesForCollections(maxCollectionAliases);
            return this;
        }


        public Builder setTagConstructors(Map<Tag, ConstructNode> constructors) {
            builder.setTagConstructors(constructors);
            return this;
        }


        public Builder setScalarResolver(ScalarResolver resolver) {
            builder.setScalarResolver(resolver);
            return this;
        }


        public Builder setEnvironmentConfig(EnvConfig envConfig) {
            builder.setEnvConfig(Optional.ofNullable(envConfig));
            return this;
        }


        public LoaderSettings build() {
            return new LoaderSettings(this);
        }
    }

}
