package com.musicslayer.cashmaster.ledger;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.musicslayer.cashmaster.data.bridge.DataBridge;

import java.io.IOException;
import java.math.BigDecimal;

public class LineItem implements Parcelable {
    public int year;
    public String month;
    public String name;
    public BigDecimal amount;
    public boolean isIncome;

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(year);
        out.writeString(month);
        out.writeString(name);
        out.writeString(DataBridge.serializeValue(amount, DataBridge.BigDecimalSerializable.SERIALIZER));
        out.writeString(Boolean.toString(isIncome)); // "writeBoolean" requires newer Android version.
    }

    public static final Parcelable.Creator<LineItem> CREATOR = new Parcelable.Creator<LineItem>() {
        @Override
        public LineItem createFromParcel(Parcel in) {
            int year = in.readInt();
            String month = in.readString();
            String name = in.readString();
            BigDecimal amount = DataBridge.deserializeValue(in.readString(), DataBridge.BigDecimalSerializable.DESERIALIZER);
            boolean isIncome = Boolean.parseBoolean(in.readString());

            LineItem lineItem = new LineItem();
            lineItem.year = year;
            lineItem.month = month;
            lineItem.name = name;
            lineItem.amount = amount;
            lineItem.isIncome = isIncome;

            return lineItem;
        }

        @Override
        public LineItem[] newArray(int size) {
            return new LineItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static final DataBridge.Serializer<LineItem> SERIALIZER = new DataBridge.Serializer<LineItem>() {
        @Override
        public void serialize(DataBridge.Writer writer, @NonNull LineItem obj) throws IOException {
            writer.beginObject();
            writer.serialize("!V!", "1", DataBridge.StringSerializable.SERIALIZER);
            writer.serialize("year", obj.year, DataBridge.IntegerSerializable.SERIALIZER);
            writer.serialize("month", obj.month, DataBridge.StringSerializable.SERIALIZER);
            writer.serialize("name", obj.name, DataBridge.StringSerializable.SERIALIZER);
            writer.serialize("amount", obj.amount, DataBridge.BigDecimalSerializable.SERIALIZER);
            writer.serialize("isIncome", obj.isIncome, DataBridge.BooleanSerializable.SERIALIZER);
            writer.endObject();
        }
    };

    public static final DataBridge.Deserializer<LineItem> DESERIALIZER = new DataBridge.Deserializer<LineItem>() {
        @NonNull
        @Override
        public LineItem deserialize(DataBridge.Reader reader) throws IOException {
            reader.beginObject();

            String version = reader.deserialize("!V!", DataBridge.StringSerializable.DESERIALIZER);
            LineItem lineItem = new LineItem();

            if("1".equals(version)) {
                int year = reader.deserialize("year", DataBridge.IntegerSerializable.DESERIALIZER);
                String month = reader.deserialize("month", DataBridge.StringSerializable.DESERIALIZER);
                String name = reader.deserialize("name", DataBridge.StringSerializable.DESERIALIZER);
                BigDecimal amount = reader.deserialize("amount", DataBridge.BigDecimalSerializable.DESERIALIZER);
                boolean isIncome = reader.deserialize("isIncome", DataBridge.BooleanSerializable.DESERIALIZER);
                reader.endObject();

                lineItem.year = year;
                lineItem.month = month;
                lineItem.name = name;
                lineItem.amount = amount;
                lineItem.isIncome = isIncome;
            }
            else {
                throw new IllegalStateException("version = " + version);
            }

            return lineItem;
        }
    };
}
