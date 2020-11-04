package com.cn.ray.tinker_ray;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
    }

    //外置卡  - dex      指定目录   copy
    public void fix(View view) {
        File filesDir = getDir("odex", Context.MODE_PRIVATE);
        String name = "fix.dex";
        String filePath = new File(filesDir, name).getAbsolutePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        InputStream is = null;
        FileOutputStream os = null;
        try {
            Log.i(TAG, "fixBug: " + new File(Environment.getExternalStorageDirectory(), name).getAbsolutePath());
            is = new FileInputStream(new File(Environment.getExternalStorageDirectory(), name));
            os = new FileOutputStream(filePath);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            File f = new File(filePath);
            FixManager.getInstance().loadDex(this);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void test(View view) {
        Test test = new Test();
        test.testFix(this);
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private void getPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        while ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED) {
        }
    }
}
