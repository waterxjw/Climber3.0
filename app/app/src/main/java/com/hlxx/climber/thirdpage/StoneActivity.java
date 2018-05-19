package com.hlxx.climber.thirdpage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hlxx.climber.R;
import com.hlxx.climber.dataTables.Comments;
import com.hlxx.climber.services.AzureServiceAdapter;
import com.hlxx.climber.services.CommentItemAdapter;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.hlxx.climber.services.AzureServiceAdapter.Initialize;

public class StoneActivity extends Activity {

    private Button upload;
    private Button out;
    private EditText user_input;

    MobileServiceClient mClient;

    MobileServiceTable<Comments> mCommentTable ;

    CommentItemAdapter mAdapter;

    Comments mComment = new Comments();

    AzureServiceAdapter mServiceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize(this);
        //mServiceAdapter =AzureServiceAdapter.getInstance();
        //mClient = mServiceAdapter.getClient();
        //init();
        //getComment();
        // 非模态化
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_stone);

        setFinishOnTouchOutside(true);//点击Dialog外退出
        stone_buttons();//按钮操作：upload,out


    }
    public void showAll() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<Comments> results = mCommentTable.execute().get();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mAdapter.clear();
                            for (Comments item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    //按钮
    protected void stone_buttons() {
        Button upload = (Button) findViewById(R.id.upload);
        Button out = (Button) findViewById(R.id.out);
        upload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                user_input = (EditText)findViewById(R.id.user_to_talk);
                mComment.setText(user_input.toString());
                Toast.makeText(StoneActivity.this, "信息已经发出去啦！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        out.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }



    //调取遗迹
    public void getComment(){
        mAdapter = new CommentItemAdapter(this,R.layout.row_list_comment);
        mCommentTable  = mClient.getTable(Comments.class);
        try {
            List<Comments> results = mCommentTable
                    .top(2)
                    .execute()
                    .get();
        }
        catch (InterruptedException e){
            Log.e("...","InterruptError");
        }
        catch (ExecutionException e){
            Log.e("...","ExecutionError");
        }
        showAll();
    }

    //输入框显示
    public void init() {

        user_input = (EditText) findViewById(R.id.user_to_talk);
        SpannableString ss = new SpannableString("刻下你想说的话吧");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        user_input.setHint(new SpannableString(ss));

    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}