package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ViewUtil;

import java.util.ArrayList;

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

    public void setOnLineItemChangeListener(YearLedgerView.OnLineItemChangeListener onLineItemChangeListener) {
        ArrayList<View> children = ViewUtil.getAllChildren(this);
        for(View child : children) {
            if(child instanceof MonthLedgerView) {
                ((MonthLedgerView) child).setOnLineItemChangeListener(onLineItemChangeListener);
            }
        }
    }

    abstract public static class OnLineItemChangeListener {
        abstract public void onChange();
    }
}
