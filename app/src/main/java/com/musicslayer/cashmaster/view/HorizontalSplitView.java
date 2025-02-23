package com.musicslayer.cashmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

// This view is made from two LinearLayouts such that they each take up half of the available horizontal width.
public class HorizontalSplitView extends LinearLayout {
    LinearLayout LA;
    LinearLayout LB;

    public HorizontalSplitView(Context context) {
        this(context, null);
    }

    public HorizontalSplitView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        makeLayout();
    }

    public void addViewA(View child) {
        LA.addView(child);
    }

    public void addViewB(View child) {
        LB.addView(child);
    }

    public void makeLayout() {
        this.setOrientation(HORIZONTAL);
        this.setWeightSum(1.0f);

        Context context = getContext();

        LA = new LinearLayout(context);
        LA.setOrientation(VERTICAL);
        LA.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));

        LB = new LinearLayout(context);
        LB.setOrientation(VERTICAL);
        LB.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5f));

        this.addView(LA);
        this.addView(LB);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        this.setLayoutParams(params);
    }
}
