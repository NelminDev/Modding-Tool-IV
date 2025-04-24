package dev.nelmin.java.objects;

public record IndexedMod(
        String id,
        String name,
        String[] authors,
        String description,
        Links links,
        Version[] versions,
        String[] tags
) {
    public record Links(
            String website,
            String source_code
    ) {
    }

    public record Version(
            String version,
            FileInfo file,
            String release_date,
            String[] dependencies,
            Compatibility compatibility,
            Requirements requirements
    ) {
        public record FileInfo(
                String download,
                String sha512,
                long file_size
        ) {
        }

        public record Compatibility(
                String[] game_versions,
                String[] incompatible_mods
        ) {
        }

        public record Requirements(
                Hardware cpu,
                Hardware gpu,
                Hardware ram_gb,
                String storage_gb
        ) {
            public record Hardware(
                    String min,
                    String suggested
            ) {
            }
        }
    }
}