package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import Adapters.IconSelectorAdapter;
import Adapters.ListTodoAdapter;
import Adapters.ThemeSelectorAdapter;
import AlarmHelpers.AlarmReceiver;
import Database.DatabaseManager;
import Objects.ConstantsDB;
import Objects.ListObject;
import Objects.Reminder;
import Objects.ThemeObject;

public class HomePage extends AppCompatActivity {

    private Toolbar toolbar;
    private static Context staticContext;

    private static List<ListObject> persistentList;
    private static List<ListObject> dynamicList;

    private RecyclerView persistentRecycler;    //pre-loaded list recycler
    private RecyclerView dynamicRecycler;       //user added list recycler

    private ListTodoAdapter persistentAdapter;
    private static ListTodoAdapter dynamicAdapter;

    public static String SelectedIcon;
    public static int SelectedTheme;
    private static ImageView selectedIconImage;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        staticContext = getApplicationContext();

        assignUIcomponents();

        loadFromDB();

        create_notification_channel();

        //set_daily_today_alarm();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btnMenu_add_todo_list:
                add_todo_list_popup();
                return true;
            case R.id.btnChangeUsername:
                change_username_popup();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void assignUIcomponents() {
        //set Toolbar and toolbar title
        toolbar = findViewById(R.id.homepage_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getSharedPreferences("user_data", MODE_PRIVATE).getString("username", "To Do"));

        persistentList = new ArrayList<>();
        dynamicList = new ArrayList<>();

        //setup persistent list's recycler
        persistentRecycler = findViewById(R.id.homepage_persistent_todo_recycler);
        persistentRecycler.setLayoutManager(new LinearLayoutManager(HomePage.this));

        //setup dynamic list's recycler
        dynamicRecycler = findViewById(R.id.homepage_dynamic_todo_recycler);
        dynamicRecycler.setLayoutManager(new LinearLayoutManager(HomePage.this));
        new ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(dynamicRecycler);

        //adapter for persistent list
        persistentAdapter = new ListTodoAdapter(HomePage.this, R.layout.list_todo, persistentList);
        persistentRecycler.setAdapter(persistentAdapter);

        //adapter for dynamic list
        dynamicAdapter = new ListTodoAdapter(HomePage.this, R.layout.list_todo, dynamicList);
        dynamicRecycler.setAdapter(dynamicAdapter);
    }


    public void add_todo_list_popup() {
        //inflating popup layout and assigning views
        view = LayoutInflater.from(HomePage.this).inflate(R.layout.popup_add_todo_list, null);
        selectedIconImage = view.findViewById(R.id.popup_addList_select_icon);
        final EditText listName = view.findViewById(R.id.popup_addList_list_name);

        //creating ALERT DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();


        //setting up THEME RecyclerView
        RecyclerView themeRecycler = view.findViewById(R.id.popup_addList_theme_recycler);
        themeRecycler.setLayoutManager(new LinearLayoutManager(HomePage.this, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) themeRecycler.getItemAnimator()).setSupportsChangeAnimations(false);


        //THEME recycler caching
        themeRecycler.setHasFixedSize(true);
        themeRecycler.setItemViewCacheSize(40);
        themeRecycler.setDrawingCacheEnabled(true);
        themeRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);


        //setting up ICON RecyclerView
        RecyclerView iconRecycler = view.findViewById(R.id.popup_addList_icon_recycler);
        iconRecycler.setLayoutManager(new LinearLayoutManager(HomePage.this, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) iconRecycler.getItemAnimator()).setSupportsChangeAnimations(false);


        //Theme Recycler ADAPTER
        ThemeSelectorAdapter themeAdapter = new ThemeSelectorAdapter(HomePage.this, R.layout.list_theme_selector, ConstantsDB.getThemeObjectsList());
        themeRecycler.setAdapter(themeAdapter);


        //Icon Recycler ADAPTER
        IconSelectorAdapter iconAdapter = new IconSelectorAdapter(HomePage.this, R.layout.list_icon_selector, ConstantsDB.getIconsList());
        iconRecycler.setAdapter(iconAdapter);


        //CANCEL button click listeners here
        view.findViewById(R.id.popup_addList_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        //DONE button click listener
        view.findViewById(R.id.popup_addList_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listName.getText().toString().equals("")) {
                    //add list to database
                    DatabaseManager db = new DatabaseManager(HomePage.this);
                    int id = db.AddListToDB(listName.getText().toString().trim(), SelectedIcon, getResources().getResourceName(SelectedTheme), false);
                    db.close();

                    dynamicList.add(new ListObject(id, listName.getText().toString().trim(), getResources().getIdentifier(SelectedIcon, "drawable", getApplicationContext().getPackageName()), false));
                }
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void change_username_popup(){
        //inflating the change username pop out layout
        View viewLocal;
        viewLocal = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_change_username,null);
        final EditText etNewUsername = viewLocal.findViewById(R.id.popup_change_username);
        final String currentTitle = getSupportActionBar().getTitle().toString();

        etNewUsername.setText(currentTitle);

        //creating ALERT DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        builder.setView(viewLocal);
        final AlertDialog alertDialog = builder.create();

        // Cancel button click listener
        viewLocal.findViewById(R.id.popup_change_username_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        // Change button click listener
        viewLocal.findViewById(R.id.popup_change_username_done_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etNewUsername.getText().toString().isEmpty()){
                    // changing the username
                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    prefs.edit().putString("username", etNewUsername.getText().toString().trim()).apply();
                    getSupportActionBar().setTitle(getSharedPreferences("user_data", MODE_PRIVATE).getString("username", "To Do"));
                    alertDialog.cancel();
                }
            }
        });
        alertDialog.show();
    }


    public static void setSelectedIcon(String iconName) {
        selectedIconImage.setImageResource(staticContext.getResources().getIdentifier(iconName, "drawable", staticContext.getPackageName()));
        SelectedIcon = iconName;
    }


    public static void setSelectedTheme(int themeRes) {
        SelectedTheme = themeRes;
    }

    //read username from shared preferences and set toolbar title = username
    private void loadFromDB() {
        //load persistent list
        persistentList.add(new ListObject(-200,"Today", R.drawable.icon_today, false));
        persistentList.add(new ListObject(-400, "Tomorrow", R.drawable.icon_tomorrow, false));
        persistentList.add(new ListObject(-600, "Important", R.drawable.icon_star, false));
        persistentAdapter.notifyDataSetChanged();

        //load user added lists
        DatabaseManager db = new DatabaseManager(HomePage.this);
        Cursor res = db.getAllLists();

        ListObject object;
        while (res.moveToNext()) {
            object = new ListObject(res.getInt(0), res.getString(1), getResources().getIdentifier(res.getString(2), "drawable", getPackageName()), Boolean.parseBoolean(res.getInt(4) + ""));
            dynamicList.add(object);
        }
        dynamicAdapter.notifyDataSetChanged();
    }

    //refresh titles
    public static void refreshTitle(int ID, String newTitle) {
        for (int i = 0; i < dynamicList.size(); i++) {
            if (dynamicList.get(i).getId() == ID) {
                dynamicList.get(i).setName(newTitle);
                dynamicAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    //remove deleted list
    public static void listDeleted(int ID){
        for (int i = 0; i < dynamicList.size(); i++) {
            if (dynamicList.get(i).getId() == ID) {
                dynamicList.remove(i);
                dynamicAdapter.notifyItemRemoved(i);
                dynamicAdapter.notifyItemRangeChanged(i,dynamicList.size());
                break;
            }
        }
    }

    //setup notification channel
    private void create_notification_channel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Due Tasks", "Due Tasks", importance);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void set_daily_today_alarm(){
        AlarmReceiver dailyAlarm = new AlarmReceiver();
        dailyAlarm.setAlarm(getBaseContext(), new Reminder(), -200);
    }

    private ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseManager db = new DatabaseManager(getBaseContext());
            // remove list from database
            db.removeListAndChildTasks(dynamicList.get(viewHolder.getAdapterPosition()).getId());
            dynamicList.remove(viewHolder.getAdapterPosition());
            dynamicAdapter.notifyItemRemoved(viewHolder.getPosition());
        }
    };
}