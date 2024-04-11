package it.voxibyte.belt.document.dvs.versioning;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.dvs.Version;
import org.jetbrains.annotations.Nullable;


public interface Versioning {


    @Nullable
    Version getDocumentVersion(Section document, boolean defaults);

    Version getFirstVersion();


    default void updateVersionID(Section updated, Section def) {
    }

}
