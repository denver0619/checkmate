package com.gdiff.checkmate;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gdiff.checkmate.databinding.ActivityMainBinding;
import com.gdiff.checkmate.presentation.activities.BaseActivity;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends BaseActivity {
    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // view binding
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivityViewPagerAdapter mainActivityViewPagerAdapter = new MainActivityViewPagerAdapter(this);
        activityMainBinding.mainViewPager.setUserInputEnabled(false); // Disables swiping
        activityMainBinding.mainViewPager.setAdapter(mainActivityViewPagerAdapter);
        activityMainBinding.mainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                activityMainBinding.mainNavigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                activityMainBinding.mainNavigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        activityMainBinding.mainNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuItemId = menuItem.getItemId();

                if (menuItemId == R.id.nav_item_todo) {
                    activityMainBinding.mainViewPager.setCurrentItem(0);
                } else if (menuItemId == R.id.nav_item_scheduled) {
                    activityMainBinding.mainViewPager.setCurrentItem(1);
                } else if (menuItemId == R.id.nav_item_repeating) {
                    activityMainBinding.mainViewPager.setCurrentItem(2);
                }
                return true;
            }
        });
    }
}

