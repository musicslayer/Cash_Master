package com.musicslayer.cashmaster.view.ledger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatTextView;

import com.musicslayer.cashmaster.util.PixelUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EditLedgerTable extends TableLayout {
    public EditLedgerTable(Context context) {
        this(context, null);
    }

    public EditLedgerTable(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void addRow(ImageButton imageButton, String name, BigDecimal amount, int color) {
        EditLedgerTable.LedgerTableRow row = new EditLedgerTable.LedgerTableRow(getContext(), imageButton, name, amount, color);
        this.addView(row);
    }

    static class LedgerTableRow extends TableRow {
        public LedgerTableRow(Context context) {
            super(context);
        }

        public LedgerTableRow(Context context, ImageButton imageButton, String name, BigDecimal amount, int color) {
            super(context);
            this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            this.setOrientation(HORIZONTAL);
            this.setGravity(Gravity.CENTER_VERTICAL);

            this.makeRow(imageButton, name, amount, color);
        }

        public void makeRow(ImageButton imageButton, String name, BigDecimal amount, int color) {
            Context context = getContext();

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

            this.addView(imageButton);
            this.addView(t0);
            this.addView(t1);
        }
    }
}
