package com.musicslayer.cashmaster.activity;

import android.content.Intent;
import android.os.Bundle;

import com.musicslayer.blisslist.app.App;
import com.musicslayer.blisslist.data.persistent.app.CategoryList;
import com.musicslayer.blisslist.data.persistent.app.Theme;
import com.musicslayer.blisslist.item.Category;
import com.musicslayer.blisslist.util.ToastUtil;

// This Activity class only exists for initialization code, not to be seen by the user.
public class InitialActivity extends BaseActivity {
    @Override
    public void createLayout(Bundle savedInstanceState) {
        // Don't actually show anything. This activity exists because it is the only one allowed to perform initialization.
        startActivity(new Intent(this, com.musicslayer.blisslist.activity.MainActivity.class));
        finish();
    }

    public void initialize() {
        // Initialize all the local app objects.
        ToastUtil.initialize();

        // Load all the stored data into local memory.
        new CategoryList().loadAllData();
        new Theme().loadAllData();

        // If there are no categories, create a default one so the user can get started easily.
        Category.createDefaultIfNeeded();

        // When first loading the app, start with the user's favorite category.
        Category.currentCategory = Category.favoriteCategory;

        // Save all the stored data right after loading it.
        // This makes sure the stored data is initialized and helps remove data with outdated versions.
        new CategoryList().saveAllData();

        App.isAppInitialized = true;
    }
}