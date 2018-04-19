package com.example.xjw.climber;

class TimeChange {

    static void changeTime(int[] originalTime) {
        originalTime = switches(originalTime);
        TimeGet.setTimeSecond(originalTime);
    }

    static void changeTime(String sOriginalTime) {
        int[] iOriginalTime = TimePut.stringToInts(sOriginalTime);
        changeTime(iOriginalTime);

    }

    private static int[] switches(int[] time) {
        switch (IsForeground.getTimes()) {
            case 1:
                time[1] -= 4;
                break;
            case 2:
                time[2] -= 40;
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
