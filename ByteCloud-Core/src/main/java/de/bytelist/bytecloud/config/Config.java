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
public class Config {

    private static Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    private static final JsonParser PARSER = new JsonParser();

    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    private File file;

    private JsonObject dataCatcher;

    public Config(String name)
    {
        this.name = name;
        this.dataCatcher = new JsonObject();
    }

    public Config(String name, JsonObject source)
    {
        this.name = name;
        this.dataCatcher = source;
    }

    public Config(File file, JsonObject jsonObject)
    {
        this.file = file;
        this.dataCatcher = jsonObject;
    }

    public Config(String key, String value)
    {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Config(String key, Object value)
    {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Config(String key, Number value)
    {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Config(Config defaults)
    {
        this.dataCatcher = defaults.dataCatcher;
    }

    public Config(Config defaults, String name)
    {
        this.dataCatcher = defaults.dataCatcher;
        this.name = name;
    }

    public Config()
    {
        this.dataCatcher = new JsonObject();
    }

    public Config(JsonObject source)
    {
        this.dataCatcher = source;
    }

    public JsonObject obj()
    {
        return dataCatcher;
    }

    public boolean contains(String key)
    {
        return this.dataCatcher.has(key);
    }

    public Config append(String key, String value)
    {
        if (value == null) return this;
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Config append(String key, Number value)
    {
        if (value == null) return this;
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Config append(String key, Boolean value)
    {
        if (value == null) return this;
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Config append(String key, JsonElement value)
    {
        if (value == null) return this;
        this.dataCatcher.add(key, value);
        return this;
    }

    public Config append(String key, Config value)
    {
        if (value == null) return this;
        this.dataCatcher.add(key, value.dataCatcher);
        return this;
    }

    public Config append(String key, Object value)
    {
        if (value == null) return this;
        this.dataCatcher.add(key, GSON.toJsonTree(value));
        return this;
    }

    public Config appendValues(Map<String, Object> values)
    {
        for(Map.Entry<String, Object> valuess : values.entrySet())
        {
            append(valuess.getKey(), valuess.getValue());
        }
        return this;
    }

    public Config remove(String key)
    {
        this.dataCatcher.remove(key);
        return this;
    }

    public Set<String> keys()
    {
        Set<String> c = new HashSet<>();

        for (Map.Entry<String, JsonElement> x : dataCatcher.entrySet())
        {
            c.add(x.getKey());
        }

        return c;
    }

    public JsonElement get(String key)
    {
        if(!dataCatcher.has(key)) return null;
        return dataCatcher.get(key);
    }

    public String getString(String key)
    {
        if (!dataCatcher.has(key)) return null;
        return dataCatcher.get(key).getAsString();
    }

    public int getInt(String key)
    {
        if (!dataCatcher.has(key)) return 0;
        return dataCatcher.get(key).getAsInt();
    }

    public long getLong(String key)
    {
        if (!dataCatcher.has(key)) return 0L;
        return dataCatcher.get(key).getAsLong();
    }

    public double getDouble(String key)
    {
        if (!dataCatcher.has(key)) return 0D;
        return dataCatcher.get(key).getAsDouble();
    }

    public boolean getBoolean(String key)
    {
        if (!dataCatcher.has(key)) return false;
        return dataCatcher.get(key).getAsBoolean();
    }

    public float getFloat(String key)
    {
        if (!dataCatcher.has(key)) return 0F;
        return dataCatcher.get(key).getAsFloat();
    }

    public short getShort(String key)
    {
        if (!dataCatcher.has(key)) return 0;
        return dataCatcher.get(key).getAsShort();
    }

    public <T> T getObject(String key, Class<T> class_)
    {
        if (!dataCatcher.has(key)) return null;
        JsonElement element = dataCatcher.get(key);

        return GSON.fromJson(element, class_);
    }

    public Config getDocument(String key)
    {
        return new Config(dataCatcher.get(key).getAsJsonObject());
    }

    public Config clear()
    {
        for(String key : keys())
        {
            remove(key);
        }
        return this;
    }

    public Config loadProperies(Properties properties)
    {
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements())
        {
            Object x = enumeration.nextElement();
            this.append(x.toString(), properties.getProperty(x.toString()));
        }
        return this;
    }

    public JsonArray getArray(String key)
    {
        return dataCatcher.get(key).getAsJsonArray();
    }

    public String convertToJson()
    {
        return GSON.toJson(dataCatcher);
    }

    public String convertToJsonString()
    {
        return dataCatcher.toString();
    }

    public boolean saveAsConfig(File backend)
    {
        if (backend == null) return false;

        if (backend.exists())
        {
            backend.delete();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), "UTF-8"))
        {
            GSON.toJson(dataCatcher, (writer));
            return true;
        } catch (IOException ex)
        {
            ex.getStackTrace();
        }
        return false;
    }

    public boolean saveAsConfig(Path path)
    {
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), "UTF-8"))
        {
            GSON.toJson(dataCatcher, outputStreamWriter);
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveAsConfig(String path)
    {
        return saveAsConfig(Paths.get(path));
    }

    public static Config loadDocument(File backend)
    {
        return loadDocument(backend.toPath());
    }

    public static Config $loadDocument(File backend) throws Exception
    {
        try
        {
            return new Config(PARSER.parse(new String(Files.readAllBytes(backend.toPath()), StandardCharsets.UTF_8)).getAsJsonObject());
        }catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public static Config loadDocument(Path backend)
    {

        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(backend), "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(reader))
        {
            JsonObject object = PARSER.parse(bufferedReader).getAsJsonObject();
            return new Config(object);
        } catch (Exception ex)
        {
            ex.getStackTrace();
        }
        return new Config();

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

    public Config loadToExistingDocument(File backend)
    {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(backend), "UTF-8"))
        {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            this.file = backend;
            return this;
        } catch (Exception ex)
        {
            ex.getStackTrace();
        }
        return new Config();
    }

    public Config loadToExistingDocument(Path path)
    {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), "UTF-8"))
        {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            return this;
        } catch (Exception ex)
        {
            ex.getStackTrace();
        }
        return new Config();
    }

    public static Config load(String input)
    {
        try (InputStreamReader reader = new InputStreamReader(new StringBufferInputStream(input), "UTF-8"))
        {
            return new Config(PARSER.parse(new BufferedReader(reader)).getAsJsonObject());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Config();
    }

    @Override
    public String toString()
    {
        return convertToJsonString();
    }

    public static Config load(JsonObject input)
    {
        return new Config(input);
    }

    public <T> T getObject(String key, Type type)
    {
        return GSON.fromJson(dataCatcher.get(key), type);
    }

    public byte[] toBytesAsUTF_8()
    {
        return convertToJsonString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toBytes()
    {
        return convertToJsonString().getBytes();
    }
}
