package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import Adapters.ListTodoAdapter;
import Objects.ListObject;

public class HomePage extends AppCompatActivity {

    private Toolbar toolbar;

    private List<ListObject> persistentList;
    private RecyclerView persistentRecycler;
    private ListTodoAdapter persistentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        assignUIcomponents();

        loadPersistentList();

        loadFromDB();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }


    private void assignUIcomponents(){
        //set Toolbar and toolbar title
        toolbar = findViewById(R.id.homepage_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getSharedPreferences("user_data", MODE_PRIVATE).getString("username", "To Do"));

        persistentList = new ArrayList<>();

        persistentRecycler = findViewById(R.id.homepage_persistent_todo_recycler);
        persistentRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        persistentAdapter = new ListTodoAdapter(getApplicationContext(), R.layout.list_todo, persistentList);
        persistentRecycler.setAdapter(persistentAdapter);
    }


    private void loadPersistentList(){
        persistentList.add(new ListObject(1, "Today", R.drawable.icon_today, false));
        persistentList.add(new ListObject(2, "Important", R.drawable.icon_star, false));
        persistentAdapter.notifyDataSetChanged();
    }


    //read username from shared preferences and set toolbar title = username
    private void loadFromDB(){}

}
