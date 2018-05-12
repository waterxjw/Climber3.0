package com.hlxx.climber.secondpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.hlxx.climber.firstpage.TimeSettingActivity;
import com.hlxx.climber.R;
import com.hlxx.climber.secondpage.records.Record;
import com.hlxx.climber.secondpage.records.RecorderEditor;
import com.hlxx.climber.secondpage.settings.*;
import com.hlxx.climber.thirdpage.EndingActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class ClimbingActivity extends AppCompatActivity {

    private static ClimbingActivity instance;

    public static void setWillScreenOn(boolean willScreenOn) {
        ClimbingActivity.willScreenOn = willScreenOn;
    }

    private static boolean willScreenOn = false;

    private AnimationDrawable animationDrawable;
    private long firstPressedTime;
    private boolean isSwitch = false;
    private CountDownTimer timer;
    private VibrateSetter vibrateSetter = new VibrateSetter(this);
    private boolean isScreenOn;
    private WeakReference<CountDownTimer> wrfCDT;
    private WeakReference<ImageView> wrfIV;
    private Thread gcRequest;
    private Record aRecord = new Record();
    private RecorderEditor aRecorderEditor;


    /**
     * @return 由TimeGet的时间决定的新的计时器
     */
    private CountDownTimer creatNewOne() {

        final TextView theRestTime = findViewById(R.id.restTime);
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);
        return new CountDownTimer(TimeGet.getTime(true), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                theRestTime.setText(TimePut.intsToString(millisUntilFinished));
            }

            //倒计时结束后
            @Override
            public void onFinish() {
                String[] times = lastTimeChronometer.getText().toString().split(":");
                aRecord.setTotalTime(Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]));
                aRecord.setSwitchTimes(IsForeground.getTimes());
                theRestTime.setText("到达");
                try {
                    aRecorderEditor.oneRecordAdd(aRecord);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                animationDrawable.stop();
                ((TextView) findViewById(R.id.theRestTimePrompt)).setText("");
                lastTimeChronometer.stop();//Chronometer暂停
                Toast.makeText(ClimbingActivity.this, "成功！", Toast.LENGTH_LONG).show();//进行弹窗提示
                //处理震动
                vibrateSetter.setVibrate();
                IsForeground.setTimes(0);
                aRecord.setFinish(true);
                //切换
                Intent intent2;
                if (isScreenOn) {
                    intent2 = new Intent(ClimbingActivity.this, MovePlayActivity.class);
                } else {
                    intent2 = new Intent(ClimbingActivity.this, EndingActivity.class);
                }
                startActivity(intent2);
                finish();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbing);
        if (willScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        aRecorderEditor = new RecorderEditor(getFilesDir());
        String data = getIntent().getStringExtra("time");
        aRecord.setTime(Integer.parseInt(data));
        TimeGet.setTimeMinute(Integer.parseInt(data));//默认+和上一页面交接

        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间

        //输出持续时间
        lastTimeChronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        lastTimeChronometer.start();

        //进行弹窗提示
        Toast.makeText(ClimbingActivity.this, "文文傻蛋！", Toast.LENGTH_LONG).show();

        //已用时间输出
        timer = creatNewOne();
        timer.start();

        //图片
        animationDrawable = /*(AnimationDrawable) img.getDrawable();*/new AnimationDrawable();
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.actions4), 500);
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.actions3), 500);
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.actions2), 500);
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.actions1), 500);
        animationDrawable.setOneShot(false);
        ImageView img = findViewById(R.id.imageView2);
        img.setBackgroundDrawable(animationDrawable);
    }

    private void createImageView(int num) {
        ImageView imageView = findViewById(R.id.climb_background);
        imageView.setImageResource(num);
        wrfIV = new WeakReference<>(imageView);
        imageView = null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        createImageView(IsForeground.setClimbBack());
        animationDrawable.start();

        Button giveUpButton = findViewById(R.id.button_giveUp);
        giveUpButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (System.currentTimeMillis() - firstPressedTime < 2000) {
                                                    final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);
                                                    String[] times = lastTimeChronometer.getText().toString().split(":");
                                                    aRecord.setTotalTime(Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]));
                                                    aRecord.setSwitchTimes(IsForeground.getTimes());
                                                    aRecord.setFinish(false);
                                                    try {
                                                        aRecorderEditor.oneRecordAdd(aRecord);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    animationDrawable.stop();
                                                    Intent intent = new Intent(ClimbingActivity.this, TimeSettingActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(getBaseContext(), "骚年，何弃疗！", Toast.LENGTH_SHORT).show();
                                                    firstPressedTime = System.currentTimeMillis();
                                                }
                                            }
                                        }
        );


        //处理缓存
        if (gcRequest == null || !gcRequest.isAlive()) {
            gcRequest = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.gc();
                }
            });
            gcRequest.start();
        }
    }


    @Override
    protected void onPause() {
        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        super.onPause();

        animationDrawable.stop();

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。

        //控制台输出是否后台
        if (/*IsForeground.determine(ClimbingActivity.this)*/theRestTime.getText().toString().equals("到达") || !isScreenOn) {
            Log.e("State", "True");
        } else {
            isSwitch = true;
            IsForeground.setTimes(IsForeground.getTimes() + 1);
            TimeChange.changeTime(theRestTime.getText().toString());
            timer.cancel();
            wrfCDT = new WeakReference<>(timer);
            timer = null;
            timer = creatNewOne();
            timer.start();
        }
        if (gcRequest != null) {
            gcRequest.interrupt();
        }
    }


    @Override
    protected void onDestroy() {
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间
        lastTimeChronometer.stop();
        timer.cancel();
        wrfCDT = new WeakReference<>(timer);
        timer = null;
        super.onDestroy();
        IsForeground.setTimes(0);
    }


    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);
            String[] times = lastTimeChronometer.getText().toString().split(":");
            aRecord.setTotalTime(Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]));
            aRecord.setSwitchTimes(IsForeground.getTimes());
            aRecord.setFinish(false);
            try {
                aRecorderEditor.oneRecordAdd(aRecord);
                Log.e("Times", "onBackPressed: " + aRecorderEditor.time);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ActivityCompat.finishAffinity(this);//退出整个程序
        } else {
            Toast.makeText(getBaseContext(), "再点一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    public static Context getMyApplication() {
        return instance;
    }
}
