package com.example.xjw.climber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class EndingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending);

        //view层的控件和业务层的控件，靠id关联和映射  给btn1赋值，即设置布局文件中的Button按钮id进行关联
        Button btn1 = (Button) findViewById(R.id.end_to_start);

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
    }
}
