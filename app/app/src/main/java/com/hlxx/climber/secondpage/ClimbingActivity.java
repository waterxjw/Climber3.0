package com.hlxx.climber.secondpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.hlxx.climber.R;
import com.hlxx.climber.firstpage.TimeSettingActivity;
import com.hlxx.climber.secondpage.records.Record;
import com.hlxx.climber.secondpage.records.RecorderEditor;
import com.hlxx.climber.secondpage.settings.*;
import com.hlxx.climber.thirdpage.EndingActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.hlxx.climber.secondpage.settings.TimeGet.getTimeSecondSetted;
import static com.hlxx.climber.secondpage.settings.TimeGet.setTimeSecondSetted;
import static com.hlxx.climber.secondpage.settings.TimePut.intsToSecond;
import static com.hlxx.climber.secondpage.settings.TimePut.stringToInts;

public class ClimbingActivity extends AppCompatActivity {
    public static void setWillScreenOn(boolean willScreenOn) {
        ClimbingActivity.willScreenOn = willScreenOn;
    }

    private static boolean willScreenOn = false;
    private long firstPressedTime;
    private boolean isSwitch = false;
    private CountDownTimer timer;
    private VibrateSetter vibrateSetter = new VibrateSetter(this);
    private boolean isScreenOn;
    private WeakReference<CountDownTimer> wrfCDT;
    private Thread gcRequest;
    private Record aRecord = new Record();
    private RecorderEditor aRecorderEditor;
    private static int widthPixels;
    private static double speed;
    private static Bitmap sourceBitmap;
    private static Bitmap toShowBitmap;

    private static class MyHandler extends Handler {
        private final WeakReference<ClimbingActivity> weakReference;

        private MyHandler(ClimbingActivity activty) {
            this.weakReference = new WeakReference<ClimbingActivity>(activty);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ClimbingActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        TextView tv = activity.findViewById(R.id.lastTime);
                        int timeUsed = intsToSecond(stringToInts(tv.getText().toString()));
                        tv = null;
                        int yLocation = (int) speed * timeUsed + widthPixels;
                        ImageView testImageView = activity.findViewById(R.id.climb_background);
                        toShowBitmap = null;
                        toShowBitmap = Bitmap.createBitmap(sourceBitmap, 0, sourceBitmap.getHeight() - yLocation, sourceBitmap.getWidth(), widthPixels);
                        testImageView.setImageBitmap(toShowBitmap);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private Handler mHandler = new MyHandler(this);

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
                //animationDrawable.stop();
                ((TextView) findViewById(R.id.theRestTimePrompt)).setText("");
                lastTimeChronometer.stop();//Chronometer暂停
                Toast.makeText(ClimbingActivity.this, "成功！", Toast.LENGTH_LONG).show();//进行弹窗提示
                //处理震动
                vibrateSetter.setVibrate();
                IsForeground.setTimes(0);
                aRecord.setFinish(true);
                if (gcRequest != null) {
                    gcRequest.interrupt();
                    gcRequest = null;
                }
                mHandler = null;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbing);
        String data = getIntent().getStringExtra("time");
        setTimeSecondSetted(Integer.parseInt(data));
        aRecorderEditor = new RecorderEditor(getFilesDir());
        aRecord.setTime(getTimeSecondSetted());//默认+和上一页面交接+初始化记录仪

        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间

        if (willScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }//设置是否常亮

        //输出持续时间
        lastTimeChronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        lastTimeChronometer.start();

        //进行弹窗提示
        Toast.makeText(ClimbingActivity.this, "文文傻蛋！", Toast.LENGTH_SHORT).show();

        //已用时间输出
        timer = creatNewOne();
        timer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                                                    if (gcRequest != null) {
                                                        gcRequest.interrupt();
                                                        gcRequest = null;
                                                    }
                                                    mHandler = null;
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
                    while (true) {
                        try {
                            Message msg = new Message();
                            msg.what = 1;  //消息(一个整型值)
                            if (mHandler != null) {
                                mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                                Thread.sleep(5000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.gc();
                    }
                }
            });
            gcRequest.start();


            widthPixels = getWindowManager().getDefaultDisplay().getHeight();
            sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.climb_sky);
            speed = (sourceBitmap.getHeight() - widthPixels) / (double) getTimeSecondSetted();
            TextView tv = findViewById(R.id.lastTime);
            int timeUsed = intsToSecond(stringToInts(tv.getText().toString()));
            tv = null;
            int yLocation = (int) speed * timeUsed + widthPixels;
            ImageView testImageView = findViewById(R.id.climb_background);
            toShowBitmap = Bitmap.createBitmap(sourceBitmap, 0, sourceBitmap.getHeight() - yLocation, sourceBitmap.getWidth(), widthPixels);
            Log.e("TIMESS", "" + (sourceBitmap.getHeight() - yLocation - widthPixels));
            testImageView.setImageBitmap(toShowBitmap);
        }
    }

    @Override
    protected void onPause() {
        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        super.onPause();

        // animationDrawable.stop();

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。

        //控制台输出是否后台
        if (theRestTime.getText().toString().equals("到达") || !isScreenOn) {
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
            mHandler = null;
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


}
