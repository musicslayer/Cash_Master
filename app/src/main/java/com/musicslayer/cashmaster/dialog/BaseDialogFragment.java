package com.musicslayer.cashmaster.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.musicslayer.cashmaster.util.ContextUtil;
import com.musicslayer.cashmaster.util.ReflectUtil;

public class BaseDialogFragment extends DialogFragment implements DialogInterface.OnShowListener {
    public DialogInterface.OnShowListener SL;
    public DialogInterface.OnDismissListener DL;

    public static BaseDialogFragment newInstance(Class<?> clazz, Object... args) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("class", clazz);
        bundle.putSerializable("args", args);

        BaseDialogFragment fragment = new BaseDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onShow(@NonNull DialogInterface dialog) {
        if(SL != null) {
            SL.onShow(this.getDialog());
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        doDismiss(dialog);
    }

    public void doDismiss(@NonNull DialogInterface dialog) {
        if(DL != null) {
            DL.onDismiss(dialog);
        }
    }

    public void setOnShowListener(DialogInterface.OnShowListener sl) {
        SL = sl;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dl) {
        DL = dl;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        assert bundle != null;

        Class<Dialog> clazz = (Class<Dialog>)bundle.getSerializable("class");
        Object[] argArray = (Object[])bundle.getSerializable("args");

        Dialog dialog = ReflectUtil.constructDialogInstance(clazz, getActivity(), argArray);
        dialog.setOnShowListener(this);

        return dialog;
    }

    public void show(Context context, String tag) {
        if(!isAdded()) {
            FragmentManager fm = getFragmentManager(context);
            this.show(fm, tag);
            fm.executePendingTransactions();
        }
    }

    public void restoreListeners(Context context, String tag) {
        BaseDialogFragment bdf = (BaseDialogFragment)getFragmentByTag(context, tag);
        if (bdf != null) {
            bdf.setOnShowListener(SL);
            bdf.setOnDismissListener(DL);
        }
    }

    public void updateArguments(Class<?> clazz, Object... args) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("class", clazz);
        bundle.putSerializable("args", args);

        this.setArguments(bundle);
    }

    public static FragmentManager getFragmentManager(Context context) {
        return ((AppCompatActivity)ContextUtil.getActivityFromContext(context)).getSupportFragmentManager();
    }

    public static Fragment getFragmentByTag(Context context, String tag) {
        return getFragmentManager(context).findFragmentByTag(tag);
    }
}
