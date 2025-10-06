package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ColorUtil;
import com.musicslayer.cashmaster.util.PixelUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class YearSumsDialogFragment extends BaseDialogFragment {
    private int input_year;

    public static YearSumsDialogFragment newInstance(int year) {
        Bundle args = new Bundle();
        args.putInt("year", year);

        YearSumsDialogFragment fragment = new YearSumsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            input_year = getArguments().getInt("year");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.dialog_year_sums, container, false);

        toolbar = view.findViewById(R.id.year_sums_dialog_toolbar);

        toolbar.setSubtitle("" + input_year);

        YearLedger yearLedger = LedgerData.ledger.getYearLedger(input_year);

        // Current year total
        BigDecimal total = yearLedger.getTotal();
        String yearTotalStr = "Total: $" + total.abs();

        TextView yearTextView = view.findViewById(R.id.year_sums_dialog_yearTextView);
        yearTextView.setText(yearTotalStr);
        if(total.compareTo(BigDecimal.ZERO) < 0) {
            yearTextView.setTextColor(ColorUtil.getThemeRed(activity));
        }
        else {
            yearTextView.setTextColor(ColorUtil.getThemeFeature(activity));
        }

        // Aggregate sums of line items
        HashMap<String, BigDecimal> sums = yearLedger.getSums();
        ArrayList<String> names = new ArrayList<>(sums.keySet());
        names.sort(null);

        TableLayout ledgerTable = view.findViewById(R.id.year_sums_dialog_yearSumsLedgerTable);
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
                t1.setTextColor(ColorUtil.getThemeRed(activity));
            }
            else {
                t1.setTextColor(ColorUtil.getThemeFeature(activity));
            }

            TableRow row = new TableRow(activity);
            row.addView(t0);
            row.addView(t1);

            ledgerTable.addView(row);
        }

        return view;
    }

    private Toolbar toolbar;
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}