package com.example.wn.myocr2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_MODE = 454;
    static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    private Button recognize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recognize = (Button) findViewById(R.id.recognize_btn);

        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_MODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(this, TakePictureActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "请打开摄像头", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_CAMERA}, REQUEST_CAMERA_MODE);
        }
        else {
            Intent intent = new Intent(this, TakePictureActivity.class);
            startActivity(intent);
        }
    }
}
