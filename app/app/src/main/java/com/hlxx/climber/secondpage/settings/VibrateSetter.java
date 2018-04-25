package com.hlxx.climber.secondpage.settings;

import android.app.Service;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import com.hlxx.climber.secondpage.ClimbingActivity;

import java.lang.ref.WeakReference;

public class VibrateSetter {

    WeakReference<ClimbingActivity> theActivity;
    static boolean isVibrate = false;

    public static void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public VibrateSetter(ClimbingActivity theActivity) {
        this.theActivity = new WeakReference<ClimbingActivity>(theActivity);
    }

    public void setVibrate() {
        //处理震动
        ClimbingActivity activity = theActivity.get();

        Vibrator theVibrator = (Vibrator) activity.getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        if (isVibrate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Log.e("Vibrate", "setVibrate: 震动");
                theVibrator.vibrate(VibrationEffect.createOneShot(1000, 1)); //等待指定时间后开始震动，震动时间,等待震动,震动的时间
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Vibrate", "setVibrate: 不震动");
        }
    }

}
