package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Keep;
import androidx.appcompat.widget.AppCompatButton;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.YearEditText;

import java.math.BigInteger;

public class AddYearDialog extends BaseDialog {
    public int user_YEAR;

    @Keep
    public AddYearDialog(Activity activity) {
        super(activity);
    }

    public int getBaseViewID() {
        return R.id.add_year_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_add_year);

        YearEditText E_YEAR = findViewById(R.id.add_year_dialog_yearEditText);

        AppCompatButton B_ADD = findViewById(R.id.add_year_dialog_createButton);
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
                    }
                    else {
                        user_YEAR = year;

                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });
    }
}
