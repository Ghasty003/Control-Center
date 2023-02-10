package com.ghasty.mobilecontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Build;
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
    private ImageView airplane, wifi, bluetooth, flashLight;

    private boolean isFlashLightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appNameEditText = findViewById(R.id.app_name_edit_text);
        launchApp = findViewById(R.id.launch_app_btn);
        airplane = findViewById(R.id.airplane);
        wifi = findViewById(R.id.wifi);
        bluetooth = findViewById(R.id.bluetooth);
        flashLight = findViewById(R.id.flash_light);

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
        wifi.setOnClickListener(view -> toggleWifiMode());
        bluetooth.setOnClickListener(view -> toggleBluetoothMode());
        flashLight.setOnClickListener(view -> {
            if (isFlashLightOn) {
                turnOffFlashLight();
            } else {
                turnOnFlashLight();
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

    private void turnOnFlashLight() {
        isFlashLightOn = true;
        flashLight.setBackground(getDrawable(R.drawable.active_item_bg));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                CameraManager camera = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
                String cameraId = camera.getCameraIdList()[0];
                camera.setTorchMode(cameraId, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void turnOffFlashLight() {
        isFlashLightOn = false;
        flashLight.setBackground(getDrawable(R.drawable.icon_bg));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                CameraManager camera = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
                String cameraId = camera.getCameraIdList()[0];
                camera.setTorchMode(cameraId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleWifiMode() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isEnabled = wifiManager.isWifiEnabled();

        if (isEnabled) {
            wifiManager.setWifiEnabled(false);
            wifi.setBackground(getDrawable(R.drawable.icon_bg));
        } else {
            wifiManager.setWifiEnabled(true);
            wifi.setBackground(getDrawable(R.drawable.active_item_bg));
        }
    }

    private void toggleBluetoothMode() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (isEnabled) {
            bluetoothAdapter.disable();
            bluetooth.setBackground(getDrawable(R.drawable.icon_bg));
        } else {
            bluetoothAdapter.enable();
            bluetooth.setBackground(getDrawable(R.drawable.active_item_bg));
        }
    }

    private void toggleAirplaneMode() {
        boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;

        Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", !isEnabled);
//        sendBroadcast(intent);
        Log.d("MY_APP", String.valueOf(isEnabled));
    }

    @Override
    protected void onStop() {
        super.onStop();
        turnOffFlashLight();
    }
}