package com.ghasty.mobilecontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText appNameEditText;
    private Button launchApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appNameEditText = findViewById(R.id.app_name_edit_text);
        launchApp = findViewById(R.id.launch_app_btn);

        launchApp.setOnClickListener(view -> {
            try {
                if (appNameEditText.getText().toString().equals("")) {
                    Toast.makeText(this, "Input app name.", Toast.LENGTH_LONG).show();
                    return;
                }
                runApp(appNameEditText.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void runApp(String appName) throws Exception {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        for (ResolveInfo info : getPackageManager().queryIntentActivities(mainIntent, 0)) {
            if (info.loadLabel(getPackageManager()).equals(appName)) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(info.activityInfo.applicationInfo.packageName);
                startActivity(launchIntent);
                return;
            }
        }

        throw new Exception("Application name is incorrect or app doesn't exist on your device.");
    }
}