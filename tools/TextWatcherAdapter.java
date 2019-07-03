package com.healbe.healbe_example_andorid.tools;

import android.text.Editable;
import android.text.TextWatcher;

public class TextWatcherAdapter implements TextWatcher {
    private Runnable r;

    public TextWatcherAdapter(Runnable onTextChanged) {
        this.r = onTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (r != null)
            r.run();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
