package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.dialog.AddLineItemDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.dialog.EditLineItemDialog;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.view.HorizontalSplitView;
import com.musicslayer.cashmaster.view.ImageButtonView;

import java.math.BigDecimal;

public class MonthLedgerView extends LinearLayout {
    public MonthLedger monthLedger;
    private MonthLedgerView.OnLineItemChangeListener onLineItemChangeListener;

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

            // Add Button
            BaseDialogFragment addLineItemDialogFragment = BaseDialogFragment.newInstance(AddLineItemDialog.class, -1, "");
            addLineItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(((AddLineItemDialog)dialog).isComplete) {
                        // Add the line item, and then fire the listener.
                        String name = ((AddLineItemDialog)dialog).user_NAME;
                        BigDecimal amount = ((AddLineItemDialog)dialog).user_AMOUNT;
                        boolean isIncome = ((AddLineItemDialog)dialog).user_ISINCOME;

                        int year = ((AddLineItemDialog)dialog).year;
                        String month = ((AddLineItemDialog)dialog).month;

                        YearLedger yearLedger = YearLedger.getYearLedger(year);
                        yearLedger.addLineItem(month, name, amount, isIncome);

                        onLineItemChangeListener.onChange();
                    }
                }
            });
            addLineItemDialogFragment.restoreListeners(context, "add_line_item");

            ImageButtonView B_ADD = new ImageButtonView(context);
            B_ADD.setImageResource(R.drawable.baseline_add_box_24);
            B_ADD.setTextSize(20);
            B_ADD.setTextString(monthLedger.month);
            B_ADD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addLineItemDialogFragment.updateArguments(AddLineItemDialog.class, monthLedger.year, monthLedger.month);
                    addLineItemDialogFragment.show(context, "add_line_item");
                }
            });

            this.addView(B_ADD);

            // Total
            // TODO Padding on top too large!
            BigDecimal total = monthLedger.getTotal();
            String monthTotalStr = "Total: $" + total.abs();

            TextView T_TOTAL = new TextView(context);
            T_TOTAL.setPadding(0, 0, 0, 20);
            T_TOTAL.setText(monthTotalStr);
            if(total.compareTo(BigDecimal.ZERO) < 0) {
                T_TOTAL.setTextColor(getResources().getColor(R.color.red));
            }
            else {
                T_TOTAL.setTextColor(getResources().getColor(R.color.feature));
            }

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
                            BigDecimal amount = ((EditLineItemDialog)dialog).user_AMOUNT;
                            boolean isIncome = ((EditLineItemDialog)dialog).user_ISINCOME;

                            yearLedger.removeLineItem(lineItem.month, lineItem.name);
                            yearLedger.addLineItem(lineItem.month, name, amount, isIncome);
                        }

                        onLineItemChangeListener.onChange();
                    }
                }
            });
            editLineItemDialogFragment.restoreListeners(context, "edit_line_item");

            // Line Items
            HorizontalSplitView horizontalSplitView = new HorizontalSplitView(context);
            horizontalSplitView.setPadding(0, 0, 0, 100);

            // Incomes
            for(LineItem lineItem : monthLedger.getSortedIncomes()) {
                ImageButtonView B_EDIT = new ImageButtonView(context);
                B_EDIT.setImageResource(R.drawable.baseline_edit_24);
                B_EDIT.setTextString(lineItem.name + " $" + lineItem.amount);
                B_EDIT.setTextColor(getResources().getColor(R.color.feature));
                B_EDIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                        editLineItemDialogFragment.show(context, "edit_line_item");
                    }
                });

                horizontalSplitView.addViewA(B_EDIT);
            }

            // Expenses
            for(LineItem lineItem : monthLedger.getSortedExpenses()) {
                ImageButtonView B_EDIT = new ImageButtonView(context);
                B_EDIT.setImageResource(R.drawable.baseline_edit_24);
                B_EDIT.setTextString(lineItem.name + " $" + lineItem.amount);
                B_EDIT.setTextColor(getResources().getColor(R.color.red));
                B_EDIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                        editLineItemDialogFragment.show(context, "edit_line_item");
                    }
                });

                horizontalSplitView.addViewB(B_EDIT);
            }

            this.addView(horizontalSplitView);
        }
    }

    public void setOnLineItemChangeListener(MonthLedgerView.OnLineItemChangeListener onLineItemChangeListener) {
        this.onLineItemChangeListener = onLineItemChangeListener;
    }

    abstract public static class OnLineItemChangeListener {
        abstract public void onChange();
    }
}