package com.example.xjw.climber;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wenming.library.BackgroundUtil;

public class ClimbingActivity extends AppCompatActivity {

    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbing);
        Intent itent = getIntent();
        String data = getIntent().getStringExtra("time");
        TimeGet.setTime(Integer.parseInt(data));


        //剩余时间输出
        final TextView tv = findViewById(R.id.timeOut);

        int[] varTime = TimeGet.getInstance().getTime();
        final int hours = varTime[0];
        final int minute = varTime[1];


        //已用时间输出
        CountDownTimer timer = new CountDownTimer((hours * 60 + minute) * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sUntilFinished = millisUntilFinished / 1000;
                long mUntilFinished = sUntilFinished / 60;
                sUntilFinished = sUntilFinished % 60;
                long hUntilFinished = mUntilFinished / 60;
                mUntilFinished = mUntilFinished % 60;

                String[] timeToOut = TimePut.timeOutPut(hUntilFinished, mUntilFinished, sUntilFinished);
                tv.setText(String.format(getResources().getString(R.string.time), timeToOut[0], timeToOut[1], timeToOut[2]));
            }

            @Override
            public void onFinish() {
                tv.setText("到达");
                TextView tv2 = findViewById(R.id.outPrompt);
                tv2.setText("");

                Intent intent2=new Intent(ClimbingActivity.this,EndingActivity.class);
                
                startActivity(intent2);
            }
        }.start();
        final Chronometer usedChronometer = findViewById(R.id.lastTime);
        usedChronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        usedChronometer.start();
        Toast.makeText(ClimbingActivity.this, "坚持！", Toast.LENGTH_LONG).show();

        //判断是否在后台
        /**以下仅作为测试
        Date begin = new Date();
        do {
            Date now = new Date();
            if (now.getTime()-begin.getTime()>20000)
                break;
        }while (Boolean.TRUE);
        */
        Boolean isForeground = BackgroundUtil.queryUsageStats(ClimbingActivity.this, "com.example.xjw.climber");
        if (isForeground == Boolean.FALSE)
            Toast.makeText(ClimbingActivity.this, "已离开", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(ClimbingActivity.this, "还在", Toast.LENGTH_LONG).show();


        //处理倒计时结束后Chronometer暂停。
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        usedChronometer.stop();
                        Looper.prepare();
                        Toast.makeText(ClimbingActivity.this, "成功！", Toast.LENGTH_LONG).show();
                        Log.e("a", "true");

                        //处理震动
                        Vibrator theVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                theVibrator.vibrate(VibrationEffect.createOneShot(1000, 1)); //等待指定时间后开始震动，震动时间,等待震动,震动的时间
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        Looper.loop();
                        break;
                    case 2:
                        Log.e("abc", "pass");
                        break;
                }
            }

        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((hours * 60 + minute) * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain(handler);
                message.what = 1;
                handler.handleMessage(message);
            }
        }).start();


        //图片问题
        ImageView img = findViewById(R.id.imageView2);
        img.setImageResource(R.drawable.animation);
        animationDrawable = (AnimationDrawable) img.getDrawable();

    }



    //emmmmmmmmm
    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }

    //释放内存，但是应该在锁屏后执行
    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }


}
