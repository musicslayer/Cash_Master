package com.musicslayer.cashmaster.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ViewUtil {
    public static ArrayList<View> getDirectChildren(View v) {
        if(!(v instanceof ViewGroup)) {
            // Only ViewGroups have children.
            return new ArrayList<>();
        }

        ViewGroup vg = (ViewGroup)v;
        ArrayList<View> children = new ArrayList<>();
        for(int i = 0; i < vg.getChildCount(); i++) {
            children.add(vg.getChildAt(i));
        }
        return children;
    }

    public static ArrayList<View> getAllChildren(View v) {
        if(!(v instanceof ViewGroup)) {
            // Only ViewGroups have children.
            return new ArrayList<>();
        }

        ViewGroup vg = (ViewGroup)v;
        ArrayList<View> children = new ArrayList<>();
        for(int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            children.add(child);
            children.addAll(getAllChildren(child));
        }
        return children;
    }
}
