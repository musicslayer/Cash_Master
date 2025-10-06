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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;
import com.musicslayer.cashmaster.ledger.LineItem;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.ToastUtil;
import com.musicslayer.cashmaster.view.red.AmountEditText;
import com.musicslayer.cashmaster.view.red.PlainTextEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EditLineItemDialogFragment extends BaseDialogFragment {
    public LineItem input_lineItem;

    public boolean output_isDelete;
    public String output_name;
    public BigDecimal output_amount;
    public boolean output_isIncome;

    public static EditLineItemDialogFragment newInstance(LineItem lineItem) {
        Bundle args = new Bundle();
        args.putParcelable("lineItem", lineItem);

        EditLineItemDialogFragment fragment = new EditLineItemDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (EditLineItemDialogFragmentListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            input_lineItem = getArguments().getParcelable("lineItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.dialog_edit_line_item, container, false);

        toolbar = view.findViewById(R.id.edit_line_item_dialog_toolbar);

        PlainTextEditText E_NAME = view.findViewById(R.id.edit_line_item_dialog_nameEditText);
        AmountEditText E_AMOUNT = view.findViewById(R.id.edit_line_item_dialog_amountEditText);

        RadioGroup radioGroup = view.findViewById(R.id.edit_line_item_dialog_radioGroup);
        RadioButton rbIncome = view.findViewById(R.id.edit_line_item_dialog_incomeRadioButton);
        RadioButton rbExpense = view.findViewById(R.id.edit_line_item_dialog_expenseRadioButton);
        if(input_lineItem.isIncome) {
            radioGroup.check(rbIncome.getId());
        }
        else {
            radioGroup.check(rbExpense.getId());
        }

        E_NAME.setTextString(input_lineItem.name);
        E_AMOUNT.setTextString("" + input_lineItem.amount);

        AppCompatButton B_EDIT = view.findViewById(R.id.edit_line_item_dialog_editButton);
        B_EDIT.setOnClickListener(new View.OnClickListener() {
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

                    YearLedger yearLedger = LedgerData.ledger.getYearLedger(input_lineItem.year);

                    if (!input_lineItem.name.equals(name) && yearLedger.hasLineItem(input_lineItem.month, name)) {
                        ToastUtil.showToast("line_item_exists");
                    }
                    else {
                        output_isDelete = false;

                        output_name = name;
                        output_amount = amount;
                        output_isIncome = isIncome;

                        isComplete = true;
                        dismiss();
                    }
                }
            }
        });

        AppCompatButton B_DELETE = view.findViewById(R.id.edit_line_item_dialog_deleteButton);
        B_DELETE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                output_isDelete = true;

                isComplete = true;
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onEditLineItemDialogFragmentComplete(isComplete, input_lineItem, output_isDelete, output_name, output_amount, output_isIncome);
        }
    }

    private Toolbar toolbar;
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    private EditLineItemDialogFragmentListener listener;
    public interface EditLineItemDialogFragmentListener {
        void onEditLineItemDialogFragmentComplete(boolean isComplete, LineItem lineItem, boolean isDelete, String newName, BigDecimal newAmount, boolean newIsIncome);
    }
}