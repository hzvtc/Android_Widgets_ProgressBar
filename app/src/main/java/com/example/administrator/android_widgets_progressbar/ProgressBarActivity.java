package com.example.administrator.android_widgets_progressbar;
/*
继承Thread类和实现Runnable接口实现多线程
一个是多个线程分别完成自己的任务，一个是多个线程共同完成一个任务
 */

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarActivity extends AppCompatActivity implements View.OnClickListener {
    /*自定义Handler的信息代码 用来识别不同的时间*/
    public static final int GUI_START_NOTIFIER = 0x108;
    public static final int GUI_STOP_NOTIFIER = 0x109;

    private int progress;
    /*线程运行次数*/
    private int count = 0;

    private ProgressBar mProgressBar;
    private Button mButton;
    private TextView progress_detail_info;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GUI_START_NOTIFIER:
                    if (!Thread.currentThread().isInterrupted()) {
                        /*设置为当前进度*/
                        mProgressBar.setProgress(progress);
                        /*将显示信息显示在屏幕上*/
                        progress_detail_info.setText(getResources().getText(R.string.str_progress_loading) + "(" + progress + "%)\n" + "Progress:" +
                                Integer.toString(mProgressBar.getProgress()) + "\n" + "Indeterminate:" + Boolean.toString(mProgressBar.isIndeterminate()));
                        mHandler.postDelayed(progressThread, 1000);
                    }
                    break;
                case GUI_STOP_NOTIFIER:
                    /*隐藏进度条*/
                    mProgressBar.setVisibility(View.GONE);
                    /*显示加载成功*/
                    progress_detail_info.setText(R.string.str_progress_done);
                    /*停止线程*/
                    Thread.currentThread().interrupt();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable progressThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        findViewById();
        setListener();
        Init();
        /*滚动条自动在最大值和最小值之间来回移动 true则是根据当前进度*/
        mProgressBar.setIndeterminate(false);
    }

    /*线程中不断更新进度条的进度*/
    class ProgressThread implements Runnable {
        @Override
        public void run() {
            count++;
            progress = count * 20;
                    /*线程运行五次后停止*/
            if (count == 5) {
                Message msg = new Message();
                msg.what = GUI_STOP_NOTIFIER;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = GUI_START_NOTIFIER;
                mHandler.sendMessage(msg);
            }
        }
    }

    private void Init() {
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
    }

    private void setListener() {
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.mButton:
                mProgressBar.setVisibility(View.VISIBLE);
                /*启动线程*/
                progressThread = new ProgressThread();
                mHandler.post(progressThread);
                break;
        }
    }

    private void findViewById() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButton = (Button) findViewById(R.id.mButton);
        progress_detail_info = (TextView) findViewById(R.id.progress_detail_info);
    }
}
