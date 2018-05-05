package com.hlxx.climber.secondpage.records;
import java.io.*;
import java.util.Calendar;

import static com.hlxx.climber.secondpage.settings.IsForeground.getTimes;
/**
 * Created by xjw on 2018/5/5.
 */

public class RecorderEditor {
    private File fileMonth;
    private File fileDay;
    private File applicationDir;
    private File timeOfDay;
    private File oneRecord;
    public int time;

    public RecorderEditor(File applicationDir) {
        this.applicationDir = applicationDir;
        fileMonth = new File(applicationDir, String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        fileDay = new File(fileMonth, "" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1));
        timeOfDay = new File(fileDay, "timeOfDay.hlxx");
    }

    private void timeChange() {
        try {
            time = RecordReader.timeOfDayGet(timeOfDay);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            time = 0;
        }
        time++;

        try {
            objectWriter(timeOfDay, time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void oneRecordAdd(Record theRecord) throws IOException {
        timeChange();
        creatFiles();
        oneRecord = new File(fileDay, time + ".hlxx");
        oneRecord.createNewFile();
        objectWriter(oneRecord, theRecord);
    }


    /**
     +     * @return flase:文件已经存在;true:文件创建成功
     +     * @throws IOException
     +     */
    private boolean creatFiles() throws IOException {
        if (fileDay.mkdirs()) {
            return timeOfDay.createNewFile();
        } else {
            return false;
        }
    }

    public static <T> void objectWriter(File objectPath, T object) throws IOException {
        FileOutputStream objectFOS = new FileOutputStream(objectPath);
        ObjectOutputStream objectOOS = new ObjectOutputStream(objectFOS);
        objectOOS.writeObject(object);
        objectOOS.close();
        objectFOS.close();
    }
}
