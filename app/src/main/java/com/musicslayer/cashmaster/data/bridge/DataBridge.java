package com.musicslayer.cashmaster.data.bridge;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import androidx.annotation.Keep;

import com.musicslayer.cashmaster.util.ReflectUtil;
import com.musicslayer.cashmaster.util.ThrowableUtil;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBridge {
    public interface SerializableToJSON {
        void serializeToJSON(Writer o) throws IOException;
        // Classes also need to implement static method "deserializeFromJSON".
    }

    public interface ExportableToJSON {
        void exportDataToJSON(DataBridge.Writer o) throws IOException;
        void importDataFromJSON(DataBridge.Reader o) throws IOException;
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

    public static <T> String serializeValue(T obj, Class<T> clazzT) {
        // JSON doesn't allow direct Strings as top-level values, so directly handle value ourselves.
        if(obj == null) { return null; }

        Writer writer = new Writer();
        Reader reader = null;

        try {
            writer.beginArray();
            writer.serialize(null, obj, clazzT);
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

    public static <T> String serialize(T obj, Class<T> clazzT) {
        if(obj == null) { return null; }

        Writer writer = new Writer();

        try {
            writer.serialize(null, obj, clazzT);
            safeFlushAndClose(writer);
            return writer.stringWriter.toString();
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            throw new IllegalStateException(e);
        }
    }

    public static <T> String exportData(T obj, Class<T> clazzT) {
        if(obj == null) { return null; }

        Writer writer = new Writer();

        try {
            writer.exportData(null, obj, clazzT);
            safeFlushAndClose(writer);
            return writer.stringWriter.toString();
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeFlushAndClose(writer);
            throw new IllegalStateException(e);
        }
    }

    public static <T> T deserializeValue(String s, Class<T> clazzT) {
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
            T obj = reader.deserialize(null, clazzT);
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

    public static <T> T deserialize(String s, Class<T> clazzT) {
        if(s == null) { return null; }

        Reader reader = new Reader(s);
        try {
            T obj = reader.deserialize(null, clazzT);
            safeClose(reader);
            return obj;
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static <T> void importData(T obj, String s, Class<T> clazzT) {
        if(s == null) { return; }

        Reader reader = new Reader(s);
        try {
            reader.importData(null, obj, clazzT);
            safeClose(reader);
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            safeClose(reader);
            throw new IllegalStateException(e);
        }
    }

    public static <T> String cycleSerialization(String s, Class<T> clazzT) {
        // Do a round trip of deserializing and serializing to make sure the string represents an object of the class.
        T obj = deserialize(s, clazzT);
        return serialize(obj, clazzT);
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

        public <T> Writer serialize(String key, T obj, Class<T> clazzT) throws IOException {
            if(key != null) {
                putName(key);
            }

            if(obj == null) {
                putNull();
            }
            else {
                wrapSerializableObj(obj).serializeToJSON(this);
            }

            return this;
        }

        public <T> Writer serializeArrayList(String key, ArrayList<T> arrayList, Class<T> clazzT) throws IOException {
            if(key != null) {
                putName(key);
            }

            if(arrayList == null) {
                putNull();
            }
            else {
                jsonWriter.beginArray();
                for(T t : arrayList) {
                    serialize(null, t, clazzT);
                }
                jsonWriter.endArray();
            }

            return this;
        }

        public <T, U> Writer serializeHashMap(String key, HashMap<T, U> hashMap, Class<T> clazzT, Class<U> clazzU) throws IOException {
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
                serializeArrayList("keys", keyArrayList, clazzT);
                serializeArrayList("values", valueArrayList, clazzU);
                jsonWriter.endObject();
            }

            return this;
        }

        public <T> Writer exportData(String key, T obj, Class<T> clazzT) throws IOException {
            if(key != null) {
                putName(key);
            }

            if(obj == null) {
                putNull();
            }
            else {
                wrapExportableObj(obj).exportDataToJSON(this);
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

        public <T> T deserialize(String key, Class<T> clazzT) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("class = " + clazzT.getSimpleName() + " key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                return getNull();
            }
            else {
                Class<? extends SerializableToJSON> wrappedClass = wrapSerializableClass(clazzT);
                return ReflectUtil.callStaticMethod(wrappedClass, "deserializeFromJSON", this);
            }
        }

        public <T> ArrayList<T> deserializeArrayList(String key, Class<T> clazzT) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("class = " + clazzT.getSimpleName() + " key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                return getNull();
            }
            else {
                ArrayList<T> arrayList = new ArrayList<>();

                jsonReader.beginArray();
                while(jsonReader.hasNext()) {
                    arrayList.add(deserialize(null, clazzT));
                }
                jsonReader.endArray();

                return arrayList;
            }
        }

        public <T, U> HashMap<T, U> deserializeHashMap(String key, Class<T> clazzT, Class<U> clazzU) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("class = " + clazzT.getSimpleName() + " key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                return getNull();
            }
            else {
                jsonReader.beginObject();
                ArrayList<T> arrayListT = deserializeArrayList("keys", clazzT);
                ArrayList<U> arrayListU = deserializeArrayList("values", clazzU);
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

        public <T> void importData(String key, T obj, Class<T> clazzT) throws IOException {
            if(key != null) {
                String nextKey = getName();
                if(!key.equals(nextKey)) {
                    // Expected key was not found.
                    throw new IllegalStateException("class = " + clazzT.getSimpleName() + " key = " + key + " nextKey = " + nextKey);
                }
            }

            if(jsonReader.peek() == JsonToken.NULL) {
                getNull();
            }
            else {
                wrapExportableObj(obj).importDataFromJSON(this);
            }
        }
    }

    public static SerializableToJSON wrapSerializableObj(Object obj) {
        // Converts an arbitrary object into a SerializableToJSON subclass.
        // Note that obj will always be non-null.
        if(obj instanceof SerializableToJSON) {
            return (SerializableToJSON)obj;
        }
        else if(obj instanceof String) {
            return new StringSerializableToJSON((String)obj);
        }
        else if(obj instanceof Integer) {
            return new IntegerSerializableToJSON((Integer)obj);
        }
        else if(obj instanceof Boolean) {
            return new BooleanSerializableToJSON((Boolean)obj);
        }
        else if(obj instanceof BigDecimal) {
            return new BigDecimalSerializableToJSON((BigDecimal)obj);
        }
        else {
            // Anything else is unsupported.
            throw new IllegalStateException("class = " + obj.getClass().getSimpleName() + " obj = " + obj);
        }
    }

    public static ExportableToJSON wrapExportableObj(Object obj) {
        // Converts an arbitrary object into a ExportableToJSON subclass.
        // Note that obj will always be non-null.
        if(obj instanceof ExportableToJSON) {
            return (ExportableToJSON)obj;
        }
        else {
            // Anything else is unsupported.
            throw new IllegalStateException("class = " + obj.getClass().getSimpleName() + " obj = " + obj);
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends SerializableToJSON> wrapSerializableClass(Class<?> clazz) {
        // Converts an arbitrary class into a SerializableToJSON class.
        if(SerializableToJSON.class.isAssignableFrom(clazz)) {
            return (Class<? extends SerializableToJSON>)clazz;
        }
        else if(String.class.isAssignableFrom(clazz)) {
            return StringSerializableToJSON.class;
        }
        else if(Integer.class.isAssignableFrom(clazz)) {
            return IntegerSerializableToJSON.class;
        }
        else if(Boolean.class.isAssignableFrom(clazz)) {
            return BooleanSerializableToJSON.class;
        }
        else if(BigDecimal.class.isAssignableFrom(clazz)) {
            return BigDecimalSerializableToJSON.class;
        }
        else {
            // Anything else is unsupported.
            throw new IllegalStateException("class = " + clazz.getSimpleName());
        }
    }

    private static class StringSerializableToJSON implements SerializableToJSON {
        String obj;
        private StringSerializableToJSON(String obj) {
            this.obj = obj;
        }

        @Override
        public void serializeToJSON(Writer o) throws IOException {
            o.putString(obj);
        }

        @Keep
        public static String deserializeFromJSON(Reader o) throws IOException {
            return o.getString();
        }
    }

    private static class IntegerSerializableToJSON implements SerializableToJSON {
        int obj;
        private IntegerSerializableToJSON(int obj) {
            this.obj = obj;
        }

        @Override
        public void serializeToJSON(DataBridge.Writer o) throws IOException {
            o.putString(Integer.toString(obj));
        }

        @Keep
        public static int deserializeFromJSON(DataBridge.Reader o) throws IOException {
            return Integer.parseInt(o.getString());
        }
    }

    private static class BooleanSerializableToJSON implements SerializableToJSON {
        boolean obj;
        private BooleanSerializableToJSON(boolean obj) {
            this.obj = obj;
        }

        @Override
        public void serializeToJSON(Writer o) throws IOException {
            o.putString(Boolean.toString(obj));
        }

        @Keep
        public static boolean deserializeFromJSON(Reader o) throws IOException {
            return Boolean.parseBoolean(o.getString());
        }
    }

    private static class BigDecimalSerializableToJSON implements SerializableToJSON {
        BigDecimal obj;
        private BigDecimalSerializableToJSON(BigDecimal obj) {
            this.obj = obj;
        }

        @Override
        public void serializeToJSON(DataBridge.Writer o) throws IOException {
            o.putString(obj.toString());
        }

        @Keep
        public static BigDecimal deserializeFromJSON(DataBridge.Reader o) throws IOException {
            return new BigDecimal(o.getString());
        }
    }
}
