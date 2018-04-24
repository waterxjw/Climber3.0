package com.example.xjw.climber.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.xjw.climber.Features;
import com.example.xjw.climber.service.MyDetectService;


/**
 * Created by wenmingvs on 2016/1/13.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Features.showForeground) {
            Intent i = new Intent(context, MyDetectService.class);
            context.startService(i);
        }

    }
}
