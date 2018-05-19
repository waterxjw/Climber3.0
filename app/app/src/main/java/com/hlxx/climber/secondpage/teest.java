package com.hlxx.climber.secondpage;

import com.hlxx.climber.secondpage.records.Record;
import com.hlxx.climber.secondpage.records.Records;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.hlxx.climber.secondpage.records.RecordReader.recordsReaded;

public class teest {
    public static void main(String[] args) {
        File file = new File(getFilesDir(), "5");
        File[] files = file.listFiles();
        for (File aFile1 : files) {
            if (aFile1.isFile()) {
                Records records = null;
                try {
                    records = recordsReaded(aFile1);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                int total = records.getTimes();
                ArrayList<Record> recordArrayList = records.getTheRecord();
                for (Record record :recordArrayList) {



                }
            }
        }

    }
}
