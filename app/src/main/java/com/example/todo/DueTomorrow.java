package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.ListItemTaskAdapter;
import Database.DatabaseManager;
import Objects.TaskObject;

public class DueTomorrow extends AppCompatActivity {

    List<TaskObject> tasksList;

    RecyclerView tasksRecycler;
    ListItemTaskAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_tomorrow);

        assignUIcomponents();

        loadFromDB();
    }

    private void assignUIcomponents() {
        tasksList = new ArrayList<>();

        tasksRecycler = findViewById(R.id.dueTomorrow_tasks_recycler);
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));

        tasksAdapter = new ListItemTaskAdapter(getBaseContext(), R.layout.item_task, tasksList);
        tasksRecycler.setAdapter(tasksAdapter);

        new ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(tasksRecycler);
    }

    private void loadFromDB() {

        Calendar c = Calendar.getInstance();

        c.add(Calendar.DAY_OF_YEAR, 1);

        String date = c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.YEAR);

        DatabaseManager db = new DatabaseManager(getApplicationContext());
        Cursor cursor = db.getTasksDueToday(date);

        TaskObject task;

        if (cursor.moveToFirst()) {
            do {
                // your code like get columns
                task = new TaskObject(cursor.getString(1), "1".equals(cursor.getString(2)), "1".equals(cursor.getString(4)));
                task.setID(cursor.getInt(0));
                task.setMarkedImportant("1".equals(cursor.getString(6)));
                task.setDate("0");
                task.setTime("0");
                tasksList.add(task);
                Log.e("LOOPING", "123456...");
            }
            while (cursor.moveToNext());
        }

        Log.e("tasks size", "today_size: " + tasksList.size());
        if (tasksList.size() > 0) {
            findViewById(R.id.dueTomorrow_hooray).setVisibility(View.GONE);
            findViewById(R.id.dueTomorrow_no_due_today_static_text).setVisibility(View.GONE);
            tasksAdapter.notifyDataSetChanged();
        }
    }

    private ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseManager db = new DatabaseManager(getBaseContext());
            db.removeTask(tasksList.get(viewHolder.getAdapterPosition()).getID());
            tasksList.remove(viewHolder.getAdapterPosition());
            tasksAdapter.notifyItemRemoved(viewHolder.getPosition());
        }
    };
}