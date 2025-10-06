package com.musicslayer.cashmaster.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.data.persistent.app.Appearance;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.dialog.AddLineItemDialogFragment;
import com.musicslayer.cashmaster.dialog.AddYearDialogFragment;
import com.musicslayer.cashmaster.dialog.ConfirmDeleteYearDialogFragment;
import com.musicslayer.cashmaster.dialog.EditLineItemDialogFragment;
import com.musicslayer.cashmaster.dialog.YearSumsDialogFragment;
import com.musicslayer.cashmaster.ledger.Ledger;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ClipboardUtil;
import com.musicslayer.cashmaster.util.ColorUtil;
import com.musicslayer.cashmaster.util.FileUtil;
import com.musicslayer.cashmaster.util.JSONUtil;
import com.musicslayer.cashmaster.util.MessageUtil;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.ledger.LineItemView;
import com.musicslayer.cashmaster.view.ledger.MonthLedgerView;
import com.musicslayer.cashmaster.view.ledger.YearLedgerView;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity implements
        MonthLedgerView.OnAddButtonClickListener,
        LineItemView.OnEditButtonClickListener,
        AddLineItemDialogFragment.AddLineItemDialogFragmentListener,
        AddYearDialogFragment.AddYearDialogFragmentListener,
        ConfirmDeleteYearDialogFragment.ConfirmDeleteYearDialogFragmentListener,
        EditLineItemDialogFragment.EditLineItemDialogFragmentListener {

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
        AppCompatImageButton addYearButton = findViewById(R.id.main_addYearButton);
        addYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddYearDialogFragment addYearDialogFragment = AddYearDialogFragment.newInstance();
                addYearDialogFragment.show(getSupportFragmentManager(),"AddYearDialogFragment");
            }
        });

        // Switch Year Button
        AppCompatImageButton switchYearButton = findViewById(R.id.main_switchYearButton);
        PopupMenu yearPopup = new PopupMenu(this, switchYearButton);

        switchYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearPopup.getMenu().clear();

                ArrayList<Integer> years = LedgerData.ledger.getAllYears();
                for(int year : years) {
                    yearPopup.getMenu().add("" + year);
                }

                yearPopup.show();
            }
        });

        yearPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int newYear = Integer.parseInt(item.toString());
                LedgerData.ledger.setCurrentYear(newYear);

                updateLayout();
                return true;
            }
        });

        // Delete Year Button
        AppCompatImageButton deleteYearButton = findViewById(R.id.main_deleteYearButton);
        deleteYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LedgerData.ledger.map_yearLedgers.size() == 1) {
                    ToastUtil.showToast("cannot_delete_only_year");
                }
                else {
                    ConfirmDeleteYearDialogFragment confirmDeleteItemDialogFragment = ConfirmDeleteYearDialogFragment.newInstance(LedgerData.ledger.currentYearLedger.year);
                    confirmDeleteItemDialogFragment.show(getSupportFragmentManager(), "ConfirmDeleteYearDialogFragment");
                }
            }
        });

        // Year Sums Button
        AppCompatImageButton yearSumsButton = findViewById(R.id.main_yearSumsButton);
        yearSumsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearSumsDialogFragment yearSumsDialogFragment = YearSumsDialogFragment.newInstance(LedgerData.ledger.currentYearLedger.year);
                yearSumsDialogFragment.show(getSupportFragmentManager(), "YearSumsDialogFragment");
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
                                LedgerData.ledger = DataBridge.deserialize(clipboardText, Ledger.DESERIALIZER);
                                LedgerData.saveAllData();

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
                        String json = DataBridge.serialize(LedgerData.ledger, Ledger.SERIALIZER);
                        ClipboardUtil.exportText("export_data", json, true);
                        break;
                    }
                    case "Export (Email)": {
                        // Export ledger data to email.
                        String json = DataBridge.serialize(LedgerData.ledger, Ledger.SERIALIZER);

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
        toolbar.setSubtitle("" + LedgerData.ledger.currentYearLedger.year);

        // Current year total
        BigDecimal total = LedgerData.ledger.currentYearLedger.getTotal();
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

        YearLedgerView yearLedgerView = new YearLedgerView(this, LedgerData.ledger.currentYearLedger);
        L.addView(yearLedgerView);
    }

    @Override
    public void onAddButtonClick(int year, String month) {
        AddLineItemDialogFragment addLineItemDialogFragment = AddLineItemDialogFragment.newInstance(year, month);
        addLineItemDialogFragment.show(getSupportFragmentManager(), "AddLineItemDialogFragment");
    }

    @Override
    public void onEditButtonClick(LineItem lineItem) {
        EditLineItemDialogFragment editLineItemDialogFragment = EditLineItemDialogFragment.newInstance(lineItem);
        editLineItemDialogFragment.show(getSupportFragmentManager(), "EditLineItemDialogFragment");
    }

    @Override
    public void onAddLineItemDialogFragmentComplete(Boolean isComplete, int year, String month, String name, BigDecimal amount, boolean isIncome) {
        if(isComplete) {
            // Add the new line item.
            YearLedger yearLedger = LedgerData.ledger.getYearLedger(year);
            yearLedger.addLineItem(month, name, amount, isIncome);

            updateLayout();
        }
    }

    @Override
    public void onAddYearDialogFragmentComplete(boolean isComplete, int newYear) {
        if(isComplete) {
            // Add and then switch to the new year.
            LedgerData.ledger.addYear(newYear);
            LedgerData.ledger.setCurrentYear(newYear);

            updateLayout();
        }
    }

    @Override
    public void onConfirmDeleteYearDialogFragmentComplete(boolean isComplete, int yearToDelete) {
        if(isComplete) {
            // Set the current year to something nearby and then remove the year.
            int newYear = LedgerData.ledger.getNearestYear(yearToDelete);

            LedgerData.ledger.setCurrentYear(newYear);
            LedgerData.ledger.removeYear(yearToDelete);

            updateLayout();
        }
    }

    @Override
    public void onEditLineItemDialogFragmentComplete(boolean isComplete, LineItem lineItem, boolean isDelete, String newName, BigDecimal newAmount, boolean newIsIncome) {
        if(isComplete) {
            // Delete/Edit the line item, and then fire the listener.
            YearLedger yearLedger = LedgerData.ledger.getYearLedger(lineItem.year);

            yearLedger.removeLineItem(lineItem.month, lineItem.name);
            if(!isDelete) {
                yearLedger.addLineItem(lineItem.month, newName, newAmount, newIsIncome);
            }

            updateLayout();
        }
    }
}