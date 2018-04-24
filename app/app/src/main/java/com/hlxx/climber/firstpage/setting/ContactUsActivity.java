package com.hlxx.climber.firstpage.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.hlxx.climber.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ImageView imageView=(ImageView)findViewById(R.id.imagelsn);
        imageView.setImageResource(R.mipmap.lsn);
    }
}
