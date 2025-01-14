package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;

public class YearLedgerView extends LinearLayout {
    public YearLedger yearLedger;

    public YearLedgerView(Context context) {
        super(context);
    }

    public YearLedgerView(Context context, YearLedger yearLedger) {
        super(context);
        this.yearLedger = yearLedger;
        this.makeLayout();
    }

    public void makeLayout() {
        this.setOrientation(VERTICAL);

        Context context = getContext();

        if(yearLedger == null) {
            setVisibility(GONE);
        }
        else {
            setVisibility(VISIBLE);

            for(MonthLedger monthLedger : yearLedger.monthLedgers) {
                MonthLedgerView monthLedgerView = new MonthLedgerView(context, monthLedger);
                this.addView(monthLedgerView);
            }
        }
    }

    public void setOnLineItemAddListener(MonthLedgerView.OnLineItemAddListener onLineItemAddListener) {
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(child instanceof MonthLedgerView) {
                ((MonthLedgerView) child).setOnLineItemAddListener(onLineItemAddListener);
            }
        }
    }

    public void setOnLineItemEditListener(MonthLedgerView.OnLineItemEditListener onLineItemEditListener) {
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(child instanceof MonthLedgerView) {
                ((MonthLedgerView) child).setOnLineItemEditListener(onLineItemEditListener);
            }
        }
    }
}
