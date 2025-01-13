package com.musicslayer.cashmaster.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.Theme;
import com.musicslayer.cashmaster.dialog.AddLineItemDialog;
import com.musicslayer.cashmaster.dialog.BaseDialogFragment;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.MonthLedger;
import com.musicslayer.cashmaster.ledger.YearLedger;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    public ArrayList<YearLedger> yearLedgers = new ArrayList<>();

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
                    MonthLedger monthLedger = yearLedger.getMonthLedger(month);
                    monthLedger.addLineItem(name, amount, isIncome);

                    updateLayout();
                }
            }
        });
        addLineItemDialogFragment.restoreListeners(this, "addMonth");

        AppCompatImageButton addButton = findViewById(R.id.main_addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLineItemDialogFragment.updateArguments(AddLineItemDialog.class, YearLedger.currentYearLedger.year);
                addLineItemDialogFragment.show(MainActivity.this, "add");

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

        // Subtitle - Match current year
        String subtitle = "" + YearLedger.currentYearLedger.year;
        int total = YearLedger.currentYearLedger.getTotal();
        toolbar.setSubtitle(subtitle + " Total: $" + total);

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

        updateLayoutInfo();
    }

    public void updateLayoutInfo() {
        LinearLayoutCompat L = findViewById(R.id.main_todoLinearLayout);
        L.removeAllViews();

        //FlexboxLayout needFlexboxLayout = findViewById(R.id.main_needFlexboxLayout);
        //FlexboxLayout haveFlexboxLayout = findViewById(R.id.main_haveFlexboxLayout);

        //needFlexboxLayout.removeAllViews();
        //haveFlexboxLayout.removeAllViews();

        ArrayList<MonthLedger> monthLedgers = YearLedger.currentYearLedger.monthLedgers;

        for(MonthLedger monthLedger : monthLedgers) {
            AppCompatButton B_ITEM = new AppCompatButton(this);
            //B_ITEM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_forward_24, 0, 0, 0);
            B_ITEM.setText("" + monthLedger.month);
            B_ITEM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    if(isEditMode) {
                        currentRenameItemName = itemName;
                        renameItemDialogFragment.updateArguments(RenameItemDialog.class, itemName);
                        renameItemDialogFragment.show(MainActivity.this, "rename_item");
                    }
                    else if(isRemoveMode) {
                        currentDeleteItemName = itemName;
                        confirmDeleteItemDialogFragment.updateArguments(ConfirmDeleteItemDialog.class, itemName);
                        confirmDeleteItemDialogFragment.show(MainActivity.this, "delete_item");
                    }
                    else {
                        Category.currentCategory.toggleItem(itemName);
                    }

                    isEditMode = false;
                    isRemoveMode = false;

                     */

                    updateLayout();
                }
            });

            L.addView(B_ITEM);

            // Line Items
            for(LineItem lineItem : monthLedger.lineItems) {
                AppCompatTextView T = new AppCompatTextView(this);
                String str = lineItem.name + " $" + lineItem.amount;
                if(!lineItem.isIncome) {
                    str = "<font color=#ff0000>" + str + "</font>";
                }
                T.setText(Html.fromHtml(str));
                L.addView(T);
            }

            // Total
            AppCompatTextView T = new AppCompatTextView(this);
            T.setText("Total: $" + monthLedger.getTotal());
            L.addView(T);
        }
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