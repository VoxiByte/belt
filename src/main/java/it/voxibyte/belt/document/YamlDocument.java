package it.voxibyte.belt.document;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.engine.ExtendedConstructor;
import it.voxibyte.belt.document.engine.ExtendedRepresenter;
import it.voxibyte.belt.document.settings.dumper.DumperSettings;
import it.voxibyte.belt.document.settings.general.GeneralSettings;
import it.voxibyte.belt.document.settings.loader.LoaderSettings;
import it.voxibyte.belt.document.settings.updater.UpdaterSettings;
import it.voxibyte.belt.document.updater.Updater;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.StreamDataWriter;
import org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.parser.Parser;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.representer.BaseRepresenter;
import org.snakeyaml.engine.v2.scanner.StreamReader;
import org.snakeyaml.engine.v2.serializer.Serializer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;


@SuppressWarnings("unused")
public class YamlDocument extends Section {


    private final File file;

    private final YamlDocument defaults;

    @Getter
    private GeneralSettings generalSettings;
    @Getter
    private LoaderSettings loaderSettings;
    @Getter
    private DumperSettings dumperSettings;
    @Getter
    private UpdaterSettings updaterSettings;


    private YamlDocument(InputStream document, InputStream defaults, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) throws IOException {

        super(generalSettings.getDefaultMap());

        this.generalSettings = generalSettings;
        this.loaderSettings = loaderSettings;
        this.dumperSettings = dumperSettings;
        this.updaterSettings = updaterSettings;
        this.file = null;
        this.defaults = defaults == null ? null : new YamlDocument(defaults, null, generalSettings, loaderSettings, dumperSettings, updaterSettings);


        reload(document);
    }


    private YamlDocument(File document, InputStream defaults, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) throws IOException {

        super(generalSettings.getDefaultMap());

        this.generalSettings = generalSettings;
        this.loaderSettings = loaderSettings;
        this.dumperSettings = dumperSettings;
        this.updaterSettings = updaterSettings;
        this.file = document;
        this.defaults = defaults == null ? null : new YamlDocument(defaults, null, generalSettings, loaderSettings, dumperSettings, updaterSettings);

        reload();
    }

    public static YamlDocument create(File document, InputStream defaults, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) throws IOException {
        return new YamlDocument(document, defaults, generalSettings, loaderSettings, dumperSettings, updaterSettings);
    }

    public static YamlDocument create(File document, InputStream defaults) throws IOException {
        return create(document, defaults, GeneralSettings.DEFAULT, LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    public static YamlDocument create(InputStream document, InputStream defaults, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) throws IOException {
        return new YamlDocument(document, defaults, generalSettings, loaderSettings, dumperSettings, updaterSettings);
    }

    public static YamlDocument create(InputStream document, InputStream defaults) throws IOException {
        return create(document, defaults, GeneralSettings.DEFAULT, LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    public static YamlDocument create(File document, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) throws IOException {
        return new YamlDocument(document, null, generalSettings, loaderSettings, dumperSettings, updaterSettings);
    }

    public static YamlDocument create(File document) throws IOException {
        return create(document, GeneralSettings.DEFAULT, LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    public static YamlDocument create(InputStream document, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) throws IOException {
        return new YamlDocument(document, null, generalSettings, loaderSettings, dumperSettings, updaterSettings);
    }

    public static YamlDocument create(InputStream document) throws IOException {
        return create(document, GeneralSettings.DEFAULT, LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    public boolean reload() throws IOException {

        if (file == null)
            return false;

        reload(file);
        return true;
    }

    private void reload(File file) throws IOException {

        clear();

        if (Objects.requireNonNull(file, "File cannot be null!").exists()) {

            reload(new BufferedInputStream(new FileInputStream(file)));
            return;
        }


        if (loaderSettings.isCreateFileIfAbsent()) {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile();
        }


        if (defaults == null) {

            initEmpty(this);
            return;
        }


        String dump = defaults.dump();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {

            writer.write(dump);
        }

        BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(dump.getBytes(StandardCharsets.UTF_8)));
        reload(bufferedInputStream);

        bufferedInputStream.close();
    }

    public void reload(InputStream inputStream) throws IOException {
        reload(inputStream, loaderSettings);
        inputStream.close();
    }

    public void reload(InputStream inputStream, LoaderSettings loaderSettings) throws IOException {

        clear();


        LoadSettings settings = Objects.requireNonNull(loaderSettings, "Loader settings cannot be null!").buildEngineSettings(generalSettings);

        ExtendedConstructor constructor = new ExtendedConstructor(settings, generalSettings.getSerializer());

        Parser parser = new ParserImpl(settings, new StreamReader(settings, new YamlUnicodeReader(Objects.requireNonNull(inputStream, "Input stream cannot be null!"))));
        Composer composer = new Composer(settings, parser);


        if (composer.hasNext()) {

            Node node = composer.next();

            if (composer.hasNext())
                throw new InvalidObjectException("Multiple documents are not supported!");
            if (!(node instanceof MappingNode))
                throw new IllegalArgumentException(String.format("Top level object is not a map! Parsed node: %s", node.toString()));

            constructor.constructSingleDocument(Optional.of(node));

            init(this, null, (MappingNode) node, constructor);

            constructor.clear();
        } else {

            initEmpty(this);
        }


        if (file != null && loaderSettings.isCreateFileIfAbsent() && !file.exists()) {

            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile();

            save();
        }


        if (defaults != null && loaderSettings.isAutoUpdate())
            Updater.update(this, defaults, updaterSettings, generalSettings);
    }

    public boolean update() throws IOException {
        return update(updaterSettings);
    }

    public boolean update(UpdaterSettings updaterSettings) throws IOException {

        if (defaults == null)
            return false;

        Updater.update(this, defaults, Objects.requireNonNull(updaterSettings, "Updater settings cannot be null!"), generalSettings);
        return true;
    }

    public void update(InputStream defaults) throws IOException {
        update(defaults, updaterSettings);
        defaults.close();
    }

    public void update(InputStream defaults, UpdaterSettings updaterSettings) throws IOException {
        Updater.update(this, YamlDocument.create(Objects.requireNonNull(defaults, "Defaults cannot be null!"), generalSettings, loaderSettings, dumperSettings, UpdaterSettings.DEFAULT), Objects.requireNonNull(updaterSettings, "Updater settings cannot be null!"), generalSettings);
    }

    public boolean save() throws IOException {

        if (file == null)
            return false;


        save(file);
        return true;
    }

    public void save(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {

            writer.write(dump());
        }
    }

    public void save(OutputStream stream, Charset charset) throws IOException {
        stream.write(dump().getBytes(charset));
    }

    public void save(OutputStreamWriter writer) throws IOException {
        writer.write(dump());
    }

    public String dump() {
        return dump(dumperSettings);
    }

    public String dump(DumperSettings dumperSettings) {

        DumpSettings settings = dumperSettings.buildEngineSettings();

        SerializedStream stream = new SerializedStream();

        BaseRepresenter representer = new ExtendedRepresenter(generalSettings, dumperSettings, settings);

        Serializer serializer = new Serializer(settings, new Emitter(settings, stream));
        serializer.emitStreamStart();

        serializer.serializeDocument(representer.represent(this));

        serializer.emitStreamEnd();


        return stream.toString();
    }

    @Nullable
    public YamlDocument getDefaults() {
        return defaults;
    }

    public void setGeneralSettings(GeneralSettings generalSettings) {

        if (generalSettings.getKeyFormat() != this.generalSettings.getKeyFormat())
            throw new IllegalArgumentException("Cannot change key format! Recreate the file if needed to do so.");

        this.generalSettings = generalSettings;
    }

    public void setDumperSettings(DumperSettings dumperSettings) {
        this.dumperSettings = dumperSettings;
    }

    public void setUpdaterSettings(UpdaterSettings updaterSettings) {
        this.updaterSettings = updaterSettings;
    }

    public void setLoaderSettings(LoaderSettings loaderSettings) {
        this.loaderSettings = loaderSettings;
    }

    @Nullable
    public File getFile() {
        return file;
    }

    @Override

    public boolean isRoot() {
        return true;
    }

    private static class SerializedStream extends StringWriter implements StreamDataWriter {
    }

}
