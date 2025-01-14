package com.musicslayer.cashmaster.ledger;

import com.musicslayer.cashmaster.data.bridge.DataBridge;

import java.io.IOException;

public class LineItem implements DataBridge.SerializableToJSON {
    public int year;
    public String month;
    public String name;
    public int amount; // Amount is always a positive number.
    public boolean isIncome;

    @Override
    public void serializeToJSON(DataBridge.Writer o) throws IOException {
        o.beginObject()
            .serialize("!V!", "1", String.class)
            .serialize("year", year, Integer.class)
            .serialize("month", month, String.class)
            .serialize("name", name, String.class)
            .serialize("amount", amount, Integer.class)
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
            int amount = o.deserialize("amount", Integer.class);
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
}
