package com.musicslayer.cashmaster.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.musicslayer.cashmaster.R;

import java.util.ArrayList;

public class BorderedSpinnerView extends LinearLayout {
    public Spinner spinner;

    public BorderedSpinnerView(Context context) {
        this(context, null);
    }

    public BorderedSpinnerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setBackgroundResource(R.drawable.border_tight);

        spinner = new Spinner(context);
        spinner.setPopupBackgroundResource(R.drawable.spinner);

        // Pick a reasonable minimum width.
        spinner.setMinimumWidth(400);
        spinner.setDropDownWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        this.addView(spinner);
    }

    public void setOptions(String option) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.wrapped_text_dropdown_item, R.id.wrapped_text_dropdown_item_textView, new String[] {option});
        adapter.setDropDownViewResource(R.layout.wrapped_text_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setOptions(String[] options) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.wrapped_text_dropdown_item, R.id.wrapped_text_dropdown_item_textView, options);
        adapter.setDropDownViewResource(R.layout.wrapped_text_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setOptions(ArrayList<String> options) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.wrapped_text_dropdown_item, R.id.wrapped_text_dropdown_item_textView, options.toArray(new String[0]));
        adapter.setDropDownViewResource(R.layout.wrapped_text_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.spinner.setOnItemSelectedListener(onItemSelectedListener);

        // If there is at least one option, choose the first one by default.
        if(this.spinner.getAdapter() != null && this.spinner.getAdapter().getCount() > 0) {
            setSelection(0);
        }
    }

    public void setSelection(int selection) {
        if(this.spinner.getOnItemSelectedListener() != null) {
            this.spinner.getOnItemSelectedListener().onItemSelected(this.spinner, this.spinner.getSelectedView(), selection, 0);
        }
        this.spinner.setSelection(selection);
    }

    public void setSelectionByValue(String selectionValue) {
        // Set spinner selection to the first occurrence of the value.
        for(int i = 0; i < this.spinner.getAdapter().getCount(); i++) {
            String option = (String)this.spinner.getItemAtPosition(i);
            if(selectionValue.equals(option)) {
                this.setSelection(i);
                break;
            }
        }
    }

    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
        if(params == null) {
            params = new ViewGroup.MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        params.setMargins(left, top, right, bottom);
        this.setLayoutParams(params);
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable state = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", state);
        bundle.putInt("selection", this.spinner.getSelectedItemPosition());

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("superState");

            this.setSelection(bundle.getInt("selection"));
        }

        super.onRestoreInstanceState(state);
    }
}
