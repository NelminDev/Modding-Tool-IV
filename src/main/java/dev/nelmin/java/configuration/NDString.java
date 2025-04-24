package dev.nelmin.java.configuration;

import lombok.Getter;
import lombok.experimental.Accessors;
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
    
    @Override
    public String toString() {
        return value;
    }
}
