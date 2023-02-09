package com.ghasty.mobilecontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText appNameEditText;
    private Button launchApp;
    private ImageView airplane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appNameEditText = findViewById(R.id.app_name_edit_text);
        launchApp = findViewById(R.id.launch_app_btn);
        airplane = findViewById(R.id.airplane);

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

        airplane.setOnClickListener(view -> toggleAirplaneMode());
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

    private void toggleAirplaneMode() {
        boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;

        Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", !isEnabled);
//        sendBroadcast(intent);
        Log.d("MY_APP", String.valueOf(isEnabled));
    }
}