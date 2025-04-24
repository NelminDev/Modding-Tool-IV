package dev.nelmin.java.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSONConfiguration is a Configuration implementation using JSON.
 * This implementation is similar to Bukkit's YAMLConfiguration in terms of API and usage.
 */
public class JSONConfiguration {
    private final JsonObject root;
    private final Gson gson;
    private File configFile;

    /**
     * Default constructor for the JSONConfiguration class.
     * Initializes the configuration with a Gson instance configured for pretty printing
     * and an empty JsonObject as the root.
     */
    public JSONConfiguration() {
        this(new GsonBuilder().setPrettyPrinting().create(), new JsonObject());
    }

    /**
     * Constructs a new JSONConfiguration instance using the given JsonObject as its root.
     *
     * @param root The root JsonObject for this configuration. Must not be null.
     */
    public JSONConfiguration(@NotNull JsonObject root) {
        this(new GsonBuilder().setPrettyPrinting().create(), root);
    }

    /**
     * Constructs a new JSONConfiguration instance with the provided Gson instance and root JsonObject.
     *
     * @param gson The Gson instance to use for JSON manipulation and serialization.
     * @param root The root JsonObject representing the underlying configuration data.
     */
    public JSONConfiguration(@NotNull Gson gson, @NotNull JsonObject root) {
        this.root = root;
        this.gson = gson;
    }

    /**
     * Loads configuration from a file.
     *
     * @param file File to load from
     * @return The loaded JSONConfiguration
     * @throws IOException If the file cannot be read
     */
    public static JSONConfiguration loadConfiguration(@NotNull File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("Configuration file not found: " + file.getAbsolutePath());
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JSONConfiguration config = new JSONConfiguration(root);
            config.configFile = file;
            return config;
        }
    }

    /**
     * Loads a configuration from a file located at the specified path.
     *
     * @param filePath The path of the file to load the configuration from. Must not be null.
     * @return The loaded JSONConfiguration instance.
     * @throws IOException If the file cannot be read.
     */
    public static JSONConfiguration loadConfiguration(@NotNull Path filePath) throws IOException {
        return loadConfiguration(filePath.toFile());
    }

    /**
     * Loads configuration from a file at the specified file path.
     *
     * @param filePath The path of the file to load the configuration from. Must not be null.
     * @return The loaded JSONConfiguration instance.
     * @throws IOException If the file cannot be read.
     */
    public static JSONConfiguration loadConfiguration(@NotNull String filePath) throws IOException {
        return loadConfiguration(new File(filePath));
    }

    /**
     * Loads configuration from a string.
     *
     * @param contents String contents to load from
     * @return The loaded JSONConfiguration
     */
    public static JSONConfiguration loadFromString(@NotNull String contents) {
        JsonObject root = JsonParser.parseString(contents).getAsJsonObject();
        return new JSONConfiguration(root);
    }

    /**
     * Creates a new empty JSONConfiguration.
     *
     * @return A new empty JSONConfiguration
     */
    public static JSONConfiguration create() {
        return new JSONConfiguration();
    }

    /**
     * Saves this configuration to a file.
     *
     * @param file File to save to
     * @throws IOException If the file cannot be written
     */
    public synchronized void save(@NotNull File file) throws IOException {
        Files.createDirectories(file.getParentFile().toPath());

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            synchronized (root) {
                writer.write(gson.toJson(root));
            }
        }

        this.configFile = file;
    }

    /**
     * Saves this configuration to its file.
     *
     * @throws IOException If the file cannot be written
     */
    public synchronized void save() throws IOException {
        if (configFile == null) {
            throw new IllegalStateException("No file specified for this configuration");
        }
        save(configFile);
    }

    /**
     * Converts this configuration to a string.
     *
     * @return String representation of this configuration
     */
    public synchronized String saveToString() {
        synchronized (root) {
            return gson.toJson(root);
        }
    }

    /**
     * Gets the requested Object by path.
     *
     * @param path Path of the Object to get
     * @return Requested Object
     */
    @Nullable
    public Object get(@NotNull String path) {
        return get(path, null);
    }

    /**
     * Gets the requested Object by path, returning a default value if not found.
     *
     * @param path Path of the Object to get
     * @param def  The default value to return if the path is not found
     * @return Requested Object
     */
    @Nullable
    public Object get(@NotNull String path, @Nullable Object def) {
        JsonElement element = getElement(path);
        if (element == null || element.isJsonNull()) {
            return def;
        }

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            }
        } else if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            JsonArray array = element.getAsJsonArray();
            for (JsonElement e : array) {
                list.add(jsonElementToObject(e));
            }
            return list;
        } else if (element.isJsonObject()) {
            Map<String, Object> map = new HashMap<>();
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                map.put(entry.getKey(), jsonElementToObject(entry.getValue()));
            }
            return map;
        }

        return def;
    }

    private Object jsonElementToObject(JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            }
        } else if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            JsonArray array = element.getAsJsonArray();
            for (JsonElement e : array) {
                list.add(jsonElementToObject(e));
            }
            return list;
        } else if (element.isJsonObject()) {
            Map<String, Object> map = new HashMap<>();
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                map.put(entry.getKey(), jsonElementToObject(entry.getValue()));
            }
            return map;
        }
        return null;
    }

    /**
     * Parses a path string, respecting escaped dots.
     *
     * @param path The path string to parse
     * @return Array of path segments
     */
    private String[] parsePath(@NotNull String path) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);

            if (escaped) {
                // If the character was escaped, add it literally
                currentPart.append(c);
                escaped = false;
            } else if (c == '\\') {
                // Start of an escape sequence
                escaped = true;
            } else if (c == '.') {
                // End of a path segment
                parts.add(currentPart.toString());
                currentPart = new StringBuilder();
            } else {
                // Regular character
                currentPart.append(c);
            }
        }

        // Add the last part
        if (currentPart.length() > 0 || parts.isEmpty()) {
            parts.add(currentPart.toString());
        }

        return parts.toArray(new String[0]);
    }

    /**
     * Sets the specified path to the given value.
     *
     * @param path  Path of the object to set
     * @param value New value to set the path to
     */
    public synchronized void set(@NotNull String path, @Nullable Object value) {
        // Parse the path normally, handling escaped dots in the parsePath method
        String[] parts = parsePath(path);

        synchronized (root) {
            JsonObject current = root;

            // Navigate through the path, creating objects as needed
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                JsonElement element = current.get(part);

                JsonObject nextObject;
                if (element == null || !element.isJsonObject()) {
                    nextObject = new JsonObject();
                    current.add(part, nextObject);
                } else {
                    nextObject = element.getAsJsonObject();
                }
                current = nextObject;
            }

            // Handle the final element
            String lastPart = parts[parts.length - 1];
            if (value == null) {
                current.remove(lastPart);
            } else {
                JsonElement jsonElement = objectToJsonElement(value);

                // Remove the key if it exists to move it to the bottom
                current.remove(lastPart);

                // Add the key with the new value
                current.add(lastPart, jsonElement);
            }
        }
    }

    private JsonElement objectToJsonElement(Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        } else if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        } else if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        } else if (value instanceof String) {
            return new JsonPrimitive((String) value);
        } else if (value instanceof NDString) {
            return new JsonPrimitive(((NDString) value).value());
        } else if (value instanceof List) {
            JsonArray array = new JsonArray();
            for (Object item : (List<?>) value) {
                array.add(objectToJsonElement(item));
            }
            return array;
        } else if (value instanceof Map) {
            JsonObject object = new JsonObject();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                object.add(entry.getKey(), objectToJsonElement(entry.getValue()));
            }
            return object;
        } else {
            return new JsonPrimitive(value.toString());
        }
    }

    /**
     * Gets the requested String by path.
     *
     * @param path Path of the String to get
     * @return Requested String
     */
    @Nullable
    public String getString(@NotNull String path) {
        return getString(path, null);
    }

    /**
     * Gets the requested String by path, returning a default value if not found.
     *
     * @param path Path of the String to get
     * @param def  The default value to return if the path is not found
     * @return Requested String
     */
    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsString();
        }
        return def;
    }

    /**
     * Gets the requested int by path.
     *
     * @param path Path of the int to get
     * @return Requested int
     */
    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    /**
     * Gets the requested int by path, returning a default value if not found.
     *
     * @param path Path of the int to get
     * @param def  The default value to return if the path is not found
     * @return Requested int
     */
    public int getInt(@NotNull String path, int def) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        }
        return def;
    }

    /**
     * Gets the requested boolean by path.
     *
     * @param path Path of the boolean to get
     * @return Requested boolean
     */
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    /**
     * Gets the requested boolean by path, returning a default value if not found.
     *
     * @param path Path of the boolean to get
     * @param def  The default value to return if the path is not found
     * @return Requested boolean
     */
    public boolean getBoolean(@NotNull String path, boolean def) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        }
        return def;
    }

    /**
     * Gets the requested NDString by path.
     *
     * @param path Path of the NDString to get
     * @return Requested NDString
     */
    @NotNull
    public NDString getNDString(@NotNull String path) {
        return getNDString(path, null);
    }

    /**
     * Gets the requested NDString by path, returning a default value if not found.
     *
     * @param path Path of the NDString to get
     * @param def  The default value to return if the path is not found
     * @return Requested NDString
     */
    @NotNull
    public NDString getNDString(@NotNull String path, @Nullable NDString def) {
        String value = getString(path);
        if (value != null) {
            return NDString.of(value);
        }
        return def;
    }

    /**
     * Gets the requested double by path.
     *
     * @param path Path of the double to get
     * @return Requested double
     */
    public double getDouble(@NotNull String path) {
        return getDouble(path, 0.0);
    }

    /**
     * Gets the requested double by path, returning a default value if not found.
     *
     * @param path Path of the double to get
     * @param def  The default value to return if the path is not found
     * @return Requested double
     */
    public double getDouble(@NotNull String path, double def) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsDouble();
        }
        return def;
    }

    /**
     * Gets the requested long by path.
     *
     * @param path Path of the long to get
     * @return Requested long
     */
    public long getLong(@NotNull String path) {
        return getLong(path, 0L);
    }

    /**
     * Gets the requested long by path, returning a default value if not found.
     *
     * @param path Path of the long to get
     * @param def  The default value to return if the path is not found
     * @return Requested long
     */
    public long getLong(@NotNull String path, long def) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsLong();
        }
        return def;
    }

    /**
     * Gets the requested List by path.
     *
     * @param path Path of the List to get
     * @return Requested List
     */
    @Nullable
    public List<?> getList(@NotNull String path) {
        return getList(path, null);
    }

    /**
     * Gets the requested List by path, returning a default value if not found.
     *
     * @param path Path of the List to get
     * @param def  The default value to return if the path is not found
     * @return Requested List
     */
    @Nullable
    public List<?> getList(@NotNull String path, @Nullable List<?> def) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            JsonArray array = element.getAsJsonArray();
            for (JsonElement e : array) {
                list.add(jsonElementToObject(e));
            }
            return list;
        }
        return def;
    }

    /**
     * Gets the requested List of Strings by path.
     *
     * @param path Path of the List to get
     * @return Requested List of Strings
     */
    @NotNull
    public List<String> getStringList(@NotNull String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (Object obj : list) {
            if (obj != null) {
                result.add(obj.toString());
            }
        }
        return result;
    }

    /**
     * Gets the requested List of Integers by path.
     *
     * @param path Path of the List to get
     * @return Requested List of Integers
     */
    @NotNull
    public List<Integer> getIntegerList(@NotNull String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>();
        for (Object obj : list) {
            if (obj instanceof Number) {
                result.add(((Number) obj).intValue());
            } else if (obj instanceof String) {
                try {
                    result.add(Integer.parseInt((String) obj));
                } catch (NumberFormatException ignored) {
                    // Skip invalid values
                }
            }
        }
        return result;
    }

    /**
     * Gets the requested ConfigurationSection by path.
     *
     * @param path Path of the ConfigurationSection to get
     * @return Requested ConfigurationSection as a JSONConfiguration
     */
    @Nullable
    public JSONConfiguration getConfigurationSection(@NotNull String path) {
        JsonElement element = getElement(path);
        if (element != null && element.isJsonObject()) {
            return new JSONConfiguration(element.getAsJsonObject());
        }
        return null;
    }

    /**
     * Gets the JsonElement at the specified path.
     *
     * @param path Path of the element to get
     * @return The JsonElement at the path, or null if not found
     */
    @Nullable
    private JsonElement getElement(@NotNull String path) {
        // Check if the path contains escaped dots
        if (path.contains("\\.")) {
            // If it does, treat it as a single key
            // Replace escaped dots with actual dots for retrieval
            String unescapedPath = path.replace("\\.", ".");

            synchronized (root) {
                return root.get(unescapedPath);
            }
        }

        // Otherwise, parse the path normally
        String[] parts = parsePath(path);

        synchronized (root) {
            JsonElement current = root;

            for (String part : parts) {
                if (current == null || !current.isJsonObject()) {
                    return null;
                }

                current = current.getAsJsonObject().get(part);
                if (current == null) {
                    return null;
                }
            }

            return current;
        }
    }

    /**
     * Checks if the specified path exists and contains data.
     *
     * @param path Path to check
     * @return True if the path exists and contains data
     */
    public boolean contains(@NotNull String path) {
        return getElement(path) != null;
    }

    /**
     * Gets a set of keys at the specified path.
     *
     * @param path Path to get keys from
     * @return Set of keys
     */
    @NotNull
    public Set<String> getKeys(@NotNull String path) {
        JsonElement element = path.isEmpty() ? root : getElement(path);
        if (element != null && element.isJsonObject()) {
            Set<String> keys = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                keys.add(entry.getKey());
            }
            return keys;
        }
        return new HashSet<>();
    }

    /**
     * Gets a set of keys at the root level.
     *
     * @return Set of keys
     */
    @NotNull
    public Set<String> getKeys() {
        return getKeys("");
    }
}
