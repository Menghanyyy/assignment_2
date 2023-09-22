package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView
        displayText = findViewById(R.id.textView);
    }

    public void printToConsole(View view) {
        // Start an AsyncTask to make the HTTP request in the background
        new HttpRequestTask().execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Perform your HTTP request here
                // Replace this URL with your actual URL
                String url = "http://192.168.0.209:8080/visits/userCount/1";

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read and process the response here
                    InputStream inputStream = conn.getInputStream();
                    // ... Process the response data
                    String response = convertStreamToString(inputStream);

                    return "HTTP request successful: " + response;
                } else {
                    return "HTTP request failed with response code: " + responseCode;
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // This method runs on the main thread and allows you to update the UI
            Log.d("HTTP Request Result", result);

            // Set the result in the displayText TextView
            displayText.setText(result);
        }

        private String convertStreamToString(InputStream stream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }
}
