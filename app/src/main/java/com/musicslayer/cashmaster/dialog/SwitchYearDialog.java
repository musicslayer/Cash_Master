package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.Int4EditText;

import java.math.BigInteger;

public class SwitchYearDialog extends BaseDialog {
    public int user_YEAR;

    public SwitchYearDialog(Activity activity) {
        super(activity);
    }

    public int getBaseViewID() {
        return R.id.switch_year_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_switch_year);

        final Int4EditText E_YEAR = findViewById(R.id.switch_year_dialog_yearEditText);

        AppCompatButton B_CREATE = findViewById(R.id.switch_year_dialog_createButton);
        B_CREATE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isValid = E_YEAR.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    int year = new BigInteger(E_YEAR.getTextString()).intValue();

                    if (!YearLedger.hasYear(year)) {
                        ToastUtil.showToast("year_does_not_exist");
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
