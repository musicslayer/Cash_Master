package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

import java.math.BigInteger;

public class BooleanEditText extends RedEditText {
    public BooleanEditText(Context context) {
        this(context, null);
    }

    public BooleanEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Enforce a maximum length to protect algorithms from having to process large values.
        // For now, all of these only need 6 digits.
        setMaxLength(6);
    }

    // Returns if the value is either "true" or "false".
    public boolean condition() {
        try {
            String value = this.getTextString();
            return "true".equals(value) || "false".equals(value);
        }
        catch(Exception ignored) {
            return false;
        }
    }
}
