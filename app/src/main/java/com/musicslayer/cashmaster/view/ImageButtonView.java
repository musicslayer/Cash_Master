package com.musicslayer.cashmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

// This View combines an ImageButton and a TextView
public class ImageButtonView extends LinearLayout {
    public ImageButton imageButton;
    public TextView textView;

    public ImageButtonView(Context context) {
        this(context, null);
    }

    public ImageButtonView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        makeLayout();
    }

    public void makeLayout() {
        Context context = getContext();

        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        imageButton = new ImageButton(context);
        imageButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        imageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.addView(imageButton);

        textView = new TextView(context);
        textView.setPadding(30, 0, 0, 0);
        this.addView(textView);
    }

    public void setImageResource(int resID) {
        imageButton.setImageResource(resID);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        imageButton.setOnClickListener(onClickListener);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setTextSize(float size) {
        textView.setTextSize(size);
    }

    public void setTextString(String text) {
        textView.setText(text);
    }
}
