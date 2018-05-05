package com.hlxx.climber.thirdpage;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hlxx.climber.R;

public class StoneActivity extends Activity {

    private Button upload;
    private Button out;
    private EditText user_input;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 非模态化
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_stone);

        setFinishOnTouchOutside(true);//点击Dialog外退出
        stone_buttons();//按钮操作：upload,out


    }

    //按钮
    protected void stone_buttons() {
        Button upload = (Button) findViewById(R.id.upload);
        Button out = (Button) findViewById(R.id.out);
        upload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Toast.makeText(StoneActivity.this, "信息已经发出去啦！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        out.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }


    //点击dialog区域外则返回
    //好像没成功
    public boolean onTouchEvent(MotionEvent event) {

        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

/*有bug的点击dialog区域外返回
   public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float x = event.getX();
        float y = event.getY();

        PointF p = new PointF(x, y);
        if(p is in StoneActivity)
            donothing;
        else
            this.finish();

        return super.onTouchEvent(event);
    }
*/


    //输入框
    public void init() {
        String text = "";//用户输入的文本
        user_input = (EditText) findViewById(R.id.user_to_talk);
        SpannableString ss = new SpannableString("刻下你想说的话吧");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        user_input.setHint(new SpannableString(ss));
        text = user_input.getText().toString();
    }


}