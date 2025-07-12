package com.gdiff.checkmate.presentation.activities.todotask;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.databinding.ActivityTodoTaskAddBinding;
import com.gdiff.checkmate.presentation.activities.BaseActivity;

import java.util.Objects;

public class TodoTaskAddActivity extends BaseActivity {
    ActivityTodoTaskAddBinding activityTodoTaskAddBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityTodoTaskAddBinding = ActivityTodoTaskAddBinding.inflate(getLayoutInflater());
        setContentView(activityTodoTaskAddBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setSupportActionBar(activityTodoTaskAddBinding.todoTaskToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

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