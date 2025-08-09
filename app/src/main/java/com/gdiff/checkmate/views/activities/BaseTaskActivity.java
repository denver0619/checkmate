package com.gdiff.checkmate.views.activities;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.gdiff.checkmate.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Comparators;

public class BaseTaskActivity extends BaseActivity{
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            // TODO: handle discard of input
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isTextFieldEmpty(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message, int resId) {
        boolean result = true;
        if (textInputEditText.getText()!=null) {
            if (textInputEditText.getText().toString().isEmpty()) {
                result = false;
                textInputLayout.setError(message);
                textInputLayout.setErrorIconDrawable(resId);
            }
            else {
                result = true;
                textInputLayout.setError(null);
                textInputLayout.setErrorIconDrawable(null);
            }
        } else {
            result = false;
            textInputLayout.setError(message);
            textInputLayout.setErrorIconDrawable(resId);
        }
        return result;
    }

    protected boolean isIntervalValid(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message, int resId, CustomComparator comparator, int comparison) {
        boolean result;
        if (!TextUtils.isEmpty(textInputEditText.getText())) {
            if (!textInputEditText.getText().toString().isEmpty()) {
                if (comparator == CustomComparator.LESS_THAN) {
                    if (Integer.parseInt(textInputEditText.getText().toString()) < comparison) {
                        result = false;
                        textInputLayout.setError(message);
                        textInputLayout.setErrorIconDrawable(resId);
                    }
                    else {
                        result = true;
                        textInputLayout.setError(null);
                        textInputLayout.setErrorIconDrawable(null);
                    }
                } else if (comparator == CustomComparator.LESS_THAN_OR_EQUAL_TO) {
                    if (Integer.parseInt(textInputEditText.getText().toString()) <= comparison) {
                        result = false;
                        textInputLayout.setError(message);
                        textInputLayout.setErrorIconDrawable(resId);
                    }
                    else {
                        result = true;
                        textInputLayout.setError(null);
                        textInputLayout.setErrorIconDrawable(null);
                    }
                } else if (comparator == CustomComparator.GREATER_THAN) {
                    if (Integer.parseInt(textInputEditText.getText().toString()) > comparison) {
                        result = false;
                        textInputLayout.setError(message);
                        textInputLayout.setErrorIconDrawable(resId);
                    }
                    else {
                        result = true;
                        textInputLayout.setError(null);
                        textInputLayout.setErrorIconDrawable(null);
                    }
                } else if (comparator == CustomComparator.GREATER_THAN_OR_EQUAL_TO) {
                    if (Integer.parseInt(textInputEditText.getText().toString()) >= comparison) {
                        result = false;
                        textInputLayout.setError(message);
                        textInputLayout.setErrorIconDrawable(resId);
                    }
                    else {
                        result = true;
                        textInputLayout.setError(null);
                        textInputLayout.setErrorIconDrawable(null);
                    }
                } else if (comparator == CustomComparator.EQUAL_TO) {
                    if (Integer.parseInt(textInputEditText.getText().toString()) == comparison) {
                        result = false;
                        textInputLayout.setError(message);
                        textInputLayout.setErrorIconDrawable(resId);
                    }
                    else {
                        result = true;
                        textInputLayout.setError(null);
                        textInputLayout.setErrorIconDrawable(null);
                    }
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    protected enum CustomComparator {
        LESS_THAN,
        LESS_THAN_OR_EQUAL_TO,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL_TO,
        EQUAL_TO
    }
}
