package com.example.xjw.climber;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ClimbingActivity extends AppCompatActivity {

    private AnimationDrawable animationDrawable;
    private long firstPressedTime;
    private boolean isSwitch = false;
    private CountDownTimer timer;


    class Timer {
        WeakReference<ClimbingActivity> theActivityReference;

        Timer(ClimbingActivity activity) {
            theActivityReference = new WeakReference((ClimbingActivity) activity);
        }

        CountDownTimer creatNewOne() {
            final TextView theRestTime = findViewById(R.id.restTime);
            final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);
            return new CountDownTimer(TimeGet.getTime(true), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long sUntilFinished = millisUntilFinished / 1000;
                    long mUntilFinished = sUntilFinished / 60;
                    sUntilFinished = sUntilFinished % 60;
                    long hUntilFinished = mUntilFinished / 60;
                    mUntilFinished = mUntilFinished % 60;

                    String[] timeToOut = TimePut.timeOutPut(hUntilFinished, mUntilFinished, sUntilFinished);
                    theRestTime.setText(String.format(getResources().getString(R.string.time), timeToOut[0], timeToOut[1], timeToOut[2]));
                }

                //倒计时结束后
                @Override
                public void onFinish() {
//                    if (!isSwitch) {
                    theRestTime.setText("到达");
                    ((TextView) findViewById(R.id.theRestTimePrompt)).setText("");
                    lastTimeChronometer.stop();//Chronometer暂停
                    Toast.makeText(ClimbingActivity.this, "成功！", Toast.LENGTH_LONG).show();//进行弹窗提示
                    //处理震动
                    Vibrator theVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            theVibrator.vibrate(VibrationEffect.createOneShot(1000, 1)); //等待指定时间后开始震动，震动时间,等待震动,震动的时间
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    //切换
                    Intent intent2 = new Intent(ClimbingActivity.this, EndingActivity.class);
                    startActivity(intent2);
//                    } else {
//                        TimeChange.changeTime(theRestTime.getText().toString());
//                        isSwitch = false;
//                        Log.i("asdfsadf", "onFinish: " + IsForeground.getTimes() + " " + theRestTime.getText().toString());
//                    }
                }

            };
        }
    }

    private Timer getTimer = new Timer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbing);
        Intent itent = getIntent();
        String data = getIntent().getStringExtra("time");
        TimeGet.setTimeMinute(Integer.parseInt(data));//默认+和上一页面交接

        final TextView theRestTime = findViewById(R.id.restTime);
        //剩余时间栏，右下角
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间


        //输出持续时间
        lastTimeChronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        lastTimeChronometer.start();

        //进行弹窗提示
        Toast.makeText(ClimbingActivity.this, "文文傻蛋！", Toast.LENGTH_LONG).show();


        //已用时间输出
        timer = getTimer.creatNewOne();
        timer.start();

        //图片
        ImageView img = findViewById(R.id.imageView2);
        img.setImageResource(R.drawable.animation);
        animationDrawable = (AnimationDrawable) img.getDrawable();
    }


    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();

        //处理缓存
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.gc();
            }
        }).start();

    }


    @Override
    protected void onPause() {
        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        super.onPause();

        if (animationDrawable != null) {
            animationDrawable.stop();
        }

        //控制台输出是否后台
        if (IsForeground.determine(ClimbingActivity.this)) {
            Log.e("State", "True");
        } else {
            Log.e("State", "False" + IsForeground.getTimes() + " " + theRestTime.getText().toString());
            isSwitch = true;

            TimeChange.changeTime(theRestTime.getText().toString());
            timer.cancel();
            timer = getTimer.creatNewOne();
            timer.start();

        }
    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            ActivityCompat.finishAffinity(this);//退出整个程序
        } else {
            Toast.makeText(getBaseContext(), "再点一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }

    }
}
