package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.button.MaterialButton;

public class NewLink extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_link);

        DatabaseManager databaseManager = new DatabaseManager(this);

        MaterialButton insertBt= (MaterialButton)findViewById(R.id.insertButton);

        ImageView backBtn = findViewById(R.id.iv_back);

        TextView linkField = findViewById(R.id.link);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        insertBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link = linkField.getText().toString();

                databaseManager.joinEvent(Login.currentUser.getUserId(), link, new DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.i("join event success", result.toString());

                        Intent i = new Intent(NewLink.this, Home.class);
                        startActivity(i);
                    }

                    @Override
                    public void onError(String error) {
                        Log.println(Log.ASSERT, "Error joining", error);
                    }
                });


            }
        });
    }
}