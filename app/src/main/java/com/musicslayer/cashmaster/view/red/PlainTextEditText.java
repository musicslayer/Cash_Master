package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.util.AttributeSet;

public class PlainTextEditText extends RedEditText {
    public PlainTextEditText(Context context) {
        this(context, null);
    }

    public PlainTextEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Enforce a maximum length to protect algorithms from having to process large values.
        // Give this text 100 characters.
        setMaxLength(100);
    }

    // Returns if the value is nonempty text.
    public boolean condition() {
        return !this.getTextString().isEmpty();
    }
}
