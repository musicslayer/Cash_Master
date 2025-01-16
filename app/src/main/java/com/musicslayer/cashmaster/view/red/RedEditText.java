package com.musicslayer.cashmaster.view.red;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;

import com.musicslayer.cashmaster.R;

// An EditText that can turn red if a condition is not met.
abstract public class RedEditText extends AppCompatEditText {
    boolean is_red = false;

    public RedEditText(Context context) {
        this(context, null);
    }

    public RedEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.getBackground().mutate();
        test();
    }

    // Use these wrappers to work around warnings regarding getText() potentially returning null.
    public String getTextString() {
        Editable E = getText();
        assert E != null;
        return E.toString();
    }

    public void setTextString(String s) {
        this.setText(s);
        this.test();
    }

    public void setMaxLength(int maxLength) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxLength);
        this.setFilters(filterArray);
    }

    // Returns if the value satisfies the condition, and will highlight itself in red if it does not.
    public boolean test() {
        Context context = getContext();

        boolean isValid;

        try {
            isValid = condition();
        }
        catch(Exception ignored) {
            isValid = false;
        }

        if(isValid) {
            getBackground().clearColorFilter();
            is_red = false;
        }
        else {
            int color = context.getColor(R.color.red);
            getBackground().setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP));
            is_red = true;
        }

        return isValid;
    }

    abstract public boolean condition();

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable state = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", state);
        bundle.putBoolean("is_red", is_red);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("superState");

            is_red = bundle.getBoolean("is_red");
            if(is_red) {
                int color = getContext().getColor(R.color.red);
                getBackground().setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP));
            }
            else {
                getBackground().clearColorFilter();
            }
        }

        super.onRestoreInstanceState(state);
    }
}
