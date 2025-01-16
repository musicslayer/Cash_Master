package com.musicslayer.cashmaster.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.Theme;
import com.musicslayer.cashmaster.dialog.AddYearDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.dialog.ConfirmDeleteYearDialog;
import com.musicslayer.cashmaster.dialog.YearSumsDialog;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.ledger.MonthLedgerView;
import com.musicslayer.cashmaster.view.ledger.YearLedgerView;

import java.math.BigDecimal;
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

        // Add Year Button
        BaseDialogFragment addYearDialogFragment = BaseDialogFragment.newInstance(AddYearDialog.class);
        addYearDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((AddYearDialog)dialog).isComplete) {
                    // Add and then switch to the new year.
                    int newYear = ((AddYearDialog)dialog).user_YEAR;
                    YearLedger.addYear(newYear);
                    YearLedger.setCurrentYear(newYear);

                    updateLayout();
                }
            }
        });
        addYearDialogFragment.restoreListeners(this, "add_year");

        AppCompatImageButton addYearButton = findViewById(R.id.main_addYearButton);
        addYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addYearDialogFragment.show(MainActivity.this, "add_year");
            }
        });

        // Switch Year Button
        AppCompatImageButton switchYearButton = findViewById(R.id.main_switchYearButton);
        PopupMenu popup = new PopupMenu(this, switchYearButton);

        switchYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.getMenu().clear();

                ArrayList<Integer> years = YearLedger.getAllYears();
                for(int year : years) {
                    popup.getMenu().add("" + year);
                }

                popup.show();
            }
        });

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int newYear = Integer.parseInt(item.toString());
                YearLedger.setCurrentYear(newYear);

                updateLayout();
                return true;
            }
        });

        // Confirm Delete Button
        BaseDialogFragment confirmDeleteItemDialogFragment = BaseDialogFragment.newInstance(ConfirmDeleteYearDialog.class, -1);
        confirmDeleteItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((ConfirmDeleteYearDialog)dialog).isComplete) {
                    // Set current year to something nearby and then remove the year.
                    int oldYear = ((ConfirmDeleteYearDialog)dialog).year;
                    int newYear = YearLedger.getNearestYear(oldYear);

                    YearLedger.setCurrentYear(newYear);
                    YearLedger.removeYear(oldYear);

                    updateLayout();
                }
            }
        });
        confirmDeleteItemDialogFragment.restoreListeners(this, "delete_year");

        AppCompatImageButton deleteYearButton = findViewById(R.id.main_deleteYearButton);
        deleteYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(YearLedger.map_yearLedgers.size() == 1) {
                    ToastUtil.showToast("cannot_delete_only_year");
                }
                else {
                    confirmDeleteItemDialogFragment.updateArguments(ConfirmDeleteYearDialog.class, YearLedger.currentYearLedger.year);
                    confirmDeleteItemDialogFragment.show(MainActivity.this, "delete_year");
                }
            }
        });

        // Year Sums Button
        AppCompatImageButton yearSumsButton = findViewById(R.id.main_yearSumsButton);
        yearSumsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDialogFragment.newInstance(YearSumsDialog.class, YearLedger.currentYearLedger.year).show(MainActivity.this, "year_sums");
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
        // Current year
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setSubtitle("" + YearLedger.currentYearLedger.year);

        // Current year total
        BigDecimal total = YearLedger.currentYearLedger.getTotal();
        String yearTotalStr = "Total: $" + total.abs();

        TextView yearTextView = findViewById(R.id.main_yearTotalTextView);
        yearTextView.setText(yearTotalStr);
        if(total.compareTo(BigDecimal.ZERO) < 0) {
            yearTextView.setTextColor(getColor(R.color.red));
        }
        else {
            yearTextView.setTextColor(getColor(R.color.feature));
        }

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

        // Year Ledger
        LinearLayoutCompat L = findViewById(R.id.main_todoLinearLayout);
        L.removeAllViews();

        YearLedgerView yearLedgerView = new YearLedgerView(this, YearLedger.currentYearLedger);
        yearLedgerView.setOnLineItemChangeListener(new MonthLedgerView.OnLineItemChangeListener() {
            @Override
            public void onChange() {
                updateLayout();
            }
        });
        L.addView(yearLedgerView);
    }
}