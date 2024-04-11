package it.voxibyte.belt.i18n;

import it.voxibyte.belt.Belt;
import it.voxibyte.belt.document.YamlDocument;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Language {

    @Getter
    private static Language instance;

    private final YamlDocument yamlDocument;

    private Language(YamlDocument yamlDocument) {
        this.yamlDocument = yamlDocument;
    }

    public static void init(JavaPlugin javaPlugin, String fileName) throws IOException {
        instance = createLanguage(javaPlugin, fileName);
    }

    private static Language createLanguage(JavaPlugin javaPlugin, String fileName) throws IOException {
        File pluginFolder = javaPlugin.getDataFolder();
        File languageFile = new File(pluginFolder, fileName + ".yml");
        InputStream defaults = Belt.getInstance().getClass().getClassLoader().getResourceAsStream(fileName + ".yml");

        YamlDocument document = YamlDocument.create(languageFile, defaults);

        return new Language(document);
    }

    public String getMessage(String path) {
        return this.yamlDocument.getString(path);
    }

    public List<String> getMessages(String path) {
        return this.yamlDocument.getStringList(path);
    }
}