package com.hlxx.climber.secondpage;

<<<<<<< HEAD:app/app/src/main/java/com/example/xjw/climber/ClimbingActivity.java
import android.app.Service;
=======
>>>>>>> upstream/master:app/app/src/main/java/com/hlxx/climber/secondpage/ClimbingActivity.java
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
<<<<<<< HEAD:app/app/src/main/java/com/example/xjw/climber/ClimbingActivity.java
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.xjw.climber.service.MyDetectService;
=======
import android.view.View;
import android.widget.*;
import com.hlxx.climber.firstpage.TimeSettingActivity;
import com.hlxx.climber.R;
import com.hlxx.climber.secondpage.settings.*;
import com.hlxx.climber.thirdpage.EndingActivity;

>>>>>>> upstream/master:app/app/src/main/java/com/hlxx/climber/secondpage/ClimbingActivity.java

public class ClimbingActivity extends AppCompatActivity {

    private static ClimbingActivity instance;

    private AnimationDrawable animationDrawable;
    private long firstPressedTime;
    private boolean isSwitch = false;
    private CountDownTimer timer;
<<<<<<< HEAD:app/app/src/main/java/com/example/xjw/climber/ClimbingActivity.java
    private Context mContext;
=======
    private VibrateSetter vibrateSetter = new VibrateSetter(this);
    boolean isScreenOn;

>>>>>>> upstream/master:app/app/src/main/java/com/hlxx/climber/secondpage/ClimbingActivity.java

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
                theRestTime.setText("到达");
                ((TextView) findViewById(R.id.theRestTimePrompt)).setText("");
                lastTimeChronometer.stop();//Chronometer暂停
                Toast.makeText(ClimbingActivity.this, "成功！", Toast.LENGTH_LONG).show();//进行弹窗提示
                //处理震动
                vibrateSetter.setVibrate();
                IsForeground.setTimes(0);
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

    //启动Service函数
    private void startService() {
        Features.showForeground = true;
        Intent intent = new Intent(mContext, MyDetectService.class);
        mContext.startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbing);

        String data = getIntent().getStringExtra("time");
        TimeGet.setTimeMinute(Integer.parseInt(data));//默认+和上一页面交接

        final TextView theRestTime = findViewById(R.id.restTime);//剩余时间栏，右下角
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间

        //开始轮询监听
        startService();
        Features.BGK_METHOD =3;

        //输出持续时间
        lastTimeChronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        lastTimeChronometer.start();

        //进行弹窗提示
        Toast.makeText(ClimbingActivity.this, "文文傻蛋！", Toast.LENGTH_LONG).show();

        //已用时间输出
        timer = creatNewOne();
        timer.start();

        //图片
        ImageView img = findViewById(R.id.imageView2);
        img.setImageResource(R.drawable.animation);
        animationDrawable = (AnimationDrawable) img.getDrawable();


    }

    private void createImageView(int num) {
        ImageView imageView = findViewById(R.id.climb_background);
        imageView.setImageResource(num);
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

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。

        //控制台输出是否后台
        if (/*IsForeground.determine(ClimbingActivity.this)*/theRestTime.getText().toString().equals("到达") || !isScreenOn) {
            Log.e("State", "True");
        } else {
            Log.e("State", "False" + IsForeground.getTimes() + " " + theRestTime.getText().toString());
            isSwitch = true;
            IsForeground.setTimes(IsForeground.getTimes() + 1);
            TimeChange.changeTime(theRestTime.getText().toString());
            timer.cancel();
            timer = creatNewOne();
            timer.start();
        }
    }


    @Override
    protected void onDestroy() {
        final Chronometer lastTimeChronometer = findViewById(R.id.lastTime);//持续时间
        lastTimeChronometer.stop();
        timer.cancel();
        super.onDestroy();
        IsForeground.setTimes(0);
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

    public static Context getMyApplication() {
        return instance;
    }
}
