package com.neoend.gms.check;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.neoend.gms.check.version.VersionCheckActivity;
import com.neoend.gms.util.SystemCommandJNI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Intent gmsCheckIntent;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.result);

        gmsCheckIntent = new Intent(this, VersionCheckActivity.class);

        // JNI call
        if (checkPermissions()) {
            Toast.makeText(this, "Granted!!", Toast.LENGTH_SHORT).show();
            goGmsCheck();
        } else {
            Toast.makeText(this, "Not Granted!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void execute(View view) {
        result.setText(SystemCommandJNI.getInstance().execute("/system/bin/getprop"));
    }

    private void goGmsCheck() {
        SystemCommandJNI.getInstance().getprop();
        startActivity(gmsCheckIntent);
    }

    public void grantPermission(View view) {
        checkPermissions();
    }

    public void goAppSetting(View view) {
        Uri packageUri = Uri.parse("package:com.neoend.gms.check");
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageUri);
        try {
            startActivityForResult(intent, 1);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();

        boolean granted = true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            granted = false;
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            granted = false;
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!granted) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }

        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Grant External storage access to display system properties", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        goGmsCheck();
    }
}
