package com.hlxx.climber.secondpage.records;
import java.io.*;
import java.util.Calendar;
/**
 * Created by xjw on 2018/5/5.
 */

public class RecordReader {
    private File fileMonth;
    private File fileDay;
    private File applicationDir;
    private File timeOfDay;


    public RecordReader(File applicationDir) {
        this.applicationDir = applicationDir;
        fileMonth = new File(applicationDir, String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        fileDay = new File(fileMonth, "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        timeOfDay = new File(fileDay, "timeOfDay.hlxx");
    }

    public Record oneRecordReaded(File recordPath) throws IOException, ClassNotFoundException {
        return objectReader(recordPath);
    }

    public static int timeOfDayGet(File timeOfDayPath) throws IOException, ClassNotFoundException {
        return (int)objectReader(timeOfDayPath);
    }


    private static  <T> T objectReader(File objectPath) throws IOException, ClassNotFoundException {
        FileInputStream objectFIS = new FileInputStream(objectPath);
        ObjectInputStream objectOIS = new ObjectInputStream(objectFIS);
        T object = (T) objectOIS.readObject();
        objectOIS.close();
        objectFIS.close();
        return object;
    }
}
