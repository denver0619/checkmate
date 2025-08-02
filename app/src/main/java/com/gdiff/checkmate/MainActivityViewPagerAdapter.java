package com.gdiff.checkmate;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gdiff.checkmate.views.fragments.main.ScheduledTaskFragment;
import com.gdiff.checkmate.views.fragments.main.TodoTaskFragment;

public class MainActivityViewPagerAdapter extends FragmentStateAdapter {

    public MainActivityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment result = null;

        switch (position) {
            case 0:
                result = new TodoTaskFragment();
                break;
            case 1:
                //placeHolder
                result = new ScheduledTaskFragment();
                break;
            case 2:
                //placeHolder
                result = new TodoTaskFragment();
                break;
            default:
                //TODO: handle exception
                throw new IllegalArgumentException("Invalid position: " + position);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
