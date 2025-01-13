package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

import java.math.BigInteger;
import java.util.ArrayList;

// TODO We shouldn't need this. This can just be a dropdown...

public class MonthEditText extends RedEditText {
    public MonthEditText(Context context) {
        this(context, null);
    }

    public MonthEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Enforce a maximum length to protect algorithms from having to process large values.
        // For now, all of these only need 20 digits.
        setMaxLength(20);
    }

    // Returns if the value is an actual month.
    public boolean condition() {
        ArrayList<String> ALL_MONTHS = new ArrayList<>();
        ALL_MONTHS.add("January");
        ALL_MONTHS.add("February");
        ALL_MONTHS.add("March");
        ALL_MONTHS.add("April");
        ALL_MONTHS.add("May");
        ALL_MONTHS.add("June");
        ALL_MONTHS.add("July");
        ALL_MONTHS.add("August");
        ALL_MONTHS.add("September");
        ALL_MONTHS.add("October");
        ALL_MONTHS.add("November");
        ALL_MONTHS.add("December");

        try {
            String month = this.getTextString();
            return ALL_MONTHS.contains(month);
        }
        catch(Exception ignored) {
            return false;
        }
    }
}
