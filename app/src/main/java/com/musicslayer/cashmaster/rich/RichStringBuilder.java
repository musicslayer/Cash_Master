package com.musicslayer.cashmaster.rich;

import android.text.Html;

import androidx.annotation.NonNull;

public class RichStringBuilder {
    public StringBuilder s = new StringBuilder();
    boolean isRich;

    public RichStringBuilder(boolean isRich) {
        this.isRich = isRich;
    }

    public RichStringBuilder appendRich(String str) {
        // Append text but format for rich if applicable.
        if(isRich) {
            s.append(enrich(str));
        }
        else {
            s.append(str);
        }

        return this;
    }

    public RichStringBuilder append(String str) {
        // Do not apply rich formatting. Input string is assumed to already be in rich format if that is desired.
        s.append(str);
        return this;
    }

    public String enrich(String str) {
        // Escape most characters.
        str = Html.escapeHtml(str);

        // Handle certain whitespace characters.
        str = str.replace("&#10;", "<br/>"); // "\n" (Don't bother with "\r" here).
        str = str.replace(" ", "&nbsp;");
        return str;
    }

    @NonNull
    @Override
    public String toString() {
        return s.toString();
    }
}