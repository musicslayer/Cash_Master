package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;

import java.math.BigDecimal;
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

        YearLedger yearLedger = YearLedger.getYearLedger(year);

        // Current year total
        BigDecimal total = yearLedger.getTotal();
        String yearTotalStr = year + " Total: $" + total.abs();

        TextView yearTextView = findViewById(R.id.year_sums_dialog_yearTextView);
        yearTextView.setText(yearTotalStr);
        if(total.compareTo(BigDecimal.ZERO) < 0) {
            yearTextView.setTextColor(activity.getColor(R.color.red));
        }
        else {
            yearTextView.setTextColor(activity.getColor(R.color.feature));
        }

        // Aggregate sums of line items
        LinearLayout yearSumsLinearLayout = findViewById(R.id.year_sums_dialog_yearSumsLinearLayout);

        HashMap<String, BigDecimal> sums = yearLedger.getSums();

        ArrayList<String> names = new ArrayList<>(sums.keySet());
        Collections.sort(names);
        for(String name : names) {
            BigDecimal amount = sums.get(name);
            TextView amountTextView = new TextView(activity);
            amountTextView.setText(name + " $" + amount.abs());

            if(amount.compareTo(BigDecimal.ZERO) < 0) {
                amountTextView.setTextColor(activity.getColor(R.color.red));
            }
            else {
                amountTextView.setTextColor(activity.getColor(R.color.feature));
            }

            yearSumsLinearLayout.addView(amountTextView);
        }
    }
}
