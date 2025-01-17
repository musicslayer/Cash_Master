package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.dialog.AddLineItemDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.dialog.EditLineItemDialog;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ColorUtil;
import com.musicslayer.cashmaster.util.PixelUtil;
import com.musicslayer.cashmaster.view.HorizontalSplitView;
import com.musicslayer.cashmaster.view.ImageButtonView;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
            B_ADD.setImageSize(getResources().getDimensionPixelSize(R.dimen.icon_size));
            B_ADD.setTextSize(getResources().getDimension(R.dimen.font_size_header));
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
            BigDecimal total = monthLedger.getTotal();
            String monthTotalStr = "Total: $" + total.abs();

            TextView T_TOTAL = new TextView(context);
            T_TOTAL.setPadding(0, 0, 0, PixelUtil.dpToPx(5, context));
            T_TOTAL.setText(monthTotalStr);
            if(total.compareTo(BigDecimal.ZERO) < 0) {
                T_TOTAL.setTextColor(ColorUtil.getThemeRed(context));
            }
            else {
                T_TOTAL.setTextColor(ColorUtil.getThemeFeature(context));
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

            HorizontalSplitView horizontalSplitView = new HorizontalSplitView(context);
            horizontalSplitView.setPadding(0, 0, 0, PixelUtil.dpToPx(30, context));

            // Incomes
            TableLayout ledgerTableA = new TableLayout(context);
            for(LineItem lineItem : monthLedger.getSortedIncomes()) {
                String name = lineItem.name;
                BigDecimal amount = lineItem.amount;
                int size = getResources().getDimensionPixelSize(R.dimen.icon_size);
                int color = ColorUtil.getThemeFeature(context);

                AppCompatImageButton B_EDIT = new AppCompatImageButton(context);
                B_EDIT.setImageResource(R.drawable.baseline_edit_24);
                B_EDIT.setLayoutParams(new TableRow.LayoutParams(size, size));
                B_EDIT.setScaleType(ImageView.ScaleType.FIT_XY);
                B_EDIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                        editLineItemDialogFragment.show(context, "edit_line_item");
                    }
                });

                AppCompatTextView t0 = new AppCompatTextView(context);
                t0.setPadding(PixelUtil.dpToPx(10, context), 0, 0, 0);
                t0.setText(name);

                AppCompatTextView t1 = new AppCompatTextView(context);
                t1.setPadding(PixelUtil.dpToPx(10, context), 0, 0, 0);

                if(amount == null) {
                    amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
                }
                String amountStr = "$" + amount;
                t1.setText(amountStr);
                t1.setTextColor(color);

                TableRow row = new TableRow(context);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.addView(B_EDIT);
                row.addView(t0);
                row.addView(t1);

                ledgerTableA.addView(row);
            }
            horizontalSplitView.addViewA(ledgerTableA);

            // Expenses
            TableLayout ledgerTableB = new TableLayout(context);
            for(LineItem lineItem : monthLedger.getSortedExpenses()) {
                String name = lineItem.name;
                BigDecimal amount = lineItem.amount;
                int size = getResources().getDimensionPixelSize(R.dimen.icon_size);
                int color = ColorUtil.getThemeRed(context);

                AppCompatImageButton B_EDIT = new AppCompatImageButton(context);
                B_EDIT.setImageResource(R.drawable.baseline_edit_24);
                B_EDIT.setLayoutParams(new TableRow.LayoutParams(size, size));
                B_EDIT.setScaleType(ImageView.ScaleType.FIT_XY);
                B_EDIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                        editLineItemDialogFragment.show(context, "edit_line_item");
                    }
                });

                AppCompatTextView t0 = new AppCompatTextView(context);
                t0.setPadding(PixelUtil.dpToPx(10, context), 0, 0, 0);
                t0.setText(name);

                AppCompatTextView t1 = new AppCompatTextView(context);
                t1.setPadding(PixelUtil.dpToPx(10, context), 0, 0, 0);

                if(amount == null) {
                    amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
                }
                String amountStr = "$" + amount;
                t1.setText(amountStr);
                t1.setTextColor(color);

                TableRow row = new TableRow(context);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.addView(B_EDIT);
                row.addView(t0);
                row.addView(t1);

                ledgerTableB.addView(row);
            }
            horizontalSplitView.addViewB(ledgerTableB);

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