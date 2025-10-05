package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Keep;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.AmountEditText;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EditLineItemDialog extends BaseDialog {
    public boolean user_ISDELETE;
    public String user_NAME;
    public BigDecimal user_AMOUNT;
    public boolean user_ISINCOME;

    public LineItem lineItem;

    @Keep
    public EditLineItemDialog(Activity activity, LineItem lineItem) {
        super(activity);
        this.lineItem = lineItem;
    }

    public int getBaseViewID() {
        return R.id.edit_line_item_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_edit_line_item);

        Toolbar toolbar = findViewById(R.id.edit_line_item_dialog_toolbar);
        toolbar.setSubtitle(lineItem.month);

        PlainTextEditText E_NAME = findViewById(R.id.edit_line_item_dialog_nameEditText);
        AmountEditText E_AMOUNT = findViewById(R.id.edit_line_item_dialog_amountEditText);

        RadioGroup radioGroup = findViewById(R.id.edit_line_item_dialog_radioGroup);
        RadioButton rbIncome = findViewById(R.id.edit_line_item_dialog_incomeRadioButton);
        RadioButton rbExpense = findViewById(R.id.edit_line_item_dialog_expenseRadioButton);
        if(lineItem.isIncome) {
            radioGroup.check(rbIncome.getId());
        }
        else {
            radioGroup.check(rbExpense.getId());
        }

        E_NAME.setTextString(lineItem.name);
        E_AMOUNT.setTextString("" + lineItem.amount);

        AppCompatButton B_EDIT = findViewById(R.id.edit_line_item_dialog_editButton);
        B_EDIT.setOnClickListener(new View.OnClickListener() {
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

                    // Adjust name to make aggregation easier.
                    name = name.trim();
                    name = name.toUpperCase();

                    YearLedger yearLedger = LedgerData.ledger.getYearLedger(lineItem.year);

                    if (!lineItem.name.equals(name) && yearLedger.hasLineItem(lineItem.month, name)) {
                        ToastUtil.showToast("line_item_exists");
                    }
                    else {
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
