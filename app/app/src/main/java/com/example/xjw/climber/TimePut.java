package com.example.xjw.climber;

/**
 * Created by xjw on 2018/4/9.
 */

public class TimePut {
    /**
     * @function 对于long形式的时间格式化为字符串
     * @return  字符串组：时—分—秒
     */
    public static String[] timeOutPut(long hour,long minute ,long second){
        String[] hour_Minute_Second = new String[3];
        if (hour < 10L) {
            hour_Minute_Second[0] = "0" + hour;
        } else {
            hour_Minute_Second[0] = ((Long) hour).toString();
        }

        if (minute < 10L) {
            hour_Minute_Second[1] = new String("0" + minute);
        } else {
            hour_Minute_Second[1] = new String(((Long) minute).toString());
        }

        if (second < 10L) {
            hour_Minute_Second[2] = new String("0" + second);
        } else {
            hour_Minute_Second[2] = new String(((Long) second).toString());
        }

        return hour_Minute_Second;
    }
}
