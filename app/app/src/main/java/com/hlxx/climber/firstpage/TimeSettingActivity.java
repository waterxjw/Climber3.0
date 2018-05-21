package com.hlxx.climber.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.ddz.floatingactionbutton.FloatingActionButton;
import com.hlxx.climber.firstpage.setting.HistoryActivity;
import com.hlxx.climber.secondpage.ClimbingActivity;
import com.hlxx.climber.R;
import com.hlxx.climber.firstpage.setting.SettingActivity;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

public class TimeSettingActivity extends AppCompatActivity {
    //背景图片
    private ImageView imageView;
    private WheelView wheelView;
    private long firstPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);
        // createScaleImage();
        createImageView(R.mipmap.remote_mountain1); //设置背景图片
        createWheelView(); //设置时间滚轮
        createButton();//设置开始专注按钮
        createFAButton();//设置右上角浮动按钮
        //下面是自定义一个任务栏，取代原先自带的任务栏


        Button aButton =findViewById(R.id.start_read_file);
        aButton.setOnClickListener((view) -> startActivity(new Intent(TimeSettingActivity.this, ToReadFile.class)));

    }

    //任务栏右侧的菜单按钮
    /*public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    //为菜单项设置时间响应
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.setting:
                Toast.makeText(this,"setting",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }*/



    private void createFAButton() {
        FloatingActionButton fabSetting = findViewById(R.id.fab_setting);
        FloatingActionButton fabHistory = findViewById(R.id.fab_history);
        FloatingActionButton fabLogin = findViewById(R.id.fab_login);
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TimeSettingActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TimeSettingActivity.this, SettingActivity.class);
                ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(TimeSettingActivity.this, new Pair<>(fabSetting, "setting"));
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });
        fabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TimeSettingActivity.this, "History", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TimeSettingActivity.this, HistoryActivity.class
                );
                startActivity(intent);
            }
        });
        fabLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TimeSettingActivity.this, "Login", Toast.LENGTH_SHORT).show();
               /* Intent intent = new Intent(TimeSettingActivity.this, LoginActivity.class
                );
                startActivity(intent);*/
            }
        });
    }

    private void createButton() {
        Button startbutton = findViewById(R.id.button);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //data即当前选中的滚轮数据
                String data = (String) wheelView.getSelectionItem();
                //下面冬冬可自行切换至下一activity并传入data数据
                Intent intent = new Intent(TimeSettingActivity.this, ClimbingActivity.class);
                intent.putExtra("time", data);
                startActivity(intent);
                finish();

            }
        });
    }

    //暂时弃用的图片缩放
    /*private void createScaleImage(){
            scaleImage=(ScaleImage)findViewById(R.id.scaleimage);
            scaleImage.startScale(R.mipmap.mountain1);

     }*/
    private void createImageView(int num) {
        imageView = findViewById(R.id.imageview);
        imageView.setImageResource(num);
    }

    //设置时间滚轮
    private void createWheelView() {
        wheelView = findViewById(R.id.wheelView);
        //设置数据适配器
        wheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        //传入数据
        wheelView.setWheelData(createMinutes());
        wheelView.setWheelClickable(true);
        //设置滚轮数据循环显示
        wheelView.setLoop(true);
        //设置滚轮显示3行
        wheelView.setWheelSize(3);
        wheelView.setSkin(WheelView.Skin.Holo);
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        //设置选中的文字颜色与大小
        style.selectedTextColor = getResources().getColor(R.color.colorbutton);
        style.holoBorderColor = getResources().getColor(R.color.colorbutton);
        style.selectedTextSize = 25;
        //设置背景颜色为透明
        style.backgroundColor = Color.alpha(0);
        style.textColor = getResources().getColor(R.color.colorbutton);

        wheelView.setStyle(style);
        //设置滚轮右侧的指示文字及其格式
        wheelView.setExtraText("分钟", getResources().getColor(R.color.colorbutton), 60, 120);
        //设置滚轮滑动停止时的事件，即更换图片
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int i, Object o) {
                //必须先清零，以避免内存过度占用问题，艹，老子为了这一行代码浪费了一中午
                imageView.setImageResource(0);
                //切换图片
                imageView.setImageResource(R.mipmap.remote_mountain1 + i);


            }
        });
    }

    //为滚轮设置数据
    private ArrayList<String> createMinutes() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 5; i <= 20; i += 5) {
            if (i == 5)
                list.add("0" + i);
            else {
                list.add("" + i);
            }
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 5000) {
            ActivityCompat.finishAffinity(this);//退出整个程序
        } else {
            Toast.makeText(getBaseContext(), "再点一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

}

