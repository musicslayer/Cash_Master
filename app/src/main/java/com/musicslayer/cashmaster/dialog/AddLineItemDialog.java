package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.BooleanEditText;
import com.musicslayer.cashmaster.view.red.Int6EditText;
import com.musicslayer.cashmaster.view.red.MonthEditText;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

import java.math.BigInteger;

public class AddLineItemDialog extends BaseDialog {
    public String user_MONTH;
    public String user_NAME;
    public int user_AMOUNT;
    public boolean user_ISINCOME;

    public int year;

    public AddLineItemDialog(Activity activity, Integer year) {
        super(activity);
        this.year = year;
    }

    public int getBaseViewID() {
        return R.id.add_line_item_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_add_line_item);

        final MonthEditText E_MONTH = findViewById(R.id.add_month_dialog_monthEditText);
        final PlainTextEditText E_NAME = findViewById(R.id.add_month_dialog_nameEditText);
        final Int6EditText E_AMOUNT = findViewById(R.id.add_month_dialog_amountEditText);
        final BooleanEditText E_ISINCOME = findViewById(R.id.add_month_dialog_isIncomeEditText);

        AppCompatButton B_CREATE = findViewById(R.id.add_month_dialog_createButton);
        B_CREATE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Give toasts for all different cases...
                // Perform all tests without short circuiting.
                boolean isValid = E_MONTH.test() & E_NAME.test() & E_AMOUNT.test() & E_ISINCOME.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    String month = E_MONTH.getTextString();
                    String name = E_NAME.getTextString();
                    int amount = new BigInteger(E_AMOUNT.getTextString()).intValue();
                    boolean isIncome = Boolean.parseBoolean(E_ISINCOME.getTextString());

                    YearLedger yearLedger = YearLedger.getYearLedger(year);

                    if (yearLedger.hasLineItem(month, name)) {
                        // Line item already exists.
                        ToastUtil.showToast("line_item_name_used");
                    } else {
                        user_MONTH = month;
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
