package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.button.MaterialButton;

public class SignUP extends AppCompatActivity {

    private DatabaseManager databaseManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseManager = new DatabaseManager(this);

        TextView usernameView = (TextView)findViewById(R.id.username);
        TextView emailView = (TextView)findViewById(R.id.email);
        TextView passwordView = (TextView)findViewById(R.id.password);
        TextView nameView = (TextView)findViewById(R.id.name);

        MaterialButton signUpBt = (MaterialButton)findViewById(R.id.signUpButton);
        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralUser newUser = new GeneralUser(
                        null,
                        usernameView.getText().toString(),
                        emailView.getText().toString(),
                        passwordView.getText().toString(),
                        nameView.getText().toString()
                );
                databaseManager.addUser(newUser, new DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            Integer userID = Integer.parseInt(result);

                            // Update ID since it is created by the DB
                            newUser.setUserId(result);

                            Log.i("On success (User ID)", String.valueOf(userID));
                            Intent i = new Intent(SignUP.this, Home.class);
                            startActivity(i);
                        }
                        catch (Exception e){
                            Log.i("User bad string", result);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.println(Log.ASSERT, "Error adding user", error);


                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.customise_toast, null, false);

                        TextView text = layout.findViewById(R.id.toast_text);
                        text.setText("Failed To Sign Up");

                        Toast toast = new Toast(SignUP.this);
                        toast.setView(layout);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        });

        TextView textView= findViewById(R.id.forget);
        String text = "Already have an account? Log In";
        SpannableString ss= new SpannableString(text);

//        MaterialButton signupButton =

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(SignUP.this, Login.class);
                startActivity(i);
            }
        };
        ss.setSpan(clickableSpan1,25,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.Root_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager im = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
            }
        });
    }
}