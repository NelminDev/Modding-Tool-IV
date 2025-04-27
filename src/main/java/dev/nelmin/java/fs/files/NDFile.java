package dev.nelmin.java.fs.files;

import dev.nelmin.java.MTIV;
import dev.nelmin.java.configuration.NDString;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class NDFile {
    public static @Nullable Path pathOf(String path) {
        return pathOf(Path.of(path));
    }

    public static @Nullable Path pathOf(Path path) {
        NDString defaultVersion = MTIV.config().getNDString("game.default");
        NDString gameDirectory = MTIV.config().getNDString("game." + defaultVersion.value() + ".directory");

        if (defaultVersion.isBlankOrNull() || gameDirectory.isBlankOrNull()) return null;

        return Path.of(gameDirectory.value()).resolve(path);
    }
}
