package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Keep;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;

public class ConfirmDeleteYearDialog extends BaseDialog {
    public int year;

    @Keep
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

        AppCompatButton B_CONFIRM = findViewById(R.id.confirm_delete_year_dialog_confirmButton);
        B_CONFIRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isComplete = true;
                dismiss();
            }
        });
    }
}
