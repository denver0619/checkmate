package com.gdiff.checkmate.views.activities;

import android.view.MenuItem;

import androidx.annotation.NonNull;

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
}
