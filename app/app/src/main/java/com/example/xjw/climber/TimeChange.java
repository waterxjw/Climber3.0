package com.example.xjw.climber;

public class TimeChange {

    public static void changeTime(int[] originalTime) {
        originalTime = switches(originalTime);
        TimeGet.setTimeSecond(originalTime);
    }

    public static void changeTime(String originalTime) {
        int[] iOriginalTime = TimeGet.stringToInts(originalTime);
        iOriginalTime = switches(iOriginalTime);
        TimeGet.setTimeSecond(iOriginalTime);
    }

    private static int[] switches(int[] time) {
        switch (IsForeground.getTimes()) {
            case 1:
                time[1] += 4;
                break;
            case 2:
                time[2] += 20;
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
