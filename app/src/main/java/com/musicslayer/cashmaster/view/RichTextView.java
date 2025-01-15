package com.musicslayer.cashmaster.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.rich.RichStringBuilder;

// A TextView that supports rich formatting including color.
public class RichTextView extends AppCompatTextView {
    private RichStringBuilder rb = new RichStringBuilder(true);
    private int color;
    private boolean shouldColor;

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void appendText(String text) {
        rb.appendRich(text);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setShouldColor(boolean shouldColor) {
        this.shouldColor = shouldColor;
    }

    public void finishText() {
        String str = rb.toString();
        if(shouldColor) {
            str = "<font color=#ffdd00>" + str + "</font>"; // TODO Use color.
        }
        this.setText(Html.fromHtml(str));
    }
}
