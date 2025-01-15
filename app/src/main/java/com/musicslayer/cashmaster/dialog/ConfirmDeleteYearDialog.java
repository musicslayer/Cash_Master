package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.view.ImageButtonView;

public class ConfirmDeleteYearDialog extends BaseDialog {
    public int year;

    public ConfirmDeleteYearDialog(Activity activity, Integer year) {
        super(activity);
        this.year = year;
    }

    public int getBaseViewID() {
        return R.id.confirm_delete_year_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_confirm_delete_year);

        Toolbar toolbar = findViewById(R.id.confirm_delete_year_dialog_toolbar);
        toolbar.setTitle("Delete Year " + year + "?");

        ImageButtonView B_CONFIRM = findViewById(R.id.confirm_delete_year_dialog_confirmButton);
        B_CONFIRM.setImageResource(R.drawable.baseline_check_24);
        B_CONFIRM.setTextString("Confirm");
        B_CONFIRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isComplete = true;
                dismiss();
            }
        });
    }
}
