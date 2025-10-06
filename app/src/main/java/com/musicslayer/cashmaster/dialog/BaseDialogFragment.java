package com.musicslayer.cashmaster.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.util.PixelUtil;

public abstract class BaseDialogFragment extends DialogFragment {
    abstract public Toolbar getToolbar();

    public boolean isComplete = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();

        adjustDialog();
        wrapInScrollView();
        addCancelButton();
    }

    private void wrapInScrollView() {
        View dialogRootView = getView();
        if (dialogRootView == null) {
            return;
        }

        ViewGroup parent = (ViewGroup) dialogRootView.getParent();
        if (parent == null) {
            return;
        }

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        parent.removeView(dialogRootView);
        scrollView.addView(dialogRootView);
        parent.addView(scrollView);
    }

    public void adjustDialog() {
        Dialog dialog = getDialog();
        if(dialog == null) {
            return;
        }

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = (int) (screenWidth * 0.9);
        window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void addCancelButton() {
        Toolbar toolbar = getToolbar();
        if (toolbar == null) {
            return;
        }

        View dialogRootView = getView();
        if (!(dialogRootView instanceof ConstraintLayout)) {
            // Root view must be a ConstraintLayout for the following logic to work.
            return;
        }

        ConstraintLayout constraintLayout = (ConstraintLayout) dialogRootView;
        ConstraintSet constraintSet = new ConstraintSet();

        AppCompatImageButton cancelButton = new AppCompatImageButton(requireContext());
        int size = getResources().getDimensionPixelSize(R.dimen.icon_size);
        cancelButton.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        cancelButton.setImageResource(R.drawable.baseline_cancel_24);
        cancelButton.setScaleType(ImageView.ScaleType.FIT_XY);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Add the button to the layout
        cancelButton.setId(View.generateViewId());
        constraintLayout.addView(cancelButton);

        // Apply constraints to position the button
        constraintSet.clone(constraintLayout);
        constraintSet.connect(cancelButton.getId(), ConstraintSet.TOP, toolbar.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(cancelButton.getId(), ConstraintSet.BOTTOM, toolbar.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(cancelButton.getId(), ConstraintSet.END, toolbar.getId(), ConstraintSet.END, PixelUtil.dpToPx(16, requireActivity()));
        constraintSet.applyTo(constraintLayout);
    }
}