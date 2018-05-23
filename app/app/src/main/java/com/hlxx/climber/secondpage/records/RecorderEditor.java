package com.hlxx.climber.secondpage.records;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.hlxx.climber.secondpage.records.RecordReader.objectReader;

public class RecorderEditor {
    private File fileMonth;
    private File fileDay;
    private File applicationDir;
    private File timeOfDay;
    private File oneRecord;
    public int[] time = new int[3];
    private boolean finish = false;

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public RecorderEditor(File applicationDir) {
        this.applicationDir = applicationDir;
        fileMonth = new File(applicationDir, String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        fileDay = new File(fileMonth, "" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
        timeOfDay = new File(fileDay, "timeOfDay.hlxx");
    }

    private void timeChange(Record theRecord) {
        try {
            time = RecordReader.timeOfDayGet(timeOfDay);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        time[0]++;
        if (finish) {
            time[1]++;
        }
        time[2] += theRecord.getTotalTime();
        try {
            objectWriter(timeOfDay, time, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void oneRecordAdd(Record theRecord) throws IOException {
        if (creatFiles()) {
            if (finish) {
                time[1] = 1;
            } else {
                time[1] = 0;
            }
            time[0] = 1;
            time[2] = theRecord.getTotalTime();
            objectWriter(timeOfDay, time, false);
        } else {
            timeChange(theRecord);
        }
        recordSort();
        oneRecord = new File(fileDay, time[0] + ".hlxx");
        oneRecord.createNewFile();
        objectWriter(oneRecord, theRecord, false);
    }


    /**
     * +     * @return flase:文件已经存在;true:文件创建成功
     * +     * @throws IOException
     * +
     */
    private boolean creatFiles() throws IOException {
        if (fileDay.mkdirs()) {
            return timeOfDay.createNewFile();
        } else {
            return false;
        }
    }

    public static <T> void objectWriter(File objectPath, T object, boolean append) throws IOException {
        FileOutputStream objectFOS = new FileOutputStream(objectPath, append);
        ObjectOutputStream objectOOS = new ObjectOutputStream(objectFOS);
        objectOOS.writeObject(object);
        objectOOS.close();
        objectFOS.close();
    }

    public void recordSort() {
        File[] filesDay = fileMonth.listFiles();
        for (File file : filesDay) {
            if (!file.equals(fileDay)) {
                File newRecords = new File(fileMonth, "" + file.getName() + ".day");
                try {
                    newRecords.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayList<File> records = new ArrayList<>(Arrays.asList(file.listFiles()));
                File total = new File(file, "timeOfDay.hlxx");
                records.remove(total);
                Records toWrite = new Records();
                try {
                    toWrite.setTime(objectReader(total));
                    total.delete();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                for (File record : records) {
                    try {
                        toWrite.addRecord(objectReader(record));
                        record.delete();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    objectWriter(newRecords, toWrite, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
