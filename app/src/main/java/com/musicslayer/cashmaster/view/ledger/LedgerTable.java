package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LedgerTable extends TableLayout {
    public LedgerTable(Context context) {
        this(context, null);
    }

    public LedgerTable(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void addRow(String name, BigDecimal amount) {
        LedgerTable.LedgerTableRow row = new LedgerTable.LedgerTableRow(getContext(), name, amount);
        this.addView(row);
    }

    static class LedgerTableRow extends TableRow {
        public LedgerTableRow(Context context) {
            super(context);
        }

        public LedgerTableRow(Context context, String name, BigDecimal amount) {
            super(context);
            setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
            this.makeRow(name, amount);
        }

        public void makeRow(String name, BigDecimal amount) {
            Context context = getContext();

            AppCompatTextView t0 = new AppCompatTextView(context);
            t0.setText(name);

            AppCompatTextView t1 = new AppCompatTextView(context);
            t1.setPadding(30, 0, 0, 0);

            if(amount == null) {
                amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
            }
            String amountStr = "$" + amount.abs();
            t1.setText(amountStr);

            if(amount.compareTo(BigDecimal.ZERO) < 0) {
                t1.setTextColor(context.getColor(R.color.red));
            }
            else {
                t1.setTextColor(context.getColor(R.color.feature));
            }

            this.addView(t0);
            this.addView(t1);
        }
    }
}
