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
import com.musicslayer.cashmaster.data.persistent.app.Appearance;
import com.musicslayer.cashmaster.data.persistent.app.YearLedgerList;
import com.musicslayer.cashmaster.dialog.AddYearDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.dialog.ConfirmDeleteYearDialog;
import com.musicslayer.cashmaster.dialog.YearSumsDialog;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ClipboardUtil;
import com.musicslayer.cashmaster.util.ColorUtil;
import com.musicslayer.cashmaster.util.FileUtil;
import com.musicslayer.cashmaster.util.JSONUtil;
import com.musicslayer.cashmaster.util.MessageUtil;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.ledger.YearLedgerView;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        PopupMenu yearPopup = new PopupMenu(this, switchYearButton);

        switchYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearPopup.getMenu().clear();

                ArrayList<Integer> years = YearLedger.getAllYears();
                for(int year : years) {
                    yearPopup.getMenu().add("" + year);
                }

                yearPopup.show();
            }
        });

        yearPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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

        // Import/Export Button
        AppCompatImageButton importExportButton = findViewById(R.id.main_importExportButton);
        PopupMenu importExportPopup = new PopupMenu(this, importExportButton);

        importExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importExportPopup.getMenu().clear();
                importExportPopup.getMenu().add("Import (Clipboard)");
                importExportPopup.getMenu().add("Export (Clipboard)");
                importExportPopup.getMenu().add("Export (Email)");
                importExportPopup.show();
            }
        });

        importExportPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String option = item.toString();
                switch(option) {
                    case "Import (Clipboard)":
                        String clipboardText = String.valueOf(ClipboardUtil.importText());

                        if(!"null".equals(clipboardText)) {
                            if(JSONUtil.isValidJSON(clipboardText)) {
                                new YearLedgerList().doImport(clipboardText);
                                updateLayout();
                                ToastUtil.showToast("import_clipboard_success");
                            }
                            else {
                                ToastUtil.showToast("import_clipboard_not_from_app");
                            }
                        }

                        break;
                    case "Export (Clipboard)": {
                        // Export ledger data to clipboard.
                        String json = new YearLedgerList().doExport();
                        ClipboardUtil.exportText("export_data", json, true);
                        break;
                    }
                    case "Export (Email)": {
                        // Export ledger data to email.
                        String json = new YearLedgerList().doExport();

                        // Create temp file with exported data and email it.
                        ArrayList<File> fileArrayList = new ArrayList<>();
                        fileArrayList.add(FileUtil.writeTempFile(json));

                        Date currentDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.US);
                        String timestamp = dateFormat.format(currentDate);

                        String appTitle = getString(R.string.app_title);
                        String subjectText = appTitle + " - Exported Data - " + timestamp;
                        String bodyText = "Exported data is attached.\nTimestamp: " + timestamp;
                        MessageUtil.sendEmail(MainActivity.this, "", subjectText, bodyText, fileArrayList);
                        break;
                    }
                    default:
                        throw new IllegalStateException("option = " + option);
                }
                return true;
            }
        });

        // Color Button
        AppCompatImageButton switchColorButton = findViewById(R.id.main_colorButton);
        PopupMenu colorPopup = new PopupMenu(this, switchColorButton);

        switchColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPopup.getMenu().clear();

                ArrayList<String> colors = Appearance.getAllColors();
                for(String color : colors) {
                    colorPopup.getMenu().add(color);
                }

                colorPopup.show();
            }
        });

        colorPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Appearance.setColor(item.toString());
                recreate();

                updateLayout();
                return true;
            }
        });

        // Mode Button
        AppCompatImageButton modeButton = findViewById(R.id.main_modeButton);
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Appearance.cycleMode();
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
            yearTextView.setTextColor(ColorUtil.getThemeRed(this));
        }
        else {
            yearTextView.setTextColor(ColorUtil.getThemeFeature(this));
        }

        // Mode Button - Icon matches current mode setting
        AppCompatImageButton modeButton = findViewById(R.id.main_modeButton);
        if("auto".equals(Appearance.mode_setting)) {
            modeButton.setImageResource(R.drawable.baseline_auto_mode_24);
        }
        else if("light".equals(Appearance.mode_setting)) {
            modeButton.setImageResource(R.drawable.baseline_light_mode_24);
        }
        else if("dark".equals(Appearance.mode_setting)) {
            modeButton.setImageResource(R.drawable.baseline_dark_mode_24);
        }
        else {
            throw new IllegalStateException("mode_setting = " + Appearance.mode_setting);
        }

        // Year Ledger
        LinearLayoutCompat L = findViewById(R.id.main_todoLinearLayout);
        L.removeAllViews();

        YearLedgerView yearLedgerView = new YearLedgerView(this, YearLedger.currentYearLedger);
        yearLedgerView.setOnLineItemChangeListener(new YearLedgerView.OnLineItemChangeListener() {
            @Override
            public void onChange() {
                updateLayout();
            }
        });
        L.addView(yearLedgerView);
    }
}