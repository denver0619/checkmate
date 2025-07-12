package com.gdiff.checkmate.presentation.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo: theme handle before super
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //todo: handle ui changes if the activity is alive
    }
}
