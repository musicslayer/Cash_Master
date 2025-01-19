package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

import java.math.BigDecimal;

public class AmountEditText extends RedEditText {
    public AmountEditText(Context context) {
        this(context, null);
    }

    public AmountEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Enforce a maximum length to protect algorithms from having to process large values.
        setMaxLength(9);
    }

    // Returns if the value is a proper amount, which is a number with at most 2 decimal places.
    public boolean condition() {
        BigDecimal value = new BigDecimal(this.getTextString());
        return value.scale() <= 2 && value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(BigDecimal.valueOf(999999.99)) <= 0;
    }
}
