package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

import java.math.BigInteger;

public class YearEditText extends RedEditText {
    public YearEditText(Context context) {
        this(context, null);
    }

    public YearEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Use 4 digits for years.
        setMaxLength(4);
    }

    // Returns if the value is an integer with up to 4 digits.
    public boolean condition() {
        try {
            BigInteger value = new BigInteger(this.getTextString());
            return value.compareTo(BigInteger.ZERO) >= 0 && value.compareTo(BigInteger.valueOf(9999)) <= 0;
        }
        catch(Exception ignored) {
            return false;
        }
    }
}
