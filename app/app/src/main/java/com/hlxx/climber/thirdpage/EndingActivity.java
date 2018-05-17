package com.hlxx.climber.thirdpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hlxx.climber.R;
import com.hlxx.climber.firstpage.TimeSettingActivity;
import com.hlxx.climber.services.AzureServiceAdapter;


public class EndingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending);
        AzureServiceAdapter.Initialize(this);
        ending_buttons();//按钮跳转：bt1,bt2


    }

    //按钮
    protected void ending_buttons() {
        //view层的控件和业务层的控件，靠id关联和映射  给btn1赋值，即设置布局文件中的Button按钮id进行关联
        Button btn1 = (Button) findViewById(R.id.end_to_start);
        Button btn2 = (Button) findViewById(R.id.stone);

        //给btn1绑定监听事件
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // 给bnt1添加点击响应事件
                Intent intent = new Intent(EndingActivity.this, TimeSettingActivity.class);
                //启动
                startActivity(intent);
                finish();

            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EndingActivity.this, StoneActivity.class);
                startActivity(intent);
            }
        });
    }

}
