package com.musicslayer.cashmaster.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.Theme;
import com.musicslayer.cashmaster.dialog.AddLineItemDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.view.ledger.MonthLedgerView;
import com.musicslayer.cashmaster.view.ledger.YearLedgerView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Add Button
        BaseDialogFragment addLineItemDialogFragment = BaseDialogFragment.newInstance(AddLineItemDialog.class, -1);
        addLineItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((AddLineItemDialog)dialog).isComplete) {
                    String month = ((AddLineItemDialog)dialog).user_MONTH;
                    String name = ((AddLineItemDialog)dialog).user_NAME;
                    int amount = ((AddLineItemDialog)dialog).user_AMOUNT;
                    boolean isIncome = ((AddLineItemDialog)dialog).user_ISINCOME;

                    YearLedger yearLedger = YearLedger.currentYearLedger;
                    yearLedger.addLineItem(month, name, amount, isIncome);

                    updateLayout();
                }
            }
        });
        addLineItemDialogFragment.restoreListeners(this, "addLineItem");

        AppCompatImageButton addButton = findViewById(R.id.main_addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLineItemDialogFragment.updateArguments(AddLineItemDialog.class, YearLedger.currentYearLedger.year);
                addLineItemDialogFragment.show(MainActivity.this, "addLineItem");

                updateLayout();
            }
        });

        // Theme Button
        AppCompatImageButton themeButton = findViewById(R.id.main_themeButton);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Theme.cycleTheme();
                recreate();

                updateLayout();
            }
        });

        updateLayout();
    }

    public void updateLayout() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);

        // Subtitle - Use current year
        int total = YearLedger.currentYearLedger.getTotal();
        String yearTotalStr = YearLedger.currentYearLedger.year + " Total: $" + Math.abs(total);
        if(total < 0) {
            yearTotalStr = "<font color=#ff0000>" + yearTotalStr + "</font>";
        }
        toolbar.setSubtitle(Html.fromHtml(yearTotalStr));

        // Theme Button - Icon matches current theme setting
        AppCompatImageButton themeButton = findViewById(R.id.main_themeButton);
        if("auto".equals(Theme.theme_setting)) {
            themeButton.setImageResource(R.drawable.baseline_auto_mode_24);
        }
        else if("light".equals(Theme.theme_setting)) {
            themeButton.setImageResource(R.drawable.baseline_light_mode_24);
        }
        else {
            themeButton.setImageResource(R.drawable.baseline_dark_mode_24);
        }

        LinearLayoutCompat L = findViewById(R.id.main_todoLinearLayout);
        L.removeAllViews();

        YearLedgerView yearLedgerView = new YearLedgerView(this, YearLedger.currentYearLedger);
        yearLedgerView.setOnLineItemEditListener(new MonthLedgerView.OnLineItemEditListener() {
            @Override
            public void onEdit() {
                updateLayout();
            }
        });
        L.addView(yearLedgerView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        if(bundle != null) {
            updateLayout();
        }
        super.onRestoreInstanceState(bundle);
    }
}