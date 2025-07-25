package com.gdiff.checkmate.presentation.fragments.main;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.Collections;
import java.util.List;

public class TasksSwipeCallback extends ItemTouchHelper.Callback {
    private final TasksSwipeActionCallback _tasksSwipeActionCallback;
    private final Activity _activity;

    public interface TasksSwipeActionCallback {
        void actionCallback(TaskModel taskModel, RecyclerView.ViewHolder viewHolder);
    }

    public TasksSwipeCallback(@NonNull Activity activity, TasksSwipeActionCallback tasksSwipeActionCallback) {
        this._activity = activity;
        this._tasksSwipeActionCallback = tasksSwipeActionCallback;

    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        TasksAdapter adapter = (TasksAdapter) recyclerView.getAdapter();
        List<TasksAdapterViewType> listViewTypes = Collections.emptyList();
        if (adapter != null) {
            listViewTypes = adapter.getViewTypeList();
        }
        if (!listViewTypes.isEmpty()) {
            if (listViewTypes.get(viewHolder.getAdapterPosition()).getType() == TasksAdapterViewType.TASK_ITEM_UNFINISHED
            || listViewTypes.get(viewHolder.getAdapterPosition()).getType() == TasksAdapterViewType.TASK_ITEM_FINISHED
            || listViewTypes.get(viewHolder.getAdapterPosition()).getType() == TasksAdapterViewType.TASK_ITEM_EXPIRED) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }
            else {
                return makeMovementFlags(0, 0);
            }
        }
        else {
            return makeMovementFlags(0, 0);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            //TODO: launch delete intent
            _tasksSwipeActionCallback.actionCallback(
                    ((TasksAdapter.ItemViewHolder) viewHolder)
                            .getTaskModel(),
                    viewHolder
            );
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags,layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentActive
                            ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View foregroundView = ((TasksAdapter.ItemViewHolder) viewHolder).getBinding().fgCard;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            _activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            foregroundView.setTranslationX(Math.max((-screenWidth)*.35f, dX)); // 50% swipe max limit
        }

//        not calling the super will cause problems on reset so edit in adapter the initial position in onBindViewHolder()
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentActive);
    }

    //Swipe Trigger
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.25f; // 30% swipe before triggering
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        ((TasksAdapter.ItemViewHolder) viewHolder).getBinding().bgCard.setTranslationX(0f);
    }

}
