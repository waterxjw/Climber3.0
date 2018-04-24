package com.example.xjw.climber.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.xjw.climber.ClimbingActivity;
import com.example.xjw.climber.R;
import com.wenming.library.BackgroundUtil;
import com.example.xjw.climber.Features;
import java.util.ArrayList;

public class MyDetectService extends Service {
    private static final float UPDATA_INTERVAL = 0.5f;//in seconds
    private String status;
    private Context mContext;
    private ArrayList<String> mContentList;
    private Notification notification;
    private AlarmManager manager;
    private PendingIntent pendingIntent;
    private NotificationCompat.Builder mBuilder;
    private Intent mIntent;
    private NotificationManager mNotificationManager;
    private static final int NOTICATION_ID = 0x1;

    public MyDetectService(Context context) {
        mContext = context;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
    @Override
    public IBinder onBind (Intent intent){
        Log.i ("Service","DS被调用");
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = this;
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        startNotification();
    }

    public void onDestroy() {
        Features.showForeground = false;
        stopForeground(true);
        super.onDestroy();
    }

    private void startNotification() {
        status = getAppStatus() ? "前台" : "后台";
        mIntent = new Intent(mContext, ClimbingActivity.class);
        pendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.fab_bg_mini)
                .setContentText(mContentList.get(3))
                .setContentTitle("App处于" + status)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notification = mBuilder.build();
        startForeground(NOTICATION_ID, notification);
    }

    private void updateNotification() {
        status = getAppStatus() ? "前台" : "后台";
        mBuilder.setContentTitle("App处于" + status);
        mBuilder.setContentText(mContentList.get(3));
        notification = mBuilder.build();
        mNotificationManager.notify(NOTICATION_ID, notification);
    }
    private boolean getAppStatus() {
        return BackgroundUtil.isForeground(mContext, 3, mContext.getPackageName());
    }
}
