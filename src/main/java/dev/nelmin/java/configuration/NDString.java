package dev.nelmin.java.configuration;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
public class NDString {
    @Getter protected final String value;

    protected NDString(String value) {
        this.value = value;
    }

    public @Nullable String getOrNull() {
        return value == null || value.isBlank() ? null : value;
    }

    /**
     * Creates a new NDString instance with the given value.
     *
     * @param value The string value
     * @return A new NDString instance
     */
    public static @NotNull NDString of(@Nullable String value) {
        return new NDString(value == null ? "" : value);
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isBlankOrNull() {
        return value == null || value.isBlank();
    }
}
