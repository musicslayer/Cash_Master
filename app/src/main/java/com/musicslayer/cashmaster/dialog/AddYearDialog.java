package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.ImageButtonView;
import com.musicslayer.cashmaster.view.red.YearEditText;

import java.math.BigInteger;

public class AddYearDialog extends BaseDialog {
    public int user_YEAR;

    public AddYearDialog(Activity activity) {
        super(activity);
    }

    public int getBaseViewID() {
        return R.id.add_year_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_add_year);

        final YearEditText E_YEAR = findViewById(R.id.add_year_dialog_yearEditText);

        ImageButtonView B_ADD = findViewById(R.id.add_year_dialog_createButton);
        B_ADD.setImageResource(R.drawable.baseline_add_box_24);
        B_ADD.setTextString("Add");
        B_ADD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isValid = E_YEAR.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    int year = new BigInteger(E_YEAR.getTextString()).intValue();

                    if (YearLedger.hasYear(year)) {
                        ToastUtil.showToast("year_exists");
                    } else {
                        user_YEAR = year;

                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });
    }
}
