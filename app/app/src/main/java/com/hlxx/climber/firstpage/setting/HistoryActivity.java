package com.hlxx.climber.firstpage.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hlxx.climber.secondpage.records.Record;
import com.hlxx.climber.secondpage.records.RecordReader;
import com.hlxx.climber.secondpage.records.Records;
import com.ruesga.timelinechart.TimelineChartView;
import com.ruesga.timelinechart.TimelineChartView.OnColorPaletteChangedListener;
import com.ruesga.timelinechart.TimelineChartView.OnSelectedItemChangedListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.hlxx.climber.R;

public class HistoryActivity extends AppCompatActivity {

    private InMemoryCursor mCursor;

    private Toolbar mToolbar;
    private TimelineChartView mGraph;
    private TextView mTimestamp;
    private TextView[][] mSeries;
    private TextView sumOfTime;
    private View[] mSeriesColors;
    private TextView concentrateTimes,failTimes,levelInAverage;
    private Calendar mStart;
    private HashMap<String,String[]> detailData=new HashMap<String,String[]>();
    private HashMap<String,String[][]> seriesData=new HashMap<String,String[][]>();
    private String [][] seriesString;

    private LayoutInflater inflater;
    private final SimpleDateFormat DATETIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat HOURTIME_FORMATTER =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat MINUTETIME_FORMATTER =
            new SimpleDateFormat("mm:ss", Locale.getDefault());
    private final NumberFormat NUMBER_FORMATTER = new DecimalFormat("#0.00");
    //private final String[] COLUMN_NAMES = {"timestamp", "Serie 1", "Serie 2", "Serie 3"};
    private final String[] COLUMN_NAMES = {"timestamp","sum"};

    private final int[] MODES = {
            TimelineChartView.GRAPH_MODE_BARS,
            TimelineChartView.GRAPH_MODE_BARS_STACK,
            TimelineChartView.GRAPH_MODE_BARS_SIDE_BY_SIDE};
    private final String[] MODES_TEXT = {
            "GRAPH_MODE_BARS",
            "GRAPH_MODE_BARS_STACK",
            "GRAPH_MODE_BARS_SIDE_BY_SIDE"};
    private int mMode;
    private int mSound;
    private boolean mInLiveUpdate;

    private static final int LIVE_UPDATE_INTERVAL = 2;
    private Handler mHandler;
    private final Runnable mLiveUpdateTask = new Runnable() {
        @Override
        public void run() {
            mStart.setTimeInMillis(System.currentTimeMillis());
            mStart.set(Calendar.MILLISECOND, 0);
            mCursor.add(createItem(mStart.getTimeInMillis()));
            mHandler.postDelayed(this, LIVE_UPDATE_INTERVAL * 1000);
        }
    };

   /* private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mode:
                    mMode++;
                    if (mMode >= MODES.length) {
                        mMode = 0;
                    }
                    mGraph.setGraphMode(MODES[mMode]);
                    Toast.makeText(HistoryActivity.this, MODES_TEXT[mMode], Toast.LENGTH_SHORT).show();
                    break;
                case R.id.footer:
                    mGraph.setShowFooter(!mGraph.isShowFooter());
                    break;
                case R.id.color:
                    int color = Color.rgb(random(255), random(255), random(255));
                    mToolbar.setBackgroundColor(color);
                    mGraph.setBackgroundColor(color);
                    mGraph.setGraphAreaBackground(color);
                    break;
                case R.id.sound:
                    mSound++;
                    if (mSound > 2) {
                        mSound = 0;
                    }
                    if (mSound == 0) {
                        mGraph.setPlaySelectionSoundEffect(false);
                    } else if (mSound == 1) {
                        mGraph.setSelectionSoundEffectSource(0);
                        mGraph.setPlaySelectionSoundEffect(true);
                    } else {
                        mGraph.setSelectionSoundEffectSource(R.raw.selection_effect);
                    }
                    break;
                case R.id.reload:
                    mCursor = createInMemoryCursor();
                    mGraph.observeData(mCursor);
                    break;
                case R.id.live_update:
                    mHandler.removeCallbacks(mLiveUpdateTask);
                    mCursor.removeAll();
                    if (!mInLiveUpdate) {
                        mGraph.setFollowCursorPosition(true);
                        mHandler.post(mLiveUpdateTask);
                    } else {
                        mGraph.setFollowCursorPosition(false);
                        createRandomData(mCursor);
                    }
                    mInLiveUpdate = !mInLiveUpdate;
                    findViewById(R.id.reload).setEnabled(!mInLiveUpdate);
                    findViewById(R.id.add).setEnabled(!mInLiveUpdate);
                    findViewById(R.id.update).setEnabled(!mInLiveUpdate);
                    findViewById(R.id.delete).setEnabled(!mInLiveUpdate);
                    break;
                case R.id.add:
                    mStart.add(Calendar.HOUR_OF_DAY, 1);
                    mCursor.add(createItem(mStart.getTimeInMillis()));
                    break;
                case R.id.delete:
                    int position = mCursor.getCount() - 1;
                    mCursor.remove(position);
                    mStart.add(Calendar.HOUR_OF_DAY, -1);
                    break;
                case R.id.update:
                    position = mCursor.getCount() - 1;
                    mCursor.update(position, createItem(mStart.getTimeInMillis()));
                    break;
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());

        setContentView(R.layout.activity_history);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("历史记录");
        }

        // Buttons
        /*Button button = findViewById(R.id.add);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.delete);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.update);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.reload);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.live_update);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.mode);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.footer);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.color);
        button.setOnClickListener(mClickListener);
        button = findViewById(R.id.sound);
        button.setOnClickListener(mClickListener);

        button = findViewById(R.id.mode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode++;
                if (mMode >= MODES.length) {
                    mMode = 0;
                }
                mGraph.setGraphMode(MODES[mMode]);
                Toast.makeText(HistoryActivity.this, MODES_TEXT[mMode], Toast.LENGTH_SHORT).show();
            }
        });*/


        // Create random data
        mCursor = createInMemoryCursor();

        // Retrieve the data and inject the cursor so the view can start observing changes
        mGraph = findViewById(R.id.graph);
        mMode = mGraph.getGraphMode();
        mSound = mGraph.isPlaySelectionSoundEffect() ? 1 : 0;
        mSound += mGraph.getSelectionSoundEffectSource() != 0 ? 1 : 0;

        // Setup info view

        mTimestamp = findViewById(R.id.item_timestamp);

        inflater = LayoutInflater.from(this);
        //mSeries = new TextView[COLUMN_NAMES.length - 1];
        sumOfTime=findViewById(R.id.sum_time);
        //mSeriesColors = new View[COLUMN_NAMES.length - 1];
        /*for (int i = 1; i < COLUMN_NAMES.length; i++) {
            View v = inflater.inflate(R.layout.serie_item_layout, series, false);
            TextView title = v.findViewById(R.id.title);
            title.setText(getString(R.string.item_name, COLUMN_NAMES[i]));
            mSeries[i - 1] = v.findViewById(R.id.value);
            mSeries[i - 1].setText("-");
            mSeriesColors[i - 1] = v.findViewById(R.id.color);
            mSeriesColors[i - 1].setBackgroundColor(Color.TRANSPARENT);
            series.addView(v);
        }*/
        concentrateTimes=findViewById(R.id.concentrationtimes);
        failTimes=findViewById(R.id.failtimes);
        levelInAverage=findViewById(R.id.levelinaverage);
        concentrateTimes.setText("_");
        failTimes.setText("_");
        levelInAverage.setText("_");

        // Setup graph view data and start listening
        mGraph.addOnSelectedItemChangedListener(new OnSelectedItemChangedListener() {
            @Override
            public void onSelectedItemChanged(TimelineChartView.Item selectedItem, boolean fromUser) {

                ViewGroup series = findViewById(R.id.item_series);
                series.removeAllViews();
                seriesString=seriesData.get(DATETIME_FORMATTER.format(selectedItem.mTimestamp));
                mTimestamp.setText(DATETIME_FORMATTER.format(selectedItem.mTimestamp));
                //for (int i = 0; i < mSeries.length; i++) {
                    //mSeries[i].setText(NUMBER_FORMATTER.format(selectedItem.mSeries[i]));
                ;
                sumOfTime.setText((detailData.get(DATETIME_FORMATTER.format(selectedItem.mTimestamp)))[3]);
                concentrateTimes.setText((detailData.get(DATETIME_FORMATTER.format(selectedItem.mTimestamp)))[0]);
                failTimes.setText((detailData.get(DATETIME_FORMATTER.format(selectedItem.mTimestamp)))[1]);
                levelInAverage.setText((detailData.get(DATETIME_FORMATTER.format(selectedItem.mTimestamp)))[2]);
                if (seriesString==null){
                    mSeries=new TextView[0][5];
                }else {
                    mSeries=new TextView[seriesString.length][5];
                    for (int i=0;i<seriesString.length;i++){
                        View v = inflater.inflate(R.layout.serie_item_layout, series, false);
                        mSeries[i][0]=v.findViewById(R.id.singleTime);
                        mSeries[i][1]=v.findViewById(R.id.actualConcentrateTime);
                        mSeries[i][2]=v.findViewById(R.id.planConcentrateTime);
                        mSeries[i][3]=v.findViewById(R.id.level);
                        mSeries[i][4]=v.findViewById(R.id.isSuccess);
                        mSeries[i][0].setText(seriesString[i][0]+"     ");
                        mSeries[i][1].setText(seriesString[i][1]);
                        mSeries[i][2].setText(seriesString[i][2]);
                        mSeries[i][3].setText(seriesString[i][3]);
                        mSeries[i][4].setText("         "+seriesString[i][4]);
                        series.addView(v);
                    }
                }



            }

            @Override
            public void onNothingSelected() {

                /*for (TextView v : mSeries) {
                    v.setText("-");
                }*/
            }
        });
        mGraph.addOnColorPaletteChangedListener(new OnColorPaletteChangedListener() {
            @Override
            public void onColorPaletteChanged(int[] palette) {
                /*int count = mSeriesColors.length;
                for (int i = 0; i < count; i++) {
                    mSeriesColors[i].setBackgroundColor(palette[i]);
                }*/
            }
        });
        mGraph.setOnClickItemListener(new TimelineChartView.OnClickItemListener() {
            @Override
            public void onClickItem(TimelineChartView.Item item, int serie) {
                String timestamp = DATETIME_FORMATTER.format(item.mTimestamp);
                Toast.makeText(HistoryActivity.this, "onClickItem => " + timestamp
                        + ", serie: " + serie, Toast.LENGTH_SHORT).show();
                mGraph.smoothScrollTo(item.mTimestamp);
            }
        });
        mGraph.setOnLongClickItemListener(new TimelineChartView.OnLongClickItemListener() {
            @Override
            public void onLongClickItem(TimelineChartView.Item item, int serie) {
                String timestamp = DATETIME_FORMATTER.format(item.mTimestamp);
                Toast.makeText(HistoryActivity.this, "onLongClickItem => " + timestamp
                        + ", serie: " + serie, Toast.LENGTH_SHORT).show();

            }
        });
        mGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HistoryActivity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
        mGraph.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(HistoryActivity.this, "onLongClick", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mGraph.observeData(mCursor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int random(int max) {
        return (int) (Math.random() * (max + 1));
    }

    private Object[] createItem(long timestamp) {
        Object[] item = new Object[COLUMN_NAMES.length];
        item[0] = timestamp;
        for (int i = 1; i < COLUMN_NAMES.length; i++) {
            item[i] = random(9999);

        }
        return item;
    }

    private InMemoryCursor createInMemoryCursor() {
        InMemoryCursor cursor = new InMemoryCursor(COLUMN_NAMES);
        //createRandomData(cursor);
        createData(cursor);
        return cursor;
    }

    /*private void createRandomData(InMemoryCursor cursor) {
        List<Object[]> data = new ArrayList<>();
        String[][] seriesdata;

        Calendar today = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        Calendar temporary = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        mStart = (Calendar) today.clone();
        mStart.add(Calendar.DAY_OF_MONTH, -30);
        while (mStart.compareTo(today) <= 0) {
            seriesdata=new String[random(5)][5];
            for (int i=0;i<seriesdata.length;i++){

                temporary.set(Calendar.HOUR_OF_DAY,random(10));
                seriesdata[i][0]=HOURTIME_FORMATTER.format(temporary.getTimeInMillis());
                seriesdata[i][1]=Integer.toString(random(10));
                seriesdata[i][2]=Integer.toString(random(10));
                seriesdata[i][3]=Integer.toString(random(100))+"%";
                seriesdata[i][4]=(random(2)==0)?"成功":"失败";
            }
            seriesData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),seriesdata);
            detailData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),new int[]{random(10),random(5),random(100)});
            data.add(createItem(mStart.getTimeInMillis()));
            mStart.add(Calendar.DAY_OF_MONTH, 1);
        }
        mStart.add(Calendar.DAY_OF_MONTH, -1);
        cursor.addAll(data);
    }*/
    private void createData(InMemoryCursor cursor){
        List<Object[]> data = new ArrayList<>();
        String[][] seriesdata;
        String[] detaildata;
        Calendar today = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        Calendar temporary = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        mStart = (Calendar) today.clone();
        mStart.add(Calendar.DAY_OF_MONTH, -today.get(Calendar.DAY_OF_MONTH)+1);
        while (mStart.compareTo(today) <= 0){
            detaildata=new String[4];
            File file=new File(getFilesDir(),Integer.toString(today.get(Calendar.MONTH)+1));
            File[] files=file.listFiles();
            File temp=null;
            if (mStart.compareTo(today)==0){
                seriesdata=null;
                detaildata[0]="-";
                detaildata[1]="-";
                detaildata[2]="-";
                detaildata[3]="今天还没有结束呢，继续专注吧！";

                seriesData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),seriesdata);
                detailData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),detaildata);
                Object[] item = new Object[2];
                item[0] = mStart.getTimeInMillis();
                try{
                    item[1]=Integer.parseInt(detaildata[3]);
                }catch (Exception e){
                    item[1]=0;
                }
                data.add(item);
                mStart.add(Calendar.DAY_OF_MONTH, 1);
                break;
            }
            try{
                for (File aFile:files){
                    if (aFile.getName().equals(Integer.toString(mStart.get(Calendar.DAY_OF_MONTH))+".hlxx")){
                        temp=aFile;
                        break;
                    }
            }

            }catch(Exception e){

            }
            if (temp==null){
                detaildata[0]="0";
                detaildata[1]="0";
                detaildata[2]="0%";
                detaildata[3]="0";
                seriesdata=null;
                seriesData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),seriesdata);
                detailData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),detaildata);
            }else{
                Records records=null;
                try{
                    records= RecordReader.recordsReaded(temp);
                }catch (Exception e){
                    e.getStackTrace();
                }
                detaildata[0]=Integer.toString(records.getTimes());
                detaildata[1]="-";
                detaildata[2]="-";
                detaildata[3]="-";
                if (records.getTimes()>0){
                    seriesdata=new String[records.getTimes()][5];
                    ArrayList<Record> recordArrayList=records.getTheRecord();
                    for (int i=0;i<recordArrayList.size();i++){
                        Record record=recordArrayList.get(i);
                        seriesdata[i][0]="00:00";
                        temporary.set(Calendar.HOUR_OF_DAY, 0);
                        temporary.set(Calendar.MINUTE, 0);
                        temporary.set(Calendar.SECOND, 0);
                        temporary.set(Calendar.MILLISECOND, 0);
                        temporary.set(Calendar.SECOND,record.getTotalTime());
                        seriesdata[i][1]=MINUTETIME_FORMATTER.format(((Calendar)temporary.clone()).getTimeInMillis());
                        temporary.set(Calendar.SECOND,record.getTimeSetted());
                        seriesdata[i][2]=MINUTETIME_FORMATTER.format(((Calendar)temporary.clone()).getTimeInMillis());
                        seriesdata[i][3]="85%";
                        seriesdata[i][4]=record.isFinish()?"成功":"失败";
                    }
                    seriesData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),seriesdata);
                    detailData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),detaildata);
                }else {
                    seriesdata=null;
                    seriesData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),seriesdata);
                    detailData.put(DATETIME_FORMATTER.format(mStart.getTimeInMillis()),detaildata);
                }
                }

            Object[] item = new Object[2];
            item[0] = mStart.getTimeInMillis();
            try{
                item[1]=Integer.parseInt(detaildata[3]);
            }catch (Exception e){
                item[1]=0;
            }
            data.add(item);
            mStart.add(Calendar.DAY_OF_MONTH, 1);
        }
        mStart.add(Calendar.DAY_OF_MONTH, -1);
        cursor.addAll(data);
    }
}
