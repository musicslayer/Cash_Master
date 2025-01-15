package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.view.BorderedSpinnerView;
import com.musicslayer.cashmaster.view.ImageButtonView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class SwitchYearDialog extends BaseDialog {
    public int user_YEAR;

    public int year;

    public SwitchYearDialog(Activity activity, Integer year) {
        super(activity);
        this.year = year;
    }

    public int getBaseViewID() {
        return R.id.switch_year_dialog;
    }

    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_switch_year);

        BorderedSpinnerView bsv = findViewById(R.id.switch_year_dialog_spinner);
        ArrayList<Integer> years = new ArrayList<>(YearLedger.map_yearLedgers.keySet());
        Collections.sort(years);

        ArrayList<String> yearStrings = new ArrayList<>();
        for(int year : years) {
            yearStrings.add("" + year);
        }

        bsv.setOptions(yearStrings);
        bsv.setSelectionByValue("" + year);

        ImageButtonView B_SWITCH = findViewById(R.id.switch_year_dialog_createButton);
        B_SWITCH.setImageResource(R.drawable.baseline_check_24);
        B_SWITCH.setTextString("Switch");
        B_SWITCH.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user_YEAR = new BigInteger((String)bsv.spinner.getSelectedItem()).intValue();

                isComplete = true;
                dismiss();
            }
        });
    }
}
