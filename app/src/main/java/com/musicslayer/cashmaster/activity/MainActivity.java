package com.musicslayer.cashmaster.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.flexbox.FlexboxLayout;
import com.musicslayer.blisslist.R;
import com.musicslayer.blisslist.data.persistent.app.Theme;
import com.musicslayer.blisslist.dialog.AddItemDialog;
import com.musicslayer.blisslist.dialog.BaseDialogFragment;
import com.musicslayer.blisslist.dialog.ChooseCategoryDialog;
import com.musicslayer.blisslist.dialog.ConfirmDeleteItemDialog;
import com.musicslayer.blisslist.dialog.RenameItemDialog;
import com.musicslayer.blisslist.item.Category;
import com.musicslayer.blisslist.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends BaseActivity {
    String currentDeleteItemName;
    String currentRenameItemName;
    public boolean isEditMode;
    public boolean isRemoveMode;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void createLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        BaseDialogFragment addItemDialogFragment = BaseDialogFragment.newInstance(AddItemDialog.class);
        addItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((AddItemDialog)dialog).isComplete) {
                    String itemName = ((AddItemDialog)dialog).user_ITEMNAME;
                    Category.currentCategory.addItem(itemName, false);

                    updateLayout();
                }
            }
        });
        addItemDialogFragment.restoreListeners(this, "add");

        AppCompatImageButton addButton = findViewById(R.id.main_addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditMode = false;
                isRemoveMode = false;

                addItemDialogFragment.show(MainActivity.this, "add");

                updateLayout();
            }
        });

        AppCompatImageButton removeButton = findViewById(R.id.main_removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditMode = false;
                isRemoveMode = !isRemoveMode;

                updateLayout();
            }
        });

        AppCompatImageButton editButton = findViewById(R.id.main_editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditMode = !isEditMode;
                isRemoveMode = false;

                updateLayout();
            }
        });

        BaseDialogFragment chooseCategoryDialogFragment = BaseDialogFragment.newInstance(ChooseCategoryDialog.class);
        chooseCategoryDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((ChooseCategoryDialog)dialog).isComplete) {
                    String currentCategoryName = ((ChooseCategoryDialog)dialog).user_CATEGORY;
                    Category.makeCurrentCategory(currentCategoryName);

                    updateLayout();
                }
            }
        });
        chooseCategoryDialogFragment.restoreListeners(this, "choose_category");

        AppCompatImageButton categoryButton = findViewById(R.id.main_categoryButton);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditMode = false;
                isRemoveMode = false;

                chooseCategoryDialogFragment.show(MainActivity.this, "choose_category");

                updateLayout();
            }
        });

        AppCompatImageButton themeButton = findViewById(R.id.main_themeButton);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditMode = false;
                isRemoveMode = false;

                Theme.cycleTheme();
                recreate();

                updateLayout();
            }
        });

        updateLayout();
    }

    public void updateLayout() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);

        String subtitle = Category.currentCategory.categoryName;
        if(Category.currentCategory.equals(Category.favoriteCategory)) {
            subtitle = "♥ " + subtitle;
        }
        toolbar.setSubtitle(subtitle);

        AppCompatImageButton editButton = findViewById(R.id.main_editButton);
        if(isEditMode) {
            editButton.setColorFilter(Color.RED);
        }
        else {
            editButton.clearColorFilter();
        }

        AppCompatImageButton removeButton = findViewById(R.id.main_removeButton);
        if(isRemoveMode) {
            removeButton.setColorFilter(Color.RED);
        }
        else {
            removeButton.clearColorFilter();
        }

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

        BaseDialogFragment confirmDeleteItemDialogFragment = BaseDialogFragment.newInstance(ConfirmDeleteItemDialog.class, "");
        confirmDeleteItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((ConfirmDeleteItemDialog)dialog).isComplete) {
                    Category.currentCategory.removeItem(currentDeleteItemName);
                    updateLayout();
                }
            }
        });
        confirmDeleteItemDialogFragment.restoreListeners(this, "delete_item");

        BaseDialogFragment renameItemDialogFragment = BaseDialogFragment.newInstance(RenameItemDialog.class, "");
        renameItemDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(((RenameItemDialog)dialog).isComplete) {
                    String newName = ((RenameItemDialog)dialog).user_NEWNAME;
                    Category.currentCategory.renameItem(currentRenameItemName, newName);

                    updateLayout();
                }
            }
        });
        renameItemDialogFragment.restoreListeners(this, "rename_item");

        if("list".equals(Category.currentCategory.style)) {
            updateLayoutList(confirmDeleteItemDialogFragment, renameItemDialogFragment);
        }
        else {
            updateLayoutTodo(confirmDeleteItemDialogFragment, renameItemDialogFragment);
        }
    }

    public void updateLayoutList(BaseDialogFragment confirmDeleteItemDialogFragment, BaseDialogFragment renameItemDialogFragment) {
        AppCompatTextView needText = findViewById(R.id.main_needText);
        AppCompatTextView haveText = findViewById(R.id.main_haveText);
        AppCompatTextView todoText = findViewById(R.id.main_todoText);

        ScrollView needScrollView = findViewById(R.id.main_needScrollView);
        ScrollView haveScrollView = findViewById(R.id.main_haveScrollView);
        ScrollView todoScrollView = findViewById(R.id.main_todoScrollView);

        needText.setVisibility(View.VISIBLE);
        haveText.setVisibility(View.VISIBLE);
        todoText.setVisibility(View.GONE);
        needScrollView.setVisibility(View.VISIBLE);
        haveScrollView.setVisibility(View.VISIBLE);
        todoScrollView.setVisibility(View.GONE);

        needText.setText("Need (" + Category.currentCategory.numNeed() + ")");
        haveText.setText("Have (" + Category.currentCategory.numHave() + ")");

        FlexboxLayout needFlexboxLayout = findViewById(R.id.main_needFlexboxLayout);
        FlexboxLayout haveFlexboxLayout = findViewById(R.id.main_haveFlexboxLayout);

        needFlexboxLayout.removeAllViews();
        haveFlexboxLayout.removeAllViews();

        ArrayList<String> itemNames = new ArrayList<>(Category.currentCategory.map_items.keySet());
        Collections.sort(itemNames, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }
        });

        for(String itemName : itemNames) {
            Item item = Category.currentCategory.getItem(itemName);

            AppCompatButton B_ITEM = new AppCompatButton(this);
            B_ITEM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_forward_24, 0, 0, 0);
            B_ITEM.setText(itemName);
            B_ITEM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

                    updateLayout();
                }
            });

            if(item.isHave) {
                haveFlexboxLayout.addView(B_ITEM);
            }
            else {
                needFlexboxLayout.addView(B_ITEM);
            }
        }
    }

    public void updateLayoutTodo(BaseDialogFragment confirmDeleteItemDialogFragment, BaseDialogFragment renameItemDialogFragment) {
        AppCompatTextView needText = findViewById(R.id.main_needText);
        AppCompatTextView haveText = findViewById(R.id.main_haveText);
        AppCompatTextView todoText = findViewById(R.id.main_todoText);

        ScrollView needScrollView = findViewById(R.id.main_needScrollView);
        ScrollView haveScrollView = findViewById(R.id.main_haveScrollView);
        ScrollView todoScrollView = findViewById(R.id.main_todoScrollView);

        needText.setVisibility(View.GONE);
        haveText.setVisibility(View.GONE);
        todoText.setVisibility(View.VISIBLE);
        needScrollView.setVisibility(View.GONE);
        haveScrollView.setVisibility(View.GONE);
        todoScrollView.setVisibility(View.VISIBLE);

        todoText.setText("To Do (" + Category.currentCategory.numNeed() + ")");

        LinearLayoutCompat todoLinearLayout = findViewById(R.id.main_todoLinearLayout);
        todoLinearLayout.removeAllViews();

        ArrayList<String> itemNames = new ArrayList<>(Category.currentCategory.map_items.keySet());
        Collections.sort(itemNames, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }
        });

        for(String itemName : itemNames) {
            Item item = Category.currentCategory.getItem(itemName);

            AppCompatCheckBox B_ITEM = new AppCompatCheckBox(this);
            B_ITEM.setChecked(item.isHave);
            B_ITEM.setText(itemName);
            B_ITEM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

                    updateLayout();
                }
            });

            todoLinearLayout.addView(B_ITEM);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("currentDeleteCategoryName", currentDeleteItemName);
        bundle.putString("currentRenameCategoryName", currentRenameItemName);
        bundle.putBoolean("isEditMode", isEditMode);
        bundle.putBoolean("isRemoveMode", isRemoveMode);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        if(bundle != null) {
            currentDeleteItemName = bundle.getString("currentDeleteCategoryName");
            currentRenameItemName = bundle.getString("currentRenameCategoryName");
            isEditMode = bundle.getBoolean("isEditMode");
            isRemoveMode = bundle.getBoolean("isRemoveMode");
            updateLayout();
        }
        super.onRestoreInstanceState(bundle);
    }
}