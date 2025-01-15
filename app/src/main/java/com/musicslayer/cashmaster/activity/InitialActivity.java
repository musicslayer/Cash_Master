package com.musicslayer.cashmaster.activity;

import android.content.Intent;
import android.os.Bundle;

import com.musicslayer.cashmaster.app.App;
import com.musicslayer.cashmaster.data.persistent.app.Theme;
import com.musicslayer.cashmaster.data.persistent.app.YearLedgerList;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;

// TODO Backup to database?
// TODO Dialogs can have "X" button in upper right
// TODO Create image button button?

// This Activity class only exists for initialization code, not to be seen by the user.
public class InitialActivity extends BaseActivity {
    @Override
    public void createLayout(Bundle savedInstanceState) {
        // Don't actually show anything. This activity exists because it is the only one allowed to perform initialization.
        startActivity(new Intent(this, com.musicslayer.cashmaster.activity.MainActivity.class));
        finish();
    }

    public void initialize() {
        // Initialize all the local app objects.
        ToastUtil.initialize();

        // Load all the stored data into local memory.
        new Theme().loadAllData();
        new YearLedgerList().loadAllData();

        // If there are no years, create a default one so the user can get started easily.
        YearLedger.createDefaultIfNeeded();

        // Save all the stored data right after loading it.
        // This makes sure the stored data is initialized and helps remove data with outdated versions.
        new Theme().saveAllData();
        new YearLedgerList().saveAllData();

        App.isAppInitialized = true;
    }
}