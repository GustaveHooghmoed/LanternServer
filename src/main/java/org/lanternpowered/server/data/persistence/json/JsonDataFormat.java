/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.persistence.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.lanternpowered.server.data.persistence.AbstractDataFormat;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class JsonDataFormat extends AbstractDataFormat {

    public JsonDataFormat(String identifier) {
        super(identifier);
    }

    @Override
    public DataContainer readFrom(InputStream input) throws IOException {
        try (JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)))) {
            return readFrom(reader);
        }
    }

    public static DataContainer serialize(Gson gson, Object o) throws IOException {
        final DataViewJsonWriter writer = new DataViewJsonWriter();
        gson.toJson(o, o.getClass(), writer);
        return writer.getResult();
    }

    public DataContainer read(String input) throws IOException {
        try (JsonReader reader = new JsonReader(new StringReader(input))) {
            return readFrom(reader);
        }
    }

    public DataContainer readFrom(JsonReader reader) throws IOException {
        return createContainer(reader);
    }

    private DataContainer createContainer(JsonReader reader) throws IOException {
        final DataContainer container = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        readView(reader, container);
        return container;
    }

    private void readView(JsonReader reader, DataView view) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            DataQuery key = DataQuery.of(reader.nextName());

            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                // Check this early so we don't need to copy the view
                readView(reader, view.createView(key));
            } else {
                view.set(key, read(reader));
            }
        }
        reader.endObject();
    }

    @Nullable
    private Object read(JsonReader reader) throws IOException {
        final JsonToken token = reader.peek();
        switch (token) {
            case BEGIN_OBJECT:
                return createContainer(reader);
            case BEGIN_ARRAY:
                return readArray(reader);
            case BOOLEAN:
                return reader.nextBoolean();
            case NULL:
                reader.nextNull();
                return null;
            case STRING:
                return reader.nextString();
            case NUMBER:
                return readNumber(reader);
            default:
                throw new IOException("Unexpected token: " + token);
        }
    }

    private static Number readNumber(JsonReader reader) throws IOException {
        // Similar to https://github.com/zml2008/configurate/blob/master/configurate-gson/src/main/java/ninja/leaping/configurate/gson/GsonConfigurationLoader.java#L113
        // Not sure what's the best way to detect the type of number
        double nextDouble = reader.nextDouble();
        int nextInt = (int) nextDouble;
        if (nextInt == nextDouble) {
            return nextInt;
        }

        long nextLong = (long) nextDouble;
        if (nextLong == nextDouble) {
            return nextLong;
        }

        return nextDouble;
    }

    private List<?> readArray(JsonReader reader) throws IOException {
        reader.beginArray();
        final List<Object> result = new ArrayList<>();
        while (reader.hasNext()) {
            result.add(read(reader));
        }
        reader.endArray();
        return result;
    }

    @Override
    public void writeTo(OutputStream output, DataView data) throws IOException {
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8)))) {
            writeView(writer, data);
        }
    }

    public String write(DataView data) throws IOException {
        final StringWriter writer = new StringWriter();
        try (JsonWriter jsonWriter = new JsonWriter(writer)) {
            writeView(jsonWriter, data);
        }
        return writer.toString();
    }

    private void writeView(JsonWriter writer, DataView view) throws IOException {
        writer.beginObject();
        for (Map.Entry<DataQuery, Object> entry : view.getValues(false).entrySet()) {
            writer.name(entry.getKey().asString('.'));
            write(writer, entry.getValue());
        }
        writer.endObject();
    }

    private void write(JsonWriter writer, @Nullable Object value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value instanceof Boolean) {
            writer.value((Boolean) value);
        } else if (value instanceof Number) {
            writer.value((Number) value);
        } else if (value instanceof String) {
            writer.value((String) value);
        } else if (value instanceof Iterable) {
            writeArray(writer, (Iterable<?>) value);
        } else if (value instanceof Map) {
            writeMap(writer, (Map<?, ?>) value);
        } else if (value instanceof DataSerializable) {
            writeView(writer, ((DataSerializable) value).toContainer());
        } else if (value instanceof DataView) {
            writeView(writer, (DataView) value);
        } else {
            throw new IllegalArgumentException("Unable to translate object to JSON: " + value);
        }
    }

    private void writeArray(JsonWriter writer, Iterable<?> iterable) throws IOException {
        writer.beginArray();
        for (Object value : iterable) {
            write(writer, value);
        }
        writer.endArray();
    }

    private void writeMap(JsonWriter writer, Map<?, ?> map) throws IOException {
        writer.beginObject();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof DataQuery) {
                key = ((DataQuery) key).asString('.');
            }
            writer.name(key.toString());
            write(writer, entry.getValue());
        }
        writer.endObject();
    }
}
