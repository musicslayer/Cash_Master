package com.musicslayer.cashmaster.ledger;

import android.os.Parcel;
import android.os.Parcelable;

import com.musicslayer.cashmaster.data.bridge.DataBridge;

import java.io.IOException;
import java.math.BigDecimal;

public class LineItem implements DataBridge.SerializableToJSON, Parcelable {
    public int year;
    public String month;
    public String name;
    public BigDecimal amount;
    public boolean isIncome;

    @Override
    public void serializeToJSON(DataBridge.Writer o) throws IOException {
        o.beginObject()
            .serialize("!V!", "1", String.class)
            .serialize("year", year, Integer.class)
            .serialize("month", month, String.class)
            .serialize("name", name, String.class)
            .serialize("amount", amount, BigDecimal.class)
            .serialize("isIncome", isIncome, Boolean.class)
            .endObject();
    }

    public static LineItem deserializeFromJSON(DataBridge.Reader o) throws IOException {
        o.beginObject();

        String version = o.deserialize("!V!", String.class);
        LineItem lineItem = new LineItem();

        if("1".equals(version)) {
            int year = o.deserialize("year", Integer.class);
            String month = o.deserialize("month", String.class);
            String name = o.deserialize("name", String.class);
            BigDecimal amount = o.deserialize("amount", BigDecimal.class);
            boolean isIncome = o.deserialize("isIncome", Boolean.class);
            o.endObject();

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

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(year);
        out.writeString(month);
        out.writeString(name);
        out.writeString(DataBridge.serializeValue(amount, BigDecimal.class));
        out.writeString(Boolean.toString(isIncome)); // "writeBoolean" requires newer Android version.
    }

    public static final Parcelable.Creator<LineItem> CREATOR = new Parcelable.Creator<LineItem>() {
        @Override
        public LineItem createFromParcel(Parcel in) {
            int year = in.readInt();
            String month = in.readString();
            String name = in.readString();
            BigDecimal amount = DataBridge.deserializeValue(in.readString(), BigDecimal.class);
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
}
