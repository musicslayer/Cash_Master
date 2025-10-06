package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.YearEditText;

import java.math.BigInteger;

public class AddYearDialogFragment extends BaseDialogFragment {
    public int output_newYear;

    public static AddYearDialogFragment newInstance() {
        return new AddYearDialogFragment();
    }

    @Keep
    public AddYearDialogFragment() {};

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AddYearDialogFragmentListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.dialog_add_year, container, false);

        toolbar = view.findViewById(R.id.add_year_dialog_toolbar);

        YearEditText E_YEAR = view.findViewById(R.id.add_year_dialog_yearEditText);

        AppCompatButton B_ADD = view.findViewById(R.id.add_year_dialog_createButton);
        B_ADD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isValid = E_YEAR.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    output_newYear = new BigInteger(E_YEAR.getTextString()).intValue();

                    if (LedgerData.ledger.hasYear(output_newYear)) {
                        ToastUtil.showToast("year_exists");
                    }
                    else {
                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onAddYearDialogFragmentComplete(isComplete, output_newYear);
        }
    }

    private Toolbar toolbar;
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    private AddYearDialogFragmentListener listener;
    public interface AddYearDialogFragmentListener {
        void onAddYearDialogFragmentComplete(boolean isComplete, int year);
    }
}