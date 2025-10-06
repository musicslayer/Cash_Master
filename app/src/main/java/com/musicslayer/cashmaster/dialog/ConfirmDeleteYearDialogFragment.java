package com.musicslayer.cashmaster.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.musicslayer.cashmaster.R;

public class ConfirmDeleteYearDialogFragment extends BaseDialogFragment {
    private int input_year;

    public static ConfirmDeleteYearDialogFragment newInstance(int year) {
        Bundle args = new Bundle();
        args.putInt("year", year);

        ConfirmDeleteYearDialogFragment fragment = new ConfirmDeleteYearDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ConfirmDeleteYearDialogFragmentListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            input_year = getArguments().getInt("year");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.dialog_confirm_delete_year, container, false);

        toolbar = view.findViewById(R.id.confirm_delete_year_dialog_toolbar);

        toolbar.setTitle("Delete Year " + input_year + "?");

        AppCompatButton B_CONFIRM = view.findViewById(R.id.confirm_delete_year_dialog_confirmButton);
        B_CONFIRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            listener.onConfirmDeleteYearDialogFragmentComplete(isComplete, input_year);
        }
    }

    private Toolbar toolbar;
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    private ConfirmDeleteYearDialogFragmentListener listener;
    public interface ConfirmDeleteYearDialogFragmentListener {
        void onConfirmDeleteYearDialogFragmentComplete(boolean isComplete, int year);
    }
}