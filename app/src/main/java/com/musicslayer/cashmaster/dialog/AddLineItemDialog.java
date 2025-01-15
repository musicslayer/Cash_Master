package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.AmountEditText;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AddLineItemDialog extends BaseDialog {
    public String user_NAME;
    public BigDecimal user_AMOUNT;
    public boolean user_ISINCOME;

    public int year;
    public String month;

    public AddLineItemDialog(Activity activity, Integer year, String month) {
        super(activity);
        this.year = year;
        this.month = month;
    }

    public int getBaseViewID() {
        return R.id.add_line_item_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_add_line_item);

        Toolbar toolbar = findViewById(R.id.add_line_item_dialog_toolbar);
        toolbar.setSubtitle(month);

        final PlainTextEditText E_NAME = findViewById(R.id.add_line_item_dialog_nameEditText);
        final AmountEditText E_AMOUNT = findViewById(R.id.add_line_item_dialog_amountEditText);

        RadioGroup radioGroup = findViewById(R.id.add_line_item_dialog_radioGroup);
        RadioButton rbIncome = findViewById(R.id.add_line_item_dialog_incomeRadioButton);
        radioGroup.check(rbIncome.getId());

        AppCompatButton B_ADD = findViewById(R.id.add_line_item_dialog_createButton);
        B_ADD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform all tests without short circuiting.
                boolean isValid = E_NAME.test() & E_AMOUNT.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    String name = E_NAME.getTextString();
                    BigDecimal amount = new BigDecimal(E_AMOUNT.getTextString()).setScale(2, RoundingMode.UNNECESSARY);
                    boolean isIncome = rbIncome.isChecked();

                    YearLedger yearLedger = YearLedger.getYearLedger(year);

                    if (yearLedger.hasLineItem(month, name)) {
                        ToastUtil.showToast("line_item_exists");
                    } else {
                        user_NAME = name;
                        user_AMOUNT = amount;
                        user_ISINCOME = isIncome;

                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });
    }
}
