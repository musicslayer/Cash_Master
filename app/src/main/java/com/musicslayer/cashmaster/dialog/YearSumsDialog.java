package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.PixelUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class YearSumsDialog extends BaseDialog {
    public int year;

    public YearSumsDialog(Activity activity, Integer year) {
        super(activity);
        this.year = year;
    }

    public int getBaseViewID() {
        return R.id.year_sums_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_year_sums);

        Toolbar toolbar = findViewById(R.id.year_sums_dialog_toolbar);
        toolbar.setSubtitle("" + year);

        YearLedger yearLedger = YearLedger.getYearLedger(year);

        // Current year total
        BigDecimal total = yearLedger.getTotal();
        String yearTotalStr = "Total: $" + total.abs();

        TextView yearTextView = findViewById(R.id.year_sums_dialog_yearTextView);
        yearTextView.setText(yearTotalStr);
        if(total.compareTo(BigDecimal.ZERO) < 0) {
            yearTextView.setTextColor(activity.getColor(R.color.red));
        }
        else {
            yearTextView.setTextColor(activity.getColor(R.color.feature));
        }

        // Aggregate sums of line items
        HashMap<String, BigDecimal> sums = yearLedger.getSums();
        ArrayList<String> names = new ArrayList<>(sums.keySet());
        Collections.sort(names);

        TableLayout ledgerTable = findViewById(R.id.year_sums_dialog_yearSumsLedgerTable);
        for(String name : names) {
            BigDecimal amount = sums.get(name);

            AppCompatTextView t0 = new AppCompatTextView(activity);
            t0.setText(name);

            AppCompatTextView t1 = new AppCompatTextView(activity);
            t1.setPadding(PixelUtil.dpToPx(10, activity), 0, 0, 0);

            if(amount == null) {
                amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
            }
            String amountStr = "$" + amount.abs();
            t1.setText(amountStr);

            if(amount.compareTo(BigDecimal.ZERO) < 0) {
                t1.setTextColor(activity.getColor(R.color.red));
            }
            else {
                t1.setTextColor(activity.getColor(R.color.feature));
            }

            TableRow row = new TableRow(activity);
            row.addView(t0);
            row.addView(t1);

            ledgerTable.addView(row);
        }
    }
}
