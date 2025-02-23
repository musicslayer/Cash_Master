package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.activity.BaseActivity;
import com.musicslayer.cashmaster.util.PixelUtil;
import com.musicslayer.cashmaster.util.ViewUtil;
import com.musicslayer.cashmaster.util.WindowUtil;

import java.util.ArrayList;

abstract public class BaseDialog extends Dialog {
    final public BaseActivity activity;

    // Tells whether the user deliberately completed this instance.
    public boolean isComplete = false;

    abstract public void createLayout(Bundle savedInstanceState);
    abstract public int getBaseViewID();

    public BaseDialog(Activity activity) {
        super(activity);
        this.activity = (BaseActivity)activity;
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLayout(savedInstanceState);
    }

    @Override
    public void show() {
        super.show();
        adjustDialog();
        addCancelButton();
    }

    public void adjustDialog() {
        ViewGroup v = findViewById(getBaseViewID());
        ViewGroup p = (ViewGroup)v.getParent();

        // Set left and bottom margins
        int marginPixels = PixelUtil.dpToPx(5, this.activity);
        v.setPadding(marginPixels, 0, 0, marginPixels);

        // Stretch to 90% width. This is needed to see any dialog at all.
        int[] dimensions = WindowUtil.getDimensions(this.activity);
        v.setLayoutParams(new FrameLayout.LayoutParams((int)(dimensions[0] * 0.9), FrameLayout.LayoutParams.WRAP_CONTENT));

        // Add a ScrollView
        ScrollView s = new ScrollView(this.activity);
        s.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));

        p.removeView(v);
        s.addView(v);
        p.addView(s);
    }

    public void addCancelButton() {
        Toolbar toolbar = findToolbar();

        ConstraintLayout CL = findViewById(getBaseViewID());
        ConstraintSet CS = new ConstraintSet();

        AppCompatImageButton cancelButton = new AppCompatImageButton(activity);
        int size = activity.getResources().getDimensionPixelSize(R.dimen.icon_size);
        cancelButton.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        cancelButton.setImageResource(R.drawable.baseline_cancel_24);
        cancelButton.setScaleType(ImageView.ScaleType.FIT_XY);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        cancelButton.setId(View.generateViewId());
        CL.addView(cancelButton);

        CS.clone(CL);
        CS.connect(cancelButton.getId(), ConstraintSet.BOTTOM, toolbar.getId(), ConstraintSet.BOTTOM, 0);
        CS.connect(cancelButton.getId(), ConstraintSet.END, toolbar.getId(), ConstraintSet.END, PixelUtil.dpToPx(16, activity));
        CS.connect(cancelButton.getId(), ConstraintSet.TOP, CL.getId(), ConstraintSet.TOP, 0);
        CS.applyTo(CL);
    }

    private Toolbar findToolbar() {
        // The toolbar should be a direct child of the base view.
        ArrayList<View> children = ViewUtil.getDirectChildren(findViewById(getBaseViewID()));
        for(View child : children) {
            if(child instanceof Toolbar) {
                return (Toolbar)child;
            }
        }
        throw new IllegalStateException("Dialog does not have a toolbar.");
    }
}
