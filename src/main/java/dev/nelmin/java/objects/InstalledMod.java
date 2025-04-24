package dev.nelmin.java.objects;

public record InstalledMod(
        String id,
        ChangedPath[] changed_paths
) {
    public record ChangedPath(
            String path,
            String backup_ver
    ) {
    }
}