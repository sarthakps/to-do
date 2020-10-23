package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.ListItemTaskAdapter;
import Adapters.ThemeChangerAdapter;
import AlarmHelpers.AlarmReceiver;
import Database.BaseDatabase;
import Database.DatabaseManager;
import Objects.ConstantsDB;
import Objects.Reminder;
import Objects.TaskObject;
import Objects.ThemeObject;

public class ListActivity extends AppCompatActivity {

    private Intent intent;
    private static Context mContext;
    private View view_change_theme, view_add_task;
    private static Toolbar toolbar;
    private static CollapsingToolbarLayout collapsingToolbar;

    private RecyclerView tasksRecycler;
    private ListItemTaskAdapter tasksListAdapter;

    private static ImageView parentBackground;
    private static ImageView floatingButton;

    private static int themeRes, listID;
    private List<TaskObject> taskObjectList = new ArrayList<>();
    private static String date = "0", time = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Slidr.attach(this);

        mContext = this;

        //setup status bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        intent = getIntent();

        listID = intent.getIntExtra(BaseDatabase.TASKS_PARENT_ID, 1);

        assignUIcomponents();

        loadTasksFromDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.todos_change_theme:
                change_theme_popup();
                break;

            case R.id.todos_rename_list:
                rename_list_popup();
                break;

            case R.id.todos_delete_list:
                delete_list_popup();
                break;
        }
        return true;
    }

    private void assignUIcomponents() {
        toolbar = findViewById(R.id.todos_toolbar);
        collapsingToolbar = findViewById(R.id.todos_collapsing_toolbar);

        toolbar = findViewById(R.id.todos_toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setTitle(intent.getStringExtra("title"));

        // load theme name from database using ID from intentExtra and set it as background
        DatabaseManager db = new DatabaseManager(ListActivity.this);
        String theme = db.getListTheme(listID);
        themeRes = getResources().getIdentifier(theme, "drawable", getApplicationContext().getPackageName());
        parentBackground = findViewById(R.id.todos_parentBackground);

        floatingButton = findViewById(R.id.todos_add_task_floatBtn);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_task_popup();
                findViewById(R.id.todos_add_task_floatBtn).animate().rotation(60).setDuration(400).start();
            }
        });

        setBackground(themeRes);

        // setup Recycler View
        tasksRecycler = findViewById(R.id.todos_tasks_recycler);
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));

        // setup Adapter for Recycler View
        tasksListAdapter = new ListItemTaskAdapter(this, R.layout.item_task, taskObjectList);
        tasksRecycler.setAdapter(tasksListAdapter);

        // swipe to delete for Recycler View
        new ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(tasksRecycler);
    }

    private void loadTasksFromDatabase() {
        DatabaseManager db = new DatabaseManager(getBaseContext());
        Cursor cursor = db.getAllTasksUnderList(listID);

        TaskObject task;

        while (cursor.moveToNext()) {
            task = new TaskObject(cursor.getString(1), "1".equals(cursor.getString(2)), "1".equals(cursor.getString(4)));
            task.setMarkedImportant("1".equals(cursor.getString(6)));
            task.setID(cursor.getInt(0));
            task.setDate(cursor.getString(4));
            task.setTime(cursor.getString(5));
            taskObjectList.add(task);
        }

        tasksListAdapter.notifyDataSetChanged();
    }

    public void change_theme_popup() {
        view_change_theme = LayoutInflater.from(ListActivity.this).inflate(R.layout.popup_theme_change, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setView(view_change_theme);

        AlertDialog alertDialog = builder.create();

        //set popup gravity to bottom
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        RecyclerView themeChangeRecycler = view_change_theme.findViewById(R.id.list_theme_change_recycler);
        themeChangeRecycler.setLayoutManager(new LinearLayoutManager(ListActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) themeChangeRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        themeChangeRecycler.setHasFixedSize(true);
        themeChangeRecycler.setItemViewCacheSize(40);
        themeChangeRecycler.setDrawingCacheEnabled(true);
        themeChangeRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        ThemeChangerAdapter adapter = new ThemeChangerAdapter(ListActivity.this, R.layout.list_theme_selector, ConstantsDB.getThemeObjectsList());
        themeChangeRecycler.setAdapter(adapter);

        int currentThemePosition = adapter.selectCurrentTheme(themeRes);
        themeChangeRecycler.scrollToPosition(currentThemePosition - 2);

        adapter.setID(listID);

        alertDialog.show();
    }

    public void delete_list_popup(){
        //inflating the delete list pop out layout
        View view_delete_list;
        view_delete_list = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_delete_list,null);

        //creating ALERT DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setView(view_delete_list);
        final AlertDialog alertDialog = builder.create();

        // Cancel button click listener
        view_delete_list.findViewById(R.id.popup_delete_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        // Confirm button click listener
        view_delete_list.findViewById(R.id.popup_delete_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatabaseManager(getBaseContext()).removeListAndChildTasks(listID);
                HomePage.listDeleted(listID);
                finish();
            }
        });

        alertDialog.show();
    }

    public void add_task_popup() {
        view_add_task = LayoutInflater.from(ListActivity.this).inflate(R.layout.popup_add_todo_task, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setView(view_add_task);

        final AlertDialog dialog = builder.create();

        final TextView dateTimeStaticTextView = view_add_task.findViewById(R.id.popup_add_task_pick_date_static_text);
        final TextView clearDueDate = view_add_task.findViewById(R.id.popup_add_task_clear_duedate);
        final EditText taskDescription = view_add_task.findViewById(R.id.popup_add_task_taskDesc);

        String date, time;

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                findViewById(R.id.todos_add_task_floatBtn).animate().rotation(0).setDuration(400).start();
            }
        });

        view_add_task.findViewById(R.id.popup_add_task_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_task_to_database(taskDescription.getText().toString());
                dialog.dismiss();
            }
        });

        view_add_task.findViewById(R.id.popup_add_task_pick_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_time_popup(dateTimeStaticTextView, clearDueDate);
            }
        });

        view_add_task.findViewById(R.id.popup_add_task_pick_date_static_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_time_popup(dateTimeStaticTextView, clearDueDate);
            }
        });

        clearDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDateTimeGlobal();
                dateTimeStaticTextView.setText("Pick a date");
                clearDueDate.setVisibility(View.GONE);
            }
        });

        dialog.show();
    }

    private void date_time_popup(final TextView dateTimeTextView, final TextView clearDueDate) {
        final View view_datetime_popup = LayoutInflater.from(ListActivity.this).inflate(R.layout.date_time_picker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setView(view_datetime_popup);

        final AlertDialog dialog = builder.create();

        final DatePicker datePicker = view_datetime_popup.findViewById(R.id.popup_datetime_date_picker);

        //set date picker's date to tomorrow
        view_datetime_popup.findViewById(R.id.popup_datetime_tomorrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, 1);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
        });

        //set date picker's date to today
        view_datetime_popup.findViewById(R.id.popup_datetime_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
        });

        final TimePicker timePicker = view_datetime_popup.findViewById(R.id.popup_datetime_time_picker);
        timePicker.setCurrentHour(12);
        timePicker.setCurrentMinute(0);

        //set time picker's time to 9 am
        view_datetime_popup.findViewById(R.id.popup_datetime_nine_am).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setCurrentHour(9);
            }
        });

        //set time picker's time to 12 pm
        view_datetime_popup.findViewById(R.id.popup_datetime_twelve_pm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setCurrentHour(12);
            }
        });

        //set time picker's time to 3 pm
        view_datetime_popup.findViewById(R.id.popup_datetime_three_pm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setCurrentHour(15);
            }
        });

        //set time picker's time to 6 pm
        view_datetime_popup.findViewById(R.id.popup_datetime_six_pm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setCurrentHour(18);
            }
        });

        //done button listener
        view_datetime_popup.findViewById(R.id.popup_datetime_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                datetime.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                String am_pm;
                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else
                    am_pm = "PM";

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                date = day + "-" + month + "-" + year;
                time = hour + ":" + minute;

                if (hour > 11) {
                    hour -= 12;
                }

                if (minute > 9) {
                    dateTimeTextView.setText(day + "-" + month + "-" + year + "  " + hour + ":" + minute + " " + am_pm);
                } else {
                    dateTimeTextView.setText(day + "-" + month + "-" + year + "  " + hour + ":" + "0" + minute + " " + am_pm);
                }

                clearDueDate.setVisibility(View.VISIBLE);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void rename_list_popup() {

        final String currentTitle = collapsingToolbar.getTitle().toString();

        View view_rename_list = LayoutInflater.from(getBaseContext()).inflate(R.layout.popup_rename_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setView(view_rename_list);

        AlertDialog alertDialog = builder.create();

        final EditText renameList = view_rename_list.findViewById(R.id.popup_rename_list_edittext);
        renameList.setText(currentTitle);

        renameList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                collapsingToolbar.setTitle(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alertDialog.show();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (renameList.getText().equals("")) {
                    collapsingToolbar.setTitle(currentTitle);
                } else if (renameList.getText().toString().trim().equals(currentTitle)) {
                    //do nothing
                } else {
                    //update new title in database
                    DatabaseManager db = new DatabaseManager(getBaseContext());
                    db.renameList(listID, renameList.getText().toString().trim());
                    db.close();
                    HomePage.refreshTitle(listID, renameList.getText().toString().trim());
                }
            }
        });
    }

    private void add_task_to_database(String taskDescription) {
        if (!taskDescription.isEmpty()) {
            TaskObject task = new TaskObject(taskDescription, false, Boolean.parseBoolean(time));

            String[] dateData = date.split("-");
            String[] timeData = time.split(":");

            if (dateData.length > 1) {
                task.setDay(Integer.parseInt(dateData[0]));
                task.setMonth(Integer.parseInt(dateData[1]));
                task.setYear(Integer.parseInt(dateData[2]));
            }

            if (timeData.length > 1) {
                task.setHour(Integer.parseInt(timeData[0]));
                task.setMinute(Integer.parseInt(timeData[1]));
            }

            DatabaseManager db = new DatabaseManager(getBaseContext());
            int taskID = db.AddTaskToDatabase(taskDescription, false, listID, date, time);
            task.setID(taskID);

            taskObjectList.add(task);
            tasksListAdapter.notifyItemInserted(taskObjectList.size() - 1);

            if (!date.equals("0") && !time.equals("0")) {
                setAlarm(task);
            }
        }

        //reset global date-time variables to not interfere with consecutive next task being added
        date = "0";
        time = "0";
    }

    private void setAlarm(TaskObject task) {
        Reminder reminder = new Reminder(task.getID(), task.getDay(), task.getMonth(), task.getYear(), task.getHour(), task.getMinute(), task.getTaskDescription());

        new AlarmReceiver().setAlarm(getBaseContext(), reminder, listID);
        Log.e("NOTIF: ", "1");
    }

    private void resetDateTimeGlobal() {
        date = "0";
        time = "0";
    }

    public static void setBackground(int imageRes) {
        themeRes = imageRes;
        parentBackground.setImageResource(imageRes);

        int accentPrimary = mContext.getResources().getColor(new ThemeObject(themeRes, false).getPrimaryAccentForTheme());
        int accentSecondary =  mContext.getResources().getColor(new ThemeObject(themeRes, false).getSecondaryAccentForTheme());
        collapsingToolbar.setCollapsedTitleTextColor(accentPrimary);
        collapsingToolbar.setExpandedTitleColor(accentPrimary);

        toolbar.getOverflowIcon().setColorFilter(accentPrimary, PorterDuff.Mode.SRC_ATOP);

        floatingButton.getDrawable().setColorFilter(accentSecondary,  PorterDuff.Mode.SRC_ATOP);
    }

    private ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseManager db = new DatabaseManager(getBaseContext());
            db.removeTask(taskObjectList.get(viewHolder.getAdapterPosition()).getID());
            db.close();
            new AlarmReceiver().cancelAlarm(getBaseContext(), taskObjectList.get(viewHolder.getAdapterPosition()).getID());
            taskObjectList.remove(viewHolder.getAdapterPosition());
            tasksListAdapter.notifyItemRemoved(viewHolder.getPosition());
        }
    };
}