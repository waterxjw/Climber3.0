package com.hlxx.climber.secondpage.records;

import java.io.Serializable;
import java.util.ArrayList;

public class Records implements Serializable {
    static final long serialVersionUID = 2L;
    private int times ;
    private ArrayList<Record> theRecord;

    public Records() {
        this.theRecord = new ArrayList<>();
    }

    public ArrayList<Record> getTheRecord() {
        return theRecord;
    }

    public int getTimes() {
        return times;
    }

    @Override
    public String toString() {
        StringBuilder aStringBuilder = new StringBuilder();
        for (Record aRecord : theRecord) {
            aStringBuilder.append(aRecord).append("\n");
        }
        return "times:" + times + ",\n theRecord:" + aStringBuilder + '}';
    }

    public Records(int times, ArrayList<Record> theRecord) {
        this.times = times;
        this.theRecord = theRecord;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void addRecord(Record newRecord) {
        this.theRecord.add(newRecord);
    }


}
