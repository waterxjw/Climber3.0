package com.example.xjw.climber;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class MovePlayActivity extends Activity {

    private VideoView video;

    /**
     * Called when the activity is firstcreated.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_movi);

        video = (VideoView) findViewById(R.id.videoView);

        MediaController mc = new MediaController(this);       // 创建一个MediaController对象
        video.setMediaController(mc);       // 将VideoView与MediaController关联起来

        mc.setVisibility(View.INVISIBLE);
        video.setMediaController(mc);//隐藏状态栏

        video.setVideoURI(Uri.parse("android.resource://com.example.xjw.climber/"
                + R.raw.ends));
        video.requestFocus();       // 设置VideoView获取焦点

        try {
            video.start();      // 播放视频
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置VideoView的Completion事件监听器
        video.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("viod", "onCompletion: END");
                finish();
                Intent intent2 = new Intent(MovePlayActivity.this, EndingActivity.class);
                startActivity(intent2);
            }
        });


    }


}

