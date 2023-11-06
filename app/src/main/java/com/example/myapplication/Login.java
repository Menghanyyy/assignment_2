package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class Login extends AppCompatActivity {

    //MAPBOX TOKEN = sk.eyJ1IjoiYWRyaWFudGVvMTEyMSIsImEiOiJjbG1uZXU3bzQwMmRtMmtwMmQ3cWV5d2M2In0.9ddhigLDMQFkY_Inz6f_Vw
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseManager = new DatabaseManager(this);


        TextView username = (TextView)findViewById(R.id.userName);
        TextView password = (TextView)findViewById(R.id.password);

        MaterialButton loginBt = (MaterialButton)findViewById(R.id.signUpButton);
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseManager.verifyPassword(password.getText().toString().trim(),username.getText().toString().trim(), new DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        databaseManager.getUserByID(Integer.parseInt(result), new DatabaseCallback<GeneralUser>() {
                            @Override
                            public void onSuccess(GeneralUser returnedUser) {
                                Log.i("get User by ID", returnedUser.getUserId());

                                MyApplication.setCurrentUser(returnedUser);

                                Intent i = new Intent(Login.this, Home.class);
                                startActivity(i);
                            }

                            @Override
                            public void onError(String error) {
                                Log.println(Log.ASSERT, "Error getting user", error);
                            }
                        });

                    }

                    @Override
                    public void onError(String error) {
                        Log.println(Log.ASSERT, "error verifying", error);

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.customise_toast, null, false);

                        TextView text = layout.findViewById(R.id.toast_text);
                        text.setText("Invalid password or user ID");

                        Toast toast = new Toast(Login.this);
                        toast.setView(layout);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

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
                Log.println(Log.ASSERT, "Signup button", "going to sign up");
            }
        };
        ss.setSpan(clickableSpan1,23,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.Root_Sign_In).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager im = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
//        new dbTesting().runTests(this);
    }
}