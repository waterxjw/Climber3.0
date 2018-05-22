package com.hlxx.climber.firstpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.hlxx.climber.R;
import com.hlxx.climber.secondpage.records.Record;
import com.hlxx.climber.secondpage.records.Records;

import java.io.File;
import java.io.IOException;

import static com.hlxx.climber.secondpage.records.RecordReader.oneRecordReaded;
import static com.hlxx.climber.secondpage.records.RecordReader.recordsReaded;
import static com.hlxx.climber.secondpage.records.RecordReader.timeOfDayGet;

public class ToReadFile extends AppCompatActivity {
    private static File filePath;
    static Button aButton;
    static EditText aEditText;
    static TextView aTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_read_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        aButton = findViewById(R.id.search);
        aEditText = findViewById(R.id.inPut);
        aTextView = findViewById(R.id.outPut);

        aTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        filePath = getFilesDir();
        File[] files = filePath.listFiles();
        StringBuilder outPuts = new StringBuilder();
        for (File file : files) {
            outPuts.append(file.getName()).append("\n");
        }
        aTextView.setText(outPuts.toString());

        aButton.setOnClickListener((view) -> {
            action();
        });

        aEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                action();
            }
            return false;
        });

    }

    private static void changeFilePath(File newPath) {
        filePath = newPath;
    }

    private static void action() {
        String command = aEditText.getText().toString();
        File toSearch = new File(filePath, command);
        if (toSearch.isDirectory()) {
            changeFilePath(toSearch);
            File[] toSearchFile = toSearch.listFiles();
            StringBuilder outPut = new StringBuilder();
            outPut.append("..").append("\n");
            for (File file : toSearchFile) {
                outPut.append(file.getName()).append("\n");
            }
            aTextView.setText(outPut.toString());
        } else if (toSearch.isFile()) {
            changeFilePath(toSearch);
            if (command.equals("timeOfDay.hlxx")) {
                try {
                    int[] times = timeOfDayGet(toSearch);
                    aTextView.setText("" + times[0]+"\n"+times[1]);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Record aRecord = oneRecordReaded(toSearch);
                    aTextView.setText(aRecord.toString());
                } catch (Exception e) {
                    try {
                        Records aRecords = recordsReaded(toSearch);
                        aTextView.setText(aRecords.toString());
                    } catch (IOException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else if (command.equals("..")) {
            toSearch = filePath.getParentFile();
            changeFilePath(toSearch);
            File[] toSearchFile = toSearch.listFiles();
            StringBuilder outPut = new StringBuilder();
            for (File file : toSearchFile) {
                outPut.append(file.getName()).append("\n");
            }
            aTextView.setText(outPut.toString());
        }
        aEditText.setText("");
    }
}