package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.BooleanEditText;
import com.musicslayer.cashmaster.view.red.Int6EditText;
import com.musicslayer.cashmaster.view.red.MonthEditText;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

import java.math.BigInteger;

public class EditLineItemDialog extends BaseDialog {
    public boolean user_ISDELETE;
    public String user_MONTH;
    public String user_NAME;
    public int user_AMOUNT;
    public boolean user_ISINCOME;

    public LineItem lineItem;

    public EditLineItemDialog(Activity activity, LineItem lineItem) {
        super(activity);
        this.lineItem = lineItem;
    }

    public int getBaseViewID() {
        return R.id.edit_line_item_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_edit_line_item);

        final MonthEditText E_MONTH = findViewById(R.id.edit_line_item_dialog_monthEditText);
        final PlainTextEditText E_NAME = findViewById(R.id.edit_line_item_dialog_nameEditText);
        final Int6EditText E_AMOUNT = findViewById(R.id.edit_line_item_dialog_amountEditText);
        final BooleanEditText E_ISINCOME = findViewById(R.id.edit_line_item_dialog_isIncomeEditText);

        E_MONTH.setTextString(lineItem.month);
        E_NAME.setTextString(lineItem.name);
        E_AMOUNT.setTextString("" + lineItem.amount);
        E_ISINCOME.setTextString("" + lineItem.isIncome);

        AppCompatButton B_EDIT = findViewById(R.id.edit_line_item_dialog_editButton);
        B_EDIT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

                    YearLedger yearLedger = YearLedger.getYearLedger(lineItem.year);

                    boolean isSame = lineItem.name.equals(name) && lineItem.month.equals(month);

                    if (!isSame && yearLedger.hasLineItem(month, name)) {
                        ToastUtil.showToast("line_item_exists");
                    } else {
                        user_MONTH = month;
                        user_NAME = name;
                        user_AMOUNT = amount;
                        user_ISINCOME = isIncome;

                        user_ISDELETE = false;

                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });

        AppCompatButton B_DELETE = findViewById(R.id.edit_line_item_dialog_deleteButton);
        B_DELETE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user_ISDELETE = true;

                isComplete = true;
                dismiss();
            }
        });
    }
}
