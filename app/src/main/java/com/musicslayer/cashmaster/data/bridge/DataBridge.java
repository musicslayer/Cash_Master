package com.musicslayer.cashmaster.data.bridge;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import androidx.annotation.NonNull;

import com.musicslayer.cashmaster.util.ThrowableUtil;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBridge {
    public interface Serializer<T> {
        void serialize(Writer writer, @NonNull T obj) throws IOException;
    }

    public interface Deserializer<T> {
        @NonNull
        T deserialize(Reader reader) throws IOException;
    }

    public static void safeFlushAndClose(Writer writer) {
        try {
            if(writer != null) {
                writer.jsonWriter.flush();
                writer.jsonWriter.close();
            }
        }
        catch(Exception ignored) {
        }
    }

    public static void safeClose(Reader reader) {
        try {
            if(reader != null) {
                reader.jsonReader.close();
            }
        }
        catch(Exception ignored) {
        }
    }

    public static <T> String serializeValue(T obj, Serializer<T> serializerT) {
        // JSON doesn't allow direct Strings as top-level values, so directly handle value ourselves.
        if(obj == null) { return null; }

        Writer writer = new Writer();
        Reader reader = null;

        try {
            writer.beginArray();
            writer.serialize(null, obj, serializerT);
            writer.endArray();
            safeFlushAndClose(writer);

            reader = new Reader(writer.stringWriter.toString());
            reader.beginArray();
            String s = reader.getString();
            reader.endArray();
            safeClose(reader);

            return s;
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static <T> String serialize(T obj, Serializer<T> serializerT) {
        if(obj == null) { return null; }

        Writer writer = new Writer();

        try {
            writer.serialize(null, obj, serializerT);
            safeFlushAndClose(writer);
            return writer.stringWriter.toString();
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            throw new IllegalStateException(e);
        }
    }

    public static <T> String serializeArrayList(ArrayList<T> arrayList, Serializer<T> serializerT) {
        if(arrayList == null) { return null; }

        Writer writer = new Writer();

        try {
            writer.serializeArrayList(null, arrayList, serializerT);
            safeFlushAndClose(writer);
            return writer.stringWriter.toString();
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            throw new IllegalStateException(e);
        }
    }

    public static <T, U> String serializeHashMap(HashMap<T, U> hashMap, Serializer<T> serializerT, Serializer<U> serializerU) {
        if(hashMap == null) { return null; }

        Writer writer = new Writer();

        try {
            writer.serializeHashMap(null, hashMap, serializerT, serializerU);
            safeFlushAndClose(writer);
            return writer.stringWriter.toString();
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            throw new IllegalStateException(e);
        }
    }

    public static <T> T deserializeValue(String s, Deserializer<T> deserializerT) {
        // JSON doesn't allow direct Strings as top-level values, so directly handle value ourselves.
        if(s == null) { return null; }

        Writer writer = new Writer();
        Reader reader = null;

        try {
            writer.beginArray();
            writer.putString(s);
            writer.endArray();
            safeFlushAndClose(writer);

            reader = new Reader(writer.stringWriter.toString());
            reader.beginArray();
            T obj = reader.deserialize(null, deserializerT);
            reader.endArray();
            safeClose(reader);

            return obj;
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static <T> T deserialize(String s, Deserializer<T> deserializerT) {
        if(s == null) { return null; }

        Reader reader = new Reader(s);
        try {
            T obj = reader.deserialize(null, deserializerT);
            safeClose(reader);
            return obj;
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static <T> ArrayList<T> deserializeArrayList(String s, Deserializer<T> deserializerT) {
        if(s == null) { return null; }

        Reader reader = new Reader(s);
        try {
            ArrayList<T> arrayList = reader.deserializeArrayList(null, deserializerT);
            safeClose(reader);
            return arrayList;
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static <T, U> HashMap<T, U> deserializeHashMap(String s, Deserializer<T> deserializerT, Deserializer<U> deserializerU) {
        if(s == null) { return null; }

        Reader reader = new Reader(s);
        try {
            HashMap<T, U> hashMap = reader.deserializeHashMap(null, deserializerT, deserializerU);
            safeClose(reader);
            return hashMap;
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static class Writer {
        public JsonWriter jsonWriter;
        public StringWriter stringWriter;

        public Writer() {
            stringWriter = new StringWriter();
            jsonWriter = new JsonWriter(stringWriter);
        }

        public Writer putName(String s) throws IOException {
            jsonWriter.name(s);
            return this;
        }

        public Writer putString(String s) throws IOException {
            jsonWriter.value(s);
            return this;
        }

        public Writer putNull() throws IOException {
            jsonWriter.nullValue();
            return this;
        }

        public Writer beginObject() throws IOException {
            jsonWriter.beginObject();
            return this;
        }

        public Writer endObject() throws IOException {
            jsonWriter.endObject();
            return this;
        }

        public Writer beginArray() throws IOException {
            jsonWriter.beginArray();
            return this;
        }

        public Writer endArray() throws IOException {
            jsonWriter.endArray();
            return this;
        }

        public <T> Writer serialize(String key, T obj, Serializer<T> serializerT) throws IOException {
            if(key != null) {
                putName(key);
            }

            if(obj == null) {
                putNull();
            }
            else {
                serializerT.serialize(this, obj);
            }

            return this;
        }

        public <T> Writer serializeArrayList(String key, ArrayList<T> arrayList, Serializer<T> serializerT) throws IOException {
            if(key != null) {
                putName(key);
            }

            if(arrayList == null) {
                putNull();
            }
            else {
                jsonWriter.beginArray();
                for(T t : arrayList) {
                    serialize(null, t, serializerT);
                }
                jsonWriter.endArray();
            }

            return this;
        }

        public <T, U> Writer serializeHashMap(String key, HashMap<T, U> hashMap, Serializer<T> serializerT, Serializer<U> serializerU) throws IOException {
            if(key != null) {
                putName(key);
            }

            if(hashMap == null) {
                putNull();
            }
            else {
                ArrayList<T> keyArrayList = new ArrayList<>(hashMap.keySet());
                ArrayList<U> valueArrayList = new ArrayList<>();
                for(T keyT : keyArrayList) {
                    valueArrayList.add(hashMap.get(keyT));
                }

                jsonWriter.beginObject();
                serializeArrayList("keys", keyArrayList, serializerT);
                serializeArrayList("values", valueArrayList, serializerU);
                jsonWriter.endObject();
            }

            return this;
        }
    }

    public static class Reader {
        public JsonReader jsonReader;
        public StringReader stringReader;

        public Reader(String s) {
            stringReader = new StringReader(s);
            jsonReader = new JsonReader(stringReader);
        }

        public String getName() throws IOException {
            return jsonReader.nextName();
        }

        public String getString() throws IOException {
            return jsonReader.nextString();
        }

        public <T> T getNull() throws IOException {
            jsonReader.nextNull();
            return null;
        }

        public Reader beginObject() throws IOException {
            jsonReader.beginObject();
            return this;
        }

        public Reader endObject() throws IOException {
            jsonReader.endObject();
            return this;
        }

        public Reader beginArray() throws IOException {
            jsonReader.beginArray();
            return this;
        }

        public Reader endArray() throws IOException {
            jsonReader.endArray();
            return this;
        }

        public <T> T deserialize(String key, Deserializer<T> deserializerT) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                return getNull();
            }
            else {
                return deserializerT.deserialize(this);
            }
        }

        public <T> ArrayList<T> deserializeArrayList(String key, Deserializer<T> deserializerT) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                return getNull();
            }
            else {
                ArrayList<T> arrayList = new ArrayList<>();

                jsonReader.beginArray();
                while(jsonReader.hasNext()) {
                    arrayList.add(deserialize(null, deserializerT));
                }
                jsonReader.endArray();

                return arrayList;
            }
        }

        public <T, U> HashMap<T, U> deserializeHashMap(String key, Deserializer<T> deserializerT, Deserializer<U> deserializerU) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                return getNull();
            }
            else {
                jsonReader.beginObject();
                ArrayList<T> arrayListT = deserializeArrayList("keys", deserializerT);
                ArrayList<U> arrayListU = deserializeArrayList("values", deserializerU);
                jsonReader.endObject();

                if(arrayListT == null || arrayListU == null || arrayListT.size() != arrayListU.size()) {
                    return null;
                }

                HashMap<T, U> hashMap = new HashMap<>();
                for(int i = 0; i < arrayListT.size(); i++) {
                    hashMap.put(arrayListT.get(i), arrayListU.get(i));
                }

                return hashMap;
            }
        }
    }

    public static class StringSerializable {
        public static final DataBridge.Serializer<String> SERIALIZER = new DataBridge.Serializer<String>() {
            @Override
            public void serialize(DataBridge.Writer writer, @NonNull String obj) throws IOException {
                writer.putString(obj);
            }
        };

        public static final DataBridge.Deserializer<String> DESERIALIZER = new DataBridge.Deserializer<String>() {
            @NonNull
            @Override
            public String deserialize(DataBridge.Reader reader) throws IOException {
                return reader.getString();
            }
        };
    }

    public static class IntegerSerializable {
        public static final DataBridge.Serializer<Integer> SERIALIZER = new DataBridge.Serializer<Integer>() {
            @Override
            public void serialize(DataBridge.Writer writer, @NonNull Integer obj) throws IOException {
                writer.putString(Integer.toString(obj));
            }
        };

        public static final DataBridge.Deserializer<Integer> DESERIALIZER = new DataBridge.Deserializer<Integer>() {
            @NonNull
            @Override
            public Integer deserialize(DataBridge.Reader reader) throws IOException {
                return Integer.parseInt(reader.getString());
            }
        };
    }

    public static class BooleanSerializable {
        public static final DataBridge.Serializer<Boolean> SERIALIZER = new DataBridge.Serializer<Boolean>() {
            @Override
            public void serialize(DataBridge.Writer writer, @NonNull Boolean obj) throws IOException {
                writer.putString(Boolean.toString(obj));
            }
        };

        public static final DataBridge.Deserializer<Boolean> DESERIALIZER = new DataBridge.Deserializer<Boolean>() {
            @NonNull
            @Override
            public Boolean deserialize(DataBridge.Reader reader) throws IOException {
                return Boolean.parseBoolean(reader.getString());
            }
        };
    }

    public static class BigDecimalSerializable {
        public static final DataBridge.Serializer<BigDecimal> SERIALIZER = new DataBridge.Serializer<BigDecimal>() {
            @Override
            public void serialize(DataBridge.Writer writer, @NonNull BigDecimal obj) throws IOException {
                writer.putString(obj.toString());
            }
        };

        public static final DataBridge.Deserializer<BigDecimal> DESERIALIZER = new DataBridge.Deserializer<BigDecimal>() {
            @NonNull
            @Override
            public BigDecimal deserialize(DataBridge.Reader reader) throws IOException {
                return new BigDecimal(reader.getString());
            }
        };
    }
}
