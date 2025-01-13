package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.view.HorizontalSplitView;

public class MonthLedgerView extends LinearLayout {
    public MonthLedger monthLedger;

    public MonthLedgerView(Context context) {
        super(context);
    }

    public MonthLedgerView(Context context, MonthLedger monthLedger) {
        super(context);
        this.monthLedger = monthLedger;
        this.makeLayout();
    }

    public void makeLayout() {
        this.setOrientation(VERTICAL);

        Context context = getContext();

        if(monthLedger == null) {
            setVisibility(GONE);
        }
        else {
            setVisibility(VISIBLE);

            // Month
            AppCompatTextView T_MONTH = new AppCompatTextView(context);
            int total = monthLedger.getTotal();
            String monthTotalStr = monthLedger.month + " Total: $" + Math.abs(total);
            if(total < 0) {
                monthTotalStr = "<font color=#ff0000>" + monthTotalStr + "</font>";
            }
            T_MONTH.setText(Html.fromHtml(monthTotalStr));

            this.addView(T_MONTH);

            // Line Items
            HorizontalSplitView horizontalSplitView = new HorizontalSplitView(context);
            horizontalSplitView.setPadding(0, 0, 0, 100);

            // Incomes
            for(LineItem lineItem : monthLedger.getSortedIncomes()) {
                AppCompatButton B_LINEITEM = new AppCompatButton(context);
                B_LINEITEM.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                B_LINEITEM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit_24, 0, 0, 0);
                String str = lineItem.name + " $" + lineItem.amount;
                if(!lineItem.isIncome) {
                    str = "<font color=#ff0000>" + str + "</font>";
                }
                B_LINEITEM.setText(Html.fromHtml(str));


                B_LINEITEM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                horizontalSplitView.addViewA(B_LINEITEM);
            }

            // Expenses
            for(LineItem lineItem : monthLedger.getSortedExpenses()) {
                AppCompatButton B_LINEITEM = new AppCompatButton(context);
                B_LINEITEM.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                B_LINEITEM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit_24, 0, 0, 0);
                String str = lineItem.name + " $" + lineItem.amount;
                str = "<font color=#ff0000>" + str + "</font>";
                B_LINEITEM.setText(Html.fromHtml(str));

                B_LINEITEM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                horizontalSplitView.addViewB(B_LINEITEM);
            }

            this.addView(horizontalSplitView);
        }
    }
}

/*
if(isEditMode) {
    currentRenameItemName = itemName;
    renameItemDialogFragment.updateArguments(RenameItemDialog.class, itemName);
    renameItemDialogFragment.show(MainActivity.this, "rename_item");
}
else if(isRemoveMode) {
    currentDeleteItemName = itemName;
    confirmDeleteItemDialogFragment.updateArguments(ConfirmDeleteItemDialog.class, itemName);
    confirmDeleteItemDialogFragment.show(MainActivity.this, "delete_item");
}
else {
    Category.currentCategory.toggleItem(itemName);
}

isEditMode = false;
isRemoveMode = false;


updateLayout();

 */