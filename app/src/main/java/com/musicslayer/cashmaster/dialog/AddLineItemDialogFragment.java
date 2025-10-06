package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.AmountEditText;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AddLineItemDialogFragment extends BaseDialogFragment {
    public int input_year;
    public String input_month;

    public String output_name;
    public BigDecimal output_amount;
    public boolean output_isIncome;

    public static AddLineItemDialogFragment newInstance(Integer year, String month) {
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putString("month", month);

        AddLineItemDialogFragment fragment = new AddLineItemDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Keep
    public AddLineItemDialogFragment() {};

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AddLineItemDialogFragmentListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            input_year = getArguments().getInt("year");
            input_month = getArguments().getString("month");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.dialog_add_line_item, container, false);

        toolbar = view.findViewById(R.id.add_line_item_dialog_toolbar);

        PlainTextEditText E_NAME = view.findViewById(R.id.add_line_item_dialog_nameEditText);
        AmountEditText E_AMOUNT = view.findViewById(R.id.add_line_item_dialog_amountEditText);

        RadioGroup radioGroup = view.findViewById(R.id.add_line_item_dialog_radioGroup);
        RadioButton rbIncome = view.findViewById(R.id.add_line_item_dialog_incomeRadioButton);
        radioGroup.check(rbIncome.getId());

        AppCompatButton B_ADD = view.findViewById(R.id.add_line_item_dialog_createButton);
        B_ADD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform all tests without short circuiting.
                boolean isValid = E_NAME.test() & E_AMOUNT.test();

                if(!isValid) {
                    ToastUtil.showToast("must_fill_inputs");
                }
                else {
                    String name = E_NAME.getTextString();
                    BigDecimal amount = new BigDecimal(E_AMOUNT.getTextString()).setScale(2, RoundingMode.UNNECESSARY);
                    boolean isIncome = rbIncome.isChecked();

                    // Adjust name to make aggregation easier.
                    name = name.trim();
                    name = name.toUpperCase();

                    YearLedger yearLedger = LedgerData.ledger.getYearLedger(input_year);

                    if (yearLedger.hasLineItem(input_month, name)) {
                        ToastUtil.showToast("line_item_exists");
                    }
                    else {
                        output_name = name;
                        output_amount = amount;
                        output_isIncome = isIncome;

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
            listener.onAddLineItemDialogFragmentComplete(isComplete, input_year, input_month, output_name, output_amount, output_isIncome);
        }
    }

    private Toolbar toolbar;
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    private AddLineItemDialogFragmentListener listener;
    public interface AddLineItemDialogFragmentListener {
        void onAddLineItemDialogFragmentComplete(Boolean isComplete, int year, String month, String name, BigDecimal amount, boolean isIncome);
    }
}