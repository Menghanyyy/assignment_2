package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    //MAPBOX TOKEN = sk.eyJ1IjoiYWRyaWFudGVvMTEyMSIsImEiOiJjbG1uZXU3bzQwMmRtMmtwMmQ3cWV5d2M2In0.9ddhigLDMQFkY_Inz6f_Vw

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = (TextView)findViewById(R.id.userName);
        TextView password = (TextView)findViewById(R.id.password);

        MaterialButton loginBt= (MaterialButton)findViewById(R.id.signInButton);
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("admin") &&
                        password.getText().toString().equals("123")){
                    //Toast.makeText(MainActivity.this, "Successful Login", Toast.LENGTH_LONG).show();
                    TextView password = (TextView)findViewById(R.id.password);
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                }else{
                    Toast.makeText(Login.this, "Failed Login", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView textView= findViewById(R.id.register);
        String text = "Don't have an account? Sign Up";
        SpannableString ss= new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(Login.this, SignUP.class);
                startActivity(i);
            }
        };
        ss.setSpan(clickableSpan1,23,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        Button mapView_Btn = (Button) findViewById(R.id.mapView_Btn);

        mapView_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MapActivity.class));
            }
        });

        Button editable_mapView_Btn = (Button) findViewById(R.id.editable_mapview_btn);

        editable_mapView_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Login.this, EditableMapActivity.class));
            }
        });

//        new dbTesting().runTests(this);
    }
}