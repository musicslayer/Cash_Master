package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

import com.musicslayer.cashmaster.ledger.Month;

import java.math.BigInteger;
import java.util.ArrayList;

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
        try {
            String month = this.getTextString();
            return Month.ALL_MONTHS.contains(month);
        }
        catch(Exception ignored) {
            return false;
        }
    }
}
