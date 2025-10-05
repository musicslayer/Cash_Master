package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.dialog.EditLineItemDialog;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ColorUtil;
import com.musicslayer.cashmaster.util.PixelUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LineItemView extends TableRow {
    public LineItem lineItem;
    private YearLedgerView.OnLineItemChangeListener onLineItemChangeListener;

    public LineItemView(Context context) {
        super(context);
    }

    public LineItemView(Context context, LineItem lineItem) {
        super(context);
        this.lineItem = lineItem;
        this.makeLayout();
    }

    public void makeLayout() {
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        Context context = getContext();

        if(lineItem == null) {
            setVisibility(GONE);
        }
        else {
            setVisibility(VISIBLE);

            // Edit Line Item Dialog
            BaseDialogFragment editLineItemDialogFragment = BaseDialogFragment.newInstance(EditLineItemDialog.class, (Object) null);
            editLineItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(((EditLineItemDialog)dialog).isComplete) {
                        // Delete/Edit the line item, and then fire the listener.
                        LineItem lineItem = ((EditLineItemDialog)dialog).lineItem;
                        YearLedger yearLedger = LedgerData.ledger.getYearLedger(lineItem.year);

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

            String name = lineItem.name;
            BigDecimal amount = lineItem.amount;
            int size = getResources().getDimensionPixelSize(R.dimen.icon_size);
            int color = lineItem.isIncome ? ColorUtil.getThemeFeature(context) : ColorUtil.getThemeRed(context);

            AppCompatImageButton B_EDIT = new AppCompatImageButton(context);
            B_EDIT.setImageResource(R.drawable.baseline_edit_24);
            B_EDIT.setLayoutParams(new TableRow.LayoutParams(size, size));
            B_EDIT.setScaleType(ImageView.ScaleType.FIT_XY);
            B_EDIT.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    editLineItemDialogFragment.updateArguments(EditLineItemDialog.class, lineItem);
                    editLineItemDialogFragment.show(context, "edit_line_item");
                }
            });

            AppCompatTextView t0 = new AppCompatTextView(context);
            t0.setPadding(PixelUtil.dpToPx(10, context), 0, 0, 0);
            t0.setText(name);
            t0.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_lineitem_text));

            AppCompatTextView t1 = new AppCompatTextView(context);
            t1.setPadding(PixelUtil.dpToPx(10, context), 0, 0, 0);

            if(amount == null) {
                amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
            }
            String amountStr = "$" + amount;
            t1.setText(amountStr);
            t1.setTextColor(color);
            t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_lineitem_text));

            this.addView(B_EDIT);
            this.addView(t0);
            this.addView(t1);
        }
    }

    public void setOnLineItemChangeListener(YearLedgerView.OnLineItemChangeListener onLineItemChangeListener) {
        this.onLineItemChangeListener = onLineItemChangeListener;
    }
}