package it.voxibyte.belt.document.updater;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.settings.general.GeneralSettings;
import it.voxibyte.belt.document.settings.updater.UpdaterSettings;
import it.voxibyte.belt.document.updater.operators.Merger;

import java.io.IOException;


public class Updater {


    public static void update(Section document, Section defaults, UpdaterSettings updaterSettings, GeneralSettings generalSettings) throws IOException {

        if (VersionedOperations.run(document, defaults, updaterSettings, generalSettings.getRouteSeparator()))
            return;

        Merger.merge(document, defaults, updaterSettings);

        if (updaterSettings.getVersioning() != null)

            updaterSettings.getVersioning().updateVersionID(document, defaults);


        if (updaterSettings.isAutoSave())
            document.getRoot().save();
    }

}
