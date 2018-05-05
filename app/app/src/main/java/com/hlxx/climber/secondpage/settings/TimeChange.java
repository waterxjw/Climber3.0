package com.hlxx.climber.secondpage.settings;

public class TimeChange {

    public static void changeTime(int[] originalTime) {
        originalTime = switches(originalTime);
        TimeGet.setTimeSecond(originalTime);
    }

    public static void changeTime(String sOriginalTime) {
        int[] iOriginalTime = TimePut.stringToInts(sOriginalTime);
        changeTime(iOriginalTime);

    }

    private static int[] switches(int[] time) {
        switch (IsForeground.getTimes()) {
            case 1:
                time[1] -= 4;
                break;
            case 2:
                time[2] -= 50;
                break;
            case 3:
                time[2] += 100;
                break;
            default:
                break;
        }
        return time;
    }
}
