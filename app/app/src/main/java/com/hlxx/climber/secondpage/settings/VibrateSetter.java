package com.hlxx.climber.secondpage.settings;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import com.hlxx.climber.R;
import com.hlxx.climber.secondpage.ClimbingActivity;
import com.hlxx.climber.thirdpage.EndingActivity;

import java.lang.ref.WeakReference;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class VibrateSetter {
    private WeakReference<ClimbingActivity> theActivity;
    private static boolean isVibrate = false;

    public static void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public VibrateSetter(ClimbingActivity theActivity) {
        this.theActivity = new WeakReference<ClimbingActivity>(theActivity);
    }

    public void setVibrate() {
        if (true) {

            ClimbingActivity activity = theActivity.get();
            //处理震动
            NotificationManager manager =
                    (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel mNotificationChannel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationChannel = new NotificationChannel("default", "verible", NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationChannel.setDescription("Exit");
                mNotificationChannel.enableLights(true);
                mNotificationChannel.enableVibration(true);
                mNotificationChannel.setVibrationPattern(new long[]{0, 1500});
                manager.createNotificationChannel(mNotificationChannel);
            }
            Notification.Builder builder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new Notification.Builder(activity, "default");
            } else {
                builder = new Notification.Builder(activity)
                        .setLights(Color.WHITE, 3000, 500)
                        .setVibrate(new long[]{0, 1500});
            }

            Intent intent=new Intent(activity,EndingActivity.class);
            PendingIntent ClickPending = PendingIntent.getActivity(activity, 0, intent, 0);
            builder.setContentIntent(ClickPending);


            //设置 通知 图标
            builder.setSmallIcon(R.mipmap.ic_launcher_round);
            //设置 通知 显示标题
            builder.setTicker("恭喜");
            //设置 通知栏 标题
            builder.setContentTitle("成功");
            //设置 通知内容
            builder.setContentText("你这次坚持成功了！");
            /*builder.setLights(Color.WHITE, 3000, 500);
            //设置 提醒 声音/震动/指示灯
            builder.setVibrate(new long[]{0, 1500, 0, 0});//builder.setDefaults(Notification.DEFAULT_ALL);
            //.setDefaults(Notification.DEFAULT_VIBRATE);*/
            Notification notification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                notification = builder.build();
                notification.flags = Notification.FLAG_SHOW_LIGHTS;
                notification.flags = Notification.FLAG_AUTO_CANCEL;
            }
            manager.notify(5, notification);
        }
    }
}
