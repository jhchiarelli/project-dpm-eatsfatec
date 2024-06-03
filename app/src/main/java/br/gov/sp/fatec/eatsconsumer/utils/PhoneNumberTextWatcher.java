package br.gov.sp.fatec.eatsconsumer.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PhoneNumberTextWatcher implements TextWatcher {

    private final EditText editText;
    private boolean isUpdating;
    private int previousLength;

    public PhoneNumberTextWatcher(EditText editText) {
        this.editText = editText;
        this.isUpdating = false;
        this.previousLength = 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        previousLength = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isUpdating) {
            return;
        }

        String str = s.toString().replaceAll("[^\\d]", "");
        StringBuilder formatted = new StringBuilder();

        int length = str.length();
        int selectionIndex = editText.getSelectionStart();

        if (length > 0) {
            formatted.append("(");
            if (length > 2) {
                formatted.append(str.substring(0, 2));
                formatted.append(") ");
                if (length > 7) {
                    formatted.append(str.substring(2, 7));
                    formatted.append("-");
                    if (length > 11) {
                        formatted.append(str.substring(7, 11));
                    } else {
                        formatted.append(str.substring(7));
                    }
                } else {
                    formatted.append(str.substring(2));
                }
            } else {
                formatted.append(str);
            }
        }

        isUpdating = true;
        editText.setText(formatted.toString());

        if (formatted.length() > previousLength) {
            selectionIndex += formatted.length() - previousLength;
        } else if (formatted.length() < previousLength) {
            selectionIndex -= previousLength - formatted.length();
        }

        editText.setSelection(Math.min(selectionIndex, formatted.length()));
        isUpdating = false;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
