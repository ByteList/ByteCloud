package de.bytelist.bytecloud.config;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ByteList on 04.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudConfig {

    private static Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    private static final JsonParser PARSER = new JsonParser();

    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    private File file;

    private JsonObject dataCatcher;

    public CloudConfig() {
        this.dataCatcher = new JsonObject();
    }

    public CloudConfig(JsonObject source) {
        this.dataCatcher = source;
    }

    public JsonObject obj() {
        return dataCatcher;
    }

    public boolean contains(String key) {
        return this.dataCatcher.has(key);
    }

    public CloudConfig append(String key, String value) {
        if (value == null) return this;
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public CloudConfig append(String key, Number value) {
        if (value == null) return this;
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public CloudConfig append(String key, Boolean value) {
        if (value == null) return this;
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public CloudConfig append(String key, JsonElement value) {
        if (value == null) return this;
        this.dataCatcher.add(key, value);
        return this;
    }

    public CloudConfig append(String key, CloudConfig value) {
        if (value == null) return this;
        this.dataCatcher.add(key, value.dataCatcher);
        return this;
    }

    public CloudConfig append(String key, Object value) {
        if (value == null) return this;
        this.dataCatcher.add(key, GSON.toJsonTree(value));
        return this;
    }

    public CloudConfig appendValues(Map<String, Object> values) {
        for (Map.Entry<String, Object> valuess : values.entrySet()) {
            append(valuess.getKey(), valuess.getValue());
        }
        return this;
    }

    public CloudConfig remove(String key) {
        this.dataCatcher.remove(key);
        return this;
    }

    public Set<String> keys() {
        Set<String> c = new HashSet<>();

        for (Map.Entry<String, JsonElement> x : dataCatcher.entrySet()) {
            c.add(x.getKey());
        }

        return c;
    }

    public JsonElement get(String key) {
        if (!dataCatcher.has(key)) return null;
        return dataCatcher.get(key);
    }

    public String getString(String key) {
        if (!dataCatcher.has(key)) return null;
        return dataCatcher.get(key).getAsString();
    }

    public int getInt(String key) {
        if (!dataCatcher.has(key)) return 0;
        return dataCatcher.get(key).getAsInt();
    }

    public long getLong(String key) {
        if (!dataCatcher.has(key)) return 0L;
        return dataCatcher.get(key).getAsLong();
    }

    public double getDouble(String key) {
        if (!dataCatcher.has(key)) return 0D;
        return dataCatcher.get(key).getAsDouble();
    }

    public boolean getBoolean(String key) {
        if (!dataCatcher.has(key)) return false;
        return dataCatcher.get(key).getAsBoolean();
    }

    public float getFloat(String key) {
        if (!dataCatcher.has(key)) return 0F;
        return dataCatcher.get(key).getAsFloat();
    }

    public short getShort(String key) {
        if (!dataCatcher.has(key)) return 0;
        return dataCatcher.get(key).getAsShort();
    }

    public <T> T getObject(String key, Class<T> class_) {
        if (!dataCatcher.has(key)) return null;
        JsonElement element = dataCatcher.get(key);

        return GSON.fromJson(element, class_);
    }

    public CloudConfig getDocument(String key) {
        return new CloudConfig(dataCatcher.get(key).getAsJsonObject());
    }

    public CloudConfig clear() {
        for (String key : keys()) {
            remove(key);
        }
        return this;
    }

    public CloudConfig loadProperies(Properties properties) {
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            Object x = enumeration.nextElement();
            this.append(x.toString(), properties.getProperty(x.toString()));
        }
        return this;
    }

    public JsonArray getArray(String key) {
        return dataCatcher.get(key).getAsJsonArray();
    }

    public String convertToJson() {
        return GSON.toJson(dataCatcher);
    }

    public String convertToJsonString() {
        return dataCatcher.toString();
    }

    public boolean saveAsConfig(File backend) {
        if (backend == null) return false;

        if (backend.exists()) {
            backend.delete();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), "UTF-8")) {
            GSON.toJson(dataCatcher, (writer));
            return true;
        } catch (IOException ex) {
            ex.getStackTrace();
        }
        return false;
    }

    public boolean saveAsConfig(Path path) {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), "UTF-8")) {
            GSON.toJson(dataCatcher, outputStreamWriter);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveAsConfig(String path) {
        return saveAsConfig(Paths.get(path));
    }

    public static CloudConfig loadDocument(File backend) {
        return loadDocument(backend.toPath());
    }

    public static CloudConfig $loadDocument(File backend) throws Exception {
        try {
            return new CloudConfig(PARSER.parse(new String(Files.readAllBytes(backend.toPath()), StandardCharsets.UTF_8)).getAsJsonObject());
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public static CloudConfig loadDocument(Path backend) {

        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(backend), "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            JsonObject object = PARSER.parse(bufferedReader).getAsJsonObject();
            return new CloudConfig(object);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return new CloudConfig();

        /*
        try
        {
            return new Document(PARSER.parse(new String(Files.readAllBytes(backend), StandardCharsets.UTF_8)).getAsJsonObject());
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Document();
        */
    }

    public CloudConfig loadToExistingDocument(File backend) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(backend), "UTF-8")) {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            this.file = backend;
            return this;
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return new CloudConfig();
    }

    public CloudConfig loadToExistingDocument(Path path) {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), "UTF-8")) {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            return this;
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return new CloudConfig();
    }

    public static CloudConfig load(String input) {
        try (InputStreamReader reader = new InputStreamReader(new StringBufferInputStream(input), "UTF-8")) {
            return new CloudConfig(PARSER.parse(new BufferedReader(reader)).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CloudConfig();
    }

    @Override
    public String toString() {
        return convertToJsonString();
    }

    public static CloudConfig load(JsonObject input) {
        return new CloudConfig(input);
    }

    public <T> T getObject(String key, Type type) {
        return GSON.fromJson(dataCatcher.get(key), type);
    }

    public byte[] toBytesAsUTF_8() {
        return convertToJsonString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toBytes() {
        return convertToJsonString().getBytes();
    }
}
