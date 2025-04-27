package dev.nelmin.java.fs.files;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IMGFile extends NDFile {
    @Getter
    private final Path path;

    public IMGFile(@NotNull Path path) {
        this.path = path;
    }

    public IMGFile(@NotNull String path) {
        this.path = Path.of(path);
    }

    public boolean backup(String newlyInstalledModID) throws IOException {
        Path backupPath = pathOf("MTIV-Backups");
        if (!path.toFile().exists() || backupPath == null) return false;

        try (FileOutputStream outputStream = new FileOutputStream(
                backupPath.resolve(
                        path.getFileName().toString().replaceFirst("\\.img$", "-before-" + newlyInstalledModID + ".img")
                ).toFile()
        )) {
            Files.copy(
                    path,
                    outputStream
            );
            return true;
        }
    }
}
