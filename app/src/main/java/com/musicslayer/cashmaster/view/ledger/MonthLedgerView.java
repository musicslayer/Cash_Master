package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.util.ColorUtil;
import com.musicslayer.cashmaster.util.PixelUtil;
import com.musicslayer.cashmaster.view.HorizontalSplitView;
import com.musicslayer.cashmaster.view.ImageButtonView;

import java.math.BigDecimal;

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

            // Add Button
            ImageButtonView B_ADD = new ImageButtonView(context);
            B_ADD.setImageResource(R.drawable.baseline_add_box_24);
            B_ADD.setImageSize(getResources().getDimensionPixelSize(R.dimen.icon_size));
            B_ADD.setTextSize(getResources().getDimension(R.dimen.font_size_header));
            B_ADD.setTextString(monthLedger.month);
            B_ADD.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onAddButtonClickListener != null) {
                        onAddButtonClickListener.onAddButtonClick(monthLedger.year, monthLedger.month);
                    }
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

            HorizontalSplitView horizontalSplitView = new HorizontalSplitView(context);
            horizontalSplitView.setPadding(0, 0, 0, PixelUtil.dpToPx(30, context));

            // Incomes
            TableLayout ledgerTableA = new TableLayout(context);
            for(LineItem lineItem : monthLedger.getSortedIncomes()) {
                LineItemView lineItemView = new LineItemView(context, lineItem);
                lineItemView.setOnEditButtonClickListener((LineItemView.OnEditButtonClickListener)context);
                ledgerTableA.addView(lineItemView);
            }
            horizontalSplitView.addViewA(ledgerTableA);

            // Expenses
            TableLayout ledgerTableB = new TableLayout(context);
            for(LineItem lineItem : monthLedger.getSortedExpenses()) {
                LineItemView lineItemView = new LineItemView(context, lineItem);
                lineItemView.setOnEditButtonClickListener((LineItemView.OnEditButtonClickListener)context);
                ledgerTableB.addView(lineItemView);
            }
            horizontalSplitView.addViewB(ledgerTableB);

            this.addView(horizontalSplitView);
        }
    }

    private OnAddButtonClickListener onAddButtonClickListener;
    public interface OnAddButtonClickListener {
        void onAddButtonClick(int year, String month);
    }
    public void setOnAddButtonClickListener(OnAddButtonClickListener listener) {
        this.onAddButtonClickListener = listener;
    }
}