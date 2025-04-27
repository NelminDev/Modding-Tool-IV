package dev.nelmin.java.fs.files;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RPFFile extends NDFile {
    @Getter
    private final Path path;

    public RPFFile(@NotNull Path path) {
        this.path = path;
    }

    public RPFFile(@NotNull String path) {
        this.path = Path.of(path);
    }

    public boolean backup(String newlyInstalledModID) throws IOException {
        Path backupPath = pathOf("MTIV-Backups");
        if (!path.toFile().exists() || backupPath == null) return false;

        try (FileOutputStream outputStream = new FileOutputStream(
                backupPath.resolve(
                        path.getFileName().toString().replaceFirst("\\.img$", "-before-" + newlyInstalledModID + ".rpf")
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
