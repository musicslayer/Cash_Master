package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.dialog.AddLineItemDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.dialog.EditLineItemDialog;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.view.HorizontalSplitView;

public class MonthLedgerView extends LinearLayout {
    public MonthLedger monthLedger;
    private MonthLedgerView.OnLineItemAddListener onLineItemAddListener;
    private MonthLedgerView.OnLineItemEditListener onLineItemEditListener;

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

            LinearLayout L_MONTH = new LinearLayout(context);
            L_MONTH.setOrientation(HORIZONTAL);
            L_MONTH.setGravity(Gravity.CENTER_VERTICAL);

            // Add Button
            BaseDialogFragment addLineItemDialogFragment = BaseDialogFragment.newInstance(AddLineItemDialog.class, -1, "");
            addLineItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(((AddLineItemDialog)dialog).isComplete) {
                        // Add the line item, and then fire the listener.
                        String name = ((AddLineItemDialog)dialog).user_NAME;
                        int amount = ((AddLineItemDialog)dialog).user_AMOUNT;
                        boolean isIncome = ((AddLineItemDialog)dialog).user_ISINCOME;

                        int year = ((AddLineItemDialog)dialog).year;
                        String month = ((AddLineItemDialog)dialog).month;
                        YearLedger yearLedger = YearLedger.getYearLedger(year);
                        yearLedger.addLineItem(month, name, amount, isIncome);

                        onLineItemAddListener.onAdd();
                    }
                }
            });
            addLineItemDialogFragment.restoreListeners(context, "addLineItem");

            AppCompatImageButton B_ADD = new AppCompatImageButton(context);
            B_ADD.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            B_ADD.setImageResource(R.drawable.baseline_add_box_24);
            B_ADD.setPadding(0, 0, 0, 0);
            B_ADD.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            B_ADD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addLineItemDialogFragment.updateArguments(AddLineItemDialog.class, monthLedger.year, monthLedger.month);
                    addLineItemDialogFragment.show(context, "addLineItem");
                }
            });

            // Month
            AppCompatTextView T_MONTH = new AppCompatTextView(context);
            T_MONTH.setPadding(30, 0, 0, 0);
            T_MONTH.setTextSize(20);
            T_MONTH.setText(monthLedger.month);

            L_MONTH.addView(B_ADD);
            L_MONTH.addView(T_MONTH);
            this.addView(L_MONTH);

            // Total
            AppCompatTextView T_TOTAL = new AppCompatTextView(context);
            T_TOTAL.setPadding(0, 0, 0, 20);
            int total = monthLedger.getTotal();
            String monthTotalStr = "Total: $" + Math.abs(total);
            if(total < 0) {
                monthTotalStr = "<font color=#ff0000>" + monthTotalStr + "</font>";
            }
            T_TOTAL.setText(Html.fromHtml(monthTotalStr));

            this.addView(T_TOTAL);

            // Edit Line Item Dialog
            BaseDialogFragment editLineItemDialogFragment = BaseDialogFragment.newInstance(EditLineItemDialog.class, (Object) null);
            editLineItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(((EditLineItemDialog)dialog).isComplete) {
                        // Delete/Edit the line item, and then fire the listener.
                        LineItem lineItem = ((EditLineItemDialog)dialog).lineItem;
                        YearLedger yearLedger = YearLedger.getYearLedger(lineItem.year);

                        boolean isDelete = ((EditLineItemDialog)dialog).user_ISDELETE;
                        if(isDelete) {
                            yearLedger.removeLineItem(lineItem.month, lineItem.name);
                        }
                        else {
                            String name = ((EditLineItemDialog)dialog).user_NAME;
                            int amount = ((EditLineItemDialog)dialog).user_AMOUNT;
                            boolean isIncome = ((EditLineItemDialog)dialog).user_ISINCOME;

                            yearLedger.removeLineItem(lineItem.month, lineItem.name);
                            yearLedger.addLineItem(lineItem.month, name, amount, isIncome);
                        }

                        onLineItemEditListener.onEdit();
                    }
                }
            });
            editLineItemDialogFragment.restoreListeners(context, "editLineItem");

            // Line Items
            HorizontalSplitView horizontalSplitView = new HorizontalSplitView(context);
            horizontalSplitView.setPadding(0, 0, 0, 100);

            // Incomes
            for(LineItem lineItem : monthLedger.getSortedIncomes()) {
                LinearLayout L_LINEITEM = new LinearLayout(context);
                L_LINEITEM.setOrientation(HORIZONTAL);
                L_LINEITEM.setPadding(0, 0, 0, 20);
                L_LINEITEM.setGravity(Gravity.CENTER_VERTICAL);

                AppCompatImageButton B_EDIT = new AppCompatImageButton(context);
                B_EDIT.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                B_EDIT.setImageResource(R.drawable.baseline_edit_24);
                B_EDIT.setPadding(0, 0, 0, 0);
                B_EDIT.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                B_EDIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                        editLineItemDialogFragment.show(context, "editLineItem");
                    }
                });

                AppCompatTextView T_LINEITEM = new AppCompatTextView(context);
                T_LINEITEM.setPadding(30, 0, 0, 0);
                String lineItemStr = lineItem.name + " $" + lineItem.amount;
                T_LINEITEM.setText(Html.fromHtml(lineItemStr));

                L_LINEITEM.addView(B_EDIT);
                L_LINEITEM.addView(T_LINEITEM);
                horizontalSplitView.addViewA(L_LINEITEM);
            }

            // Expenses
            for(LineItem lineItem : monthLedger.getSortedExpenses()) {
                LinearLayout L_LINEITEM = new LinearLayout(context);
                L_LINEITEM.setOrientation(HORIZONTAL);
                L_LINEITEM.setPadding(0, 0, 0, 20);
                L_LINEITEM.setGravity(Gravity.CENTER_VERTICAL);

                AppCompatImageButton B_EDIT = new AppCompatImageButton(context);
                B_EDIT.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                B_EDIT.setImageResource(R.drawable.baseline_edit_24);
                B_EDIT.setPadding(0, 0, 0, 0);
                B_EDIT.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                B_EDIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                        editLineItemDialogFragment.show(context, "editLineItem");
                    }
                });

                AppCompatTextView T_LINEITEM = new AppCompatTextView(context);
                T_LINEITEM.setPadding(30, 0, 0, 0);
                String lineItemStr = lineItem.name + " $" + lineItem.amount;
                lineItemStr = "<font color=#ff0000>" + lineItemStr + "</font>";
                T_LINEITEM.setText(Html.fromHtml(lineItemStr));

                L_LINEITEM.addView(B_EDIT);
                L_LINEITEM.addView(T_LINEITEM);
                horizontalSplitView.addViewB(L_LINEITEM);
            }

            this.addView(horizontalSplitView);
        }
    }

    public void setOnLineItemAddListener(MonthLedgerView.OnLineItemAddListener onLineItemAddListener) {
        this.onLineItemAddListener = onLineItemAddListener;
    }

    public void setOnLineItemEditListener(MonthLedgerView.OnLineItemEditListener onLineItemEditListener) {
        this.onLineItemEditListener = onLineItemEditListener;
    }

    abstract public static class OnLineItemAddListener {
        abstract public void onAdd();
    }

    abstract public static class OnLineItemEditListener {
        abstract public void onEdit();
    }
}