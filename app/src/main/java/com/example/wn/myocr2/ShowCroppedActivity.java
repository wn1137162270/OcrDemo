package com.example.wn.myocr2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;

public class ShowCroppedActivity extends AppCompatActivity {
    private static final String TAG = "AppCompatActivity";

    private ImageView originalPicture;
    private ImageView grayScale;
    private TextView recognizeResult;

    private ProgressDialog dialog;

    private Uri uri;
    private String result;
    private TessBaseAPI tessBaseAPI;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tessBaseAPI = new TessBaseAPI();
            String dataPath = Environment.getExternalStorageDirectory() + "/tesseract/";
            String language = "chi_sim";
            tessBaseAPI.init(dataPath, language);
            tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
            tessBaseAPI.setImage(convertGray(getBitmapFromUri(uri)));
            result = tessBaseAPI.getUTF8Text();
            tessBaseAPI.end();
            Message message = new Message();
            message.arg1 = 0;
            handler.sendMessage(message);
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                recognizeResult.setText(result);
                dialog.dismiss();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cropped);

        init();

        Intent intent = getIntent();
        //code
        uri = intent.getData();
        originalPicture.setImageURI(uri);
        grayScale.setImageBitmap(convertGray(getBitmapFromUri(uri)));

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void init(){
        originalPicture = (ImageView) findViewById(R.id.original_picture_iv);
        grayScale = (ImageView) findViewById(R.id.gray_scale_iv);
        recognizeResult = (TextView) findViewById(R.id.recognize_result_tv);

        dialog = new ProgressDialog(this);
        dialog.setMessage("识别中...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private Bitmap getBitmapFromUri(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromUri");
        }
        return bitmap;
    }

    private Bitmap convertGray(Bitmap originalBitmap){
        Bitmap result = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(originalBitmap, 0, 0, paint);
        return result;
    }
}
