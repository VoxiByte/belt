package it.voxibyte.belt.document.updater;

import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.dvs.Version;
import it.voxibyte.belt.document.dvs.versioning.Versioning;
import it.voxibyte.belt.document.settings.updater.UpdaterSettings;
import it.voxibyte.belt.document.updater.operators.Mapper;
import it.voxibyte.belt.document.updater.operators.Relocator;

import java.util.Objects;


public class VersionedOperations {


    public static boolean run(Section document, Section defaults, UpdaterSettings settings, char separator) {

        Versioning versioning = settings.getVersioning();

        if (versioning == null)
            return false;


        Version documentVersion = versioning.getDocumentVersion(document, false), defaultsVersion = Objects.requireNonNull(versioning.getDocumentVersion(defaults, true), "Version ID of the defaults cannot be null! Is it malformed or not specified?");


        int compared = documentVersion != null ? documentVersion.compareTo(defaultsVersion) : -1;

        if (compared > 0 && !settings.isEnableDowngrading())

            throw new UnsupportedOperationException(String.format("Downgrading is not enabled (%s > %s)!", defaultsVersion.asID(), documentVersion.asID()));


        if (compared == 0)
            return true;


        if (compared < 0)

            iterate(document, documentVersion != null ? documentVersion : versioning.getFirstVersion(), defaultsVersion, settings, separator);


        settings.getIgnoredRoutes(defaultsVersion.asID(), separator).forEach(route ->
                document.getOptionalBlock(route).ifPresent(block -> block.setIgnored(true)));
        return false;
    }


    private static void iterate(Section document, Version documentVersion, Version defaultsVersion, UpdaterSettings settings, char separator) {

        Version current = documentVersion.copy();

        while (current.compareTo(defaultsVersion) <= 0) {

            current.next();

            Relocator.apply(document, settings.getRelocations(current.asID(), separator));
            Mapper.apply(document, settings.getMappers(current.asID(), separator));

            settings.getCustomLogic(current.asID()).forEach(consumer -> consumer.accept(document.getRoot()));
        }
    }

}
