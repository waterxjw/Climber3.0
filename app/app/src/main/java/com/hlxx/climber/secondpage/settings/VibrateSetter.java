package com.hlxx.climber.secondpage.settings;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.hlxx.climber.R;
import com.hlxx.climber.secondpage.ClimbingActivity;

import java.lang.ref.WeakReference;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.hlxx.climber.firstpage.setting.SharedPreferenceUtils.getBoolean;

public class VibrateSetter {
    private WeakReference<ClimbingActivity> theActivity;
    private static boolean isVibrate;


    public VibrateSetter(ClimbingActivity theActivity) {
        this.theActivity = new WeakReference<>(theActivity);
    }

    public void makeVibrate(boolean finish) {

        ClimbingActivity activity = theActivity.get();
        //处理震动
        isVibrate = getBoolean(activity, "remind");

        NotificationManager manager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel mNotificationChannel;
        NotificationChannel mNotificationChannel_NoVibrate;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel("Vibrate", "Vibrate", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationChannel.setDescription("Exit");
            mNotificationChannel.enableLights(true);
            mNotificationChannel.enableVibration(true);
            mNotificationChannel.setVibrationPattern(new long[]{0, 1500, 500, 1000});
            manager.createNotificationChannel(mNotificationChannel);

            mNotificationChannel_NoVibrate = new NotificationChannel("NoVibrate", "NoVibrate", NotificationManager.
                    IMPORTANCE_LOW);
            mNotificationChannel_NoVibrate.setDescription("Exit");
            mNotificationChannel_NoVibrate.enableLights(true);
            mNotificationChannel_NoVibrate.enableVibration(false);
            mNotificationChannel.setVibrationPattern(new long[]{} );
            manager.createNotificationChannel(mNotificationChannel_NoVibrate);
        }

        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (isVibrate) {
                builder = new Notification.Builder(activity, "Vibrate");
            } else {
                builder = new Notification.Builder(activity, "NoVibrate");
            }
        } else {
            builder = new Notification.Builder(activity).setLights(Color.WHITE, 3000, 500);
            if (isVibrate) {
                builder.setVibrate(new long[]{0, 1500, 500, 1000});
            }
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher_round));
        if (finish) {
            builder.setTicker("恭喜");
            builder.setContentTitle("成功");
            builder.setContentText("你这次坚持成功了！");
        } else {
            builder.setTicker("可惜");
            builder.setContentTitle("失败");
            builder.setContentText("切换过多，下次加油！");
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(5, notification);
    }
}

