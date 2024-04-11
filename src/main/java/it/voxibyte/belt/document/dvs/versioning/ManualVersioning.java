package it.voxibyte.belt.document.dvs.versioning;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.dvs.Pattern;
import it.voxibyte.belt.document.dvs.Version;
import org.jetbrains.annotations.Nullable;


public class ManualVersioning implements Versioning {


    private final Version documentVersion;
    private final Version defaultsVersion;


    public ManualVersioning(Pattern pattern, String documentVersionId, String defaultsVersionId) {
        this.documentVersion = documentVersionId == null ? null : pattern.getVersion(documentVersionId);
        this.defaultsVersion = pattern.getVersion(defaultsVersionId);
    }

    @Nullable
    @Override

    public Version getDocumentVersion(Section document, boolean defaults) {
        return defaults ? defaultsVersion : documentVersion;
    }

    @Override

    public Version getFirstVersion() {
        return defaultsVersion.getPattern().getFirstVersion();
    }

    @Override

    public String toString() {
        return "ManualVersioning{" +
                "documentVersion=" + documentVersion +
                ", defaultsVersion=" + defaultsVersion +
                '}';
    }
}
