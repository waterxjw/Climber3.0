package com.hlxx.climber.thirdpage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.hlxx.climber.R;
import com.hlxx.climber.firstpage.TimeSettingActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class EndingActivity extends AppCompatActivity {
    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/hlxx/ClimberPicture";
    private ImageView imageView;
    private int[] picarrary;
    private int picindex;
    private int realTemp;
    private final int PERMISSION_REQUEST_CODE = 1;
    private String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending);

        ending_buttons();//按钮跳转：bt1,bt2，bt3
        picarrary = new int[]{R.mipmap.end_background, R.mipmap.end_background1, R.mipmap.end_background2, R.mipmap.end_background3, R.mipmap.end_background4};
        Random rnd = new Random();
        picindex = rnd.nextInt(4);
        realTemp = picarrary[picindex];
        imageView = (ImageView) findViewById(R.id.ending);
        imageView.setImageResource(realTemp);

        if (lacksPermissions(PERMISSIONS)) {// 缺少权限，则向用户申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    //按钮
    protected void ending_buttons() {
        Button btn1 = findViewById(R.id.end_to_start);
        Button btn2 = findViewById(R.id.stone);
        Button btn3 = findViewById(R.id.catch_picture);

        btn1.setOnClickListener(v -> {
            Intent intent = new Intent(EndingActivity.this, TimeSettingActivity.class);
            startActivity(intent);
            finish();
        });
        btn2.setOnClickListener(view -> {
            Intent intent = new Intent(EndingActivity.this, StoneActivity.class);
            startActivity(intent);
        });
        btn3.setOnClickListener(view -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), realTemp);
            saveImage(getApplicationContext(), bitmap);
            if (ContextCompat.checkSelfPermission(getApplication(), PERMISSIONS[0]) != PackageManager.PERMISSION_DENIED) {
                Toast.makeText(EndingActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EndingActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void saveImage(Context context, Bitmap bmp) {
        String fileName = null;
        File file = null;
        FileOutputStream outStream = null;
        try {
            file = new File(SAVE_REAL_PATH, System.currentTimeMillis() + ".jpg");
            if (!file.exists()) {
                file.getParentFile().mkdir();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileName = file.toString();
            outStream = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    private boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_DENIED) {
                return true;
            }
        }
        return false;
    }
}
