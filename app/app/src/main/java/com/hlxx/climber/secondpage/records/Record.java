package com.hlxx.climber.secondpage.records;

import java.io.Serializable;

public class Record implements Serializable {
    static final long serialVersionUID = 1L;
    private int time;
    private boolean finish;
    private int switchTimes;
    private int totalTime;

    public void setTime(int time) {
        this.time = time;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public void setSwitchTimes(int switchTimes) {
        this.switchTimes = switchTimes;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "Record{" +
                "time=" + time +
                ", finish=" + finish +
                ", switchTimes=" + switchTimes +
                ", totalTime=" + totalTime +
                '}';
    }
}
