package com.hlxx.climber.secondpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
    private static boolean willScreenOn = false;
    private long firstPressedTime;
    private boolean isSwitch = false;
    private boolean cancle = false;
    private CountDownTimer timer;
    private VibrateSetter vibrateSetter = new VibrateSetter(this);
    private boolean isScreenOn = true;
    private Thread gcRequest;
    private Record aRecord = new Record();
    private RecorderEditor aRecorderEditor;
    private static int hightPixels;
    private static int widthPixels;
    private static double backgroundSpeed;
    private static double mountainSpeed = 10;
    private static Bitmap sourceBGBitmap;
    private static Bitmap toShowBGBitmap;
    private static Bitmap sourceMBitmap;
    private static Bitmap toShowMBitmap;
    private Handler mHandler = new MyHandler(this);

    //设置是否常亮
    public static void setWillScreenOn(boolean willScreenOn) {
        ClimbingActivity.willScreenOn = willScreenOn;
    }

    //是否熄屏
    private void screenState() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
    }

    //用于处理背景图片
    private static class MyHandler extends Handler {
        private final WeakReference<ClimbingActivity> weakReference;

        private MyHandler(ClimbingActivity activty) {
            this.weakReference = new WeakReference<>(activty);
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
                        int yLocation = (int) backgroundSpeed * timeUsed + hightPixels;
                        if (yLocation / (double) sourceBGBitmap.getHeight() < 0.595) {
                            tv.setTextColor(ContextCompat.getColor(activity, R.color.color_time_rest_start));
                        } else if (yLocation / (double) sourceBGBitmap.getHeight() > 0.79) {
                            tv.setTextColor(ContextCompat.getColor(activity, R.color.color_time_rest_end));
                            ((TextView) activity.findViewById(R.id.theRestTimePrompt)).setTextColor(ContextCompat.getColor(activity, R.color.color_time_rest_end));
                            ((TextView) activity.findViewById(R.id.restTime)).setTextColor(ContextCompat.getColor(activity, R.color.color_time_rest_end));
                        } else {
                            tv.setTextColor(ContextCompat.getColor(activity, R.color.color_time_rest_mid));
                        }
                        tv = null;

                        ImageView bgdImageView = activity.findViewById(R.id.climb_background);
                        yLocation = yLocation > sourceBGBitmap.getHeight() ? sourceBGBitmap.getHeight() : yLocation;
                        toShowBGBitmap = null;
                        toShowBGBitmap = Bitmap.createBitmap(sourceBGBitmap, 0, sourceBGBitmap.getHeight() - yLocation, sourceBGBitmap.getWidth(), hightPixels);
                        bgdImageView.setImageBitmap(toShowBGBitmap);

                        ImageView mtImageView = activity.findViewById(R.id.mountain);
                        yLocation = (int) mountainSpeed * timeUsed + hightPixels;
                        yLocation = yLocation > sourceMBitmap.getHeight() ? sourceMBitmap.getHeight() : yLocation;
                        toShowMBitmap = null;
                        toShowMBitmap = Bitmap.createBitmap(sourceMBitmap, 0, sourceMBitmap.getHeight() - yLocation, sourceMBitmap.getWidth(), hightPixels);
                        mtImageView.setImageBitmap(toShowMBitmap);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //返回由TimeGet的时间决定的新的计时器
    private CountDownTimer creatNewOne() {
        final TextView theRestTime = findViewById(R.id.restTime);
        return new CountDownTimer(TimeGet.getTime(true), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                theRestTime.setText(TimePut.intsToString(millisUntilFinished));
            }

            //倒计时结束后
            @Override
            public void onFinish() {
                theRestTime.setText("到达");
                recordWrite(true);
                ((TextView) findViewById(R.id.theRestTimePrompt)).setText("");
                ((Chronometer) findViewById(R.id.lastTime)).stop();//Chronometer暂停
                Toast.makeText(ClimbingActivity.this, "成功！", Toast.LENGTH_LONG).show();//进行弹窗提示
                vibrateSetter.setVibrate();//处理震动
                IsForeground.setTimes(0);
                if (gcRequest != null) {
                    gcRequest.interrupt();
                    gcRequest = null;
                }
                screenState();
                //切换
                Intent nextIntent;
                if (isScreenOn) {
                    nextIntent = new Intent(ClimbingActivity.this, MovePlayActivity.class);
                } else {
                    nextIntent = new Intent(ClimbingActivity.this, EndingActivity.class);
                }
                startActivity(nextIntent);
                mHandler = null;
                finish();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbing);
        String data = getIntent().getStringExtra("time");
        setTimeSecondSetted(Integer.parseInt(data) - 4);
        aRecorderEditor = new RecorderEditor(getFilesDir());
        aRecord.setTimeSetted(getTimeSecondSetted());//默认+和上一页面交接+初始化记录仪

        if (willScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }//设置是否常亮

        //输出持续时间
        Chronometer lastTimeChronometer = findViewById(R.id.lastTime);
        lastTimeChronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        lastTimeChronometer.start();
        //进行弹窗提示
        Toast.makeText(ClimbingActivity.this, "文文傻蛋！", Toast.LENGTH_SHORT).show();

        //剩余时间输出
        timer = creatNewOne();
        timer.start();

        //设置背景
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        hightPixels = dm.heightPixels;
        widthPixels = dm.widthPixels;
        dm = null;
        sourceBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.climb_sky);
        backgroundSpeed = (sourceBGBitmap.getHeight() - hightPixels) / (double) getTimeSecondSetted();
        ImageView bgImageView = findViewById(R.id.climb_background);
        toShowBGBitmap = Bitmap.createBitmap(sourceBGBitmap, 0, sourceBGBitmap.getHeight() - hightPixels, sourceBGBitmap.getWidth(), hightPixels);
        bgImageView.setImageBitmap(toShowBGBitmap);

        //设置山
        sourceMBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.climb_mountain);
        mountainSpeed = mountainSpeed < (sourceMBitmap.getHeight() - hightPixels) / (double) getTimeSecondSetted() ? mountainSpeed : (sourceMBitmap.getHeight() - hightPixels) / (double) getTimeSecondSetted();
        ImageView mtImageView = findViewById(R.id.mountain);
        toShowMBitmap = Bitmap.createBitmap(sourceMBitmap, 0, sourceMBitmap.getHeight() - hightPixels, sourceMBitmap.getWidth(), hightPixels);
        mtImageView.setImageBitmap(toShowMBitmap);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //放弃按钮
        (findViewById(R.id.button_giveUp)).setOnClickListener((view) -> {
            if (System.currentTimeMillis() - firstPressedTime < 5000) {
                cancle = true;
                recordWrite(false);
                if (gcRequest != null) {
                    gcRequest.interrupt();
                    gcRequest = null;
                }
                mHandler = null;
                startActivity(new Intent(ClimbingActivity.this, TimeSettingActivity.class));
                finish();
            } else {
                Toast.makeText(getBaseContext(), "骚年，何弃疗！", Toast.LENGTH_SHORT).show();
                firstPressedTime = System.currentTimeMillis();
            }
        });

        //处理缓存+更新背景
        if (gcRequest == null || !gcRequest.isAlive()) {
            gcRequest = new Thread(() -> {
                boolean threadState = true;
                while (threadState) {
                    try {
                        Message msg = new Message();
                        msg.what = 1;  //消息(一个整型值)
                        if (mHandler != null) {
                            mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        threadState = false;
                    }
                }
                System.gc();
            });
            gcRequest.start();
        }
    }

    @Override
    protected void onPause() {
        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        super.onPause();

        screenState();
        //是否后台
        if (!cancle && !theRestTime.getText().toString().equals("到达") && isScreenOn) {
            isSwitch = true;
            IsForeground.setTimes(IsForeground.getTimes() + 1);
            TimeChange.changeTime(theRestTime.getText().toString());
            timer.cancel();
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
        Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间
        lastTimeChronometer.stop();
        lastTimeChronometer = null;
        timer.cancel();
        timer = null;
        super.onDestroy();
        IsForeground.setTimes(0);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 5000) {
            cancle=true;
            mHandler = null;
            recordWrite(false);
            ActivityCompat.finishAffinity(this);//退出整个程序
        } else {
            Toast.makeText(getBaseContext(), "再点一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    private void recordWrite(boolean finish) {

        String[] times = ((Chronometer) findViewById(R.id.lastTime)).getText().toString().split(":");
        aRecord.setTotalTime(Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]));
        aRecord.setSwitchTimes(IsForeground.getTimes());
        aRecord.setFinish(finish);
        aRecorderEditor.setFinish(finish);
        aRecord.setLevel();
        try {
            aRecorderEditor.oneRecordAdd(aRecord);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
