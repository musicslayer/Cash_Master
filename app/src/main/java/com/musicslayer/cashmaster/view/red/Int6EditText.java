package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

import java.math.BigInteger;

public class Int6EditText extends RedEditText {
    public Int6EditText(Context context) {
        this(context, null);
    }

    public Int6EditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Enforce a maximum length to protect algorithms from having to process large values.
        // For now, all of these only need 6 digits.
        setMaxLength(6);
    }

    // Returns if the value is an integer with up to 6 digits.
    public boolean condition() {
        try {
            BigInteger value = new BigInteger(this.getTextString());
            return value.compareTo(BigInteger.ZERO) >= 0 && value.compareTo(BigInteger.valueOf(999999)) <= 0;
        }
        catch(Exception ignored) {
            return false;
        }
    }
}
