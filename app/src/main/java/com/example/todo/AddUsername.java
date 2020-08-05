package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class AddUsername extends AppCompatActivity {

    private ImageView todoIconStatic;
    private EditText username;
    private TextView btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_username);

        isUserAlreadyRegistered();

        assignUIcomponents();

        //elements slide in from off-screen
        introAnimation();

        buttonListeners();
    }

    private void isUserAlreadyRegistered(){

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String username = "";
        if(!prefs.getString("username", "").equals("")){
            Intent intent = new Intent(AddUsername.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    private void assignUIcomponents() {
        todoIconStatic = findViewById(R.id.add_username_todo_icon_static);
        username = findViewById(R.id.add_username_input);
        btnDone = findViewById(R.id.add_username_done_btn);
    }

    private void introAnimation() {
        Animation slide_in_from_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_top);
        Animation slide_in_from_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left);
        Animation slide_in_from_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_bottom);

        todoIconStatic.startAnimation(slide_in_from_top);
        btnDone.startAnimation(slide_in_from_bottom);
        username.startAnimation(slide_in_from_left);
    }

    private void buttonListeners() {
        //button(textView) to submit username pressed.
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().equals("")) {
                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    prefs.edit().putString("username", username.getText().toString().trim()).apply();
                    Intent intent = new Intent(AddUsername.this, HomePage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}