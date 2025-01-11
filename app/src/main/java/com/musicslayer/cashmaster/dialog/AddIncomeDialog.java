package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

public class AddIncomeDialog extends BaseDialog {
    public String user_INCOMENAME;
    public int user_INCOMEAMOUNT;

    public AddIncomeDialog(Activity activity) {
        super(activity);
    }

    public int getBaseViewID() {
        return R.id.add_month_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        /*
        setContentView(R.layout.dialog_add_item);

        final PlainTextEditText E = findViewById(R.id.add_item_dialog_editText);

        AppCompatButton B_CREATE = findViewById(R.id.add_item_dialog_createButton);
        B_CREATE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isValid = E.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    user_ITEMNAME = E.getTextString();

                    if(Category.currentCategory.isItemSaved(user_ITEMNAME)) {
                        ToastUtil.showToast("item_name_used");
                    }
                    else {
                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });

         */
    }
}
