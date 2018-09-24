package cn.yinxm.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import cn.yinxm.pc.IView;
import cn.yinxm.pc.ProducerConsumerManager;
import cn.yinxm.test.R;

/**
 * Created by yinxuming on 2018/6/8.
 * 生产消费模型，首选BlockingQueueModel-》LockConditionBetterModel-》LockConditionModel-》WaitNotifyModel
 * 1、单生产者，单消费者
 * 2、多生产者，单消费者
 * 3、单生产者，多消费者
 * 4、多生产者，多消费者
 */
public class ConsumerFragment extends Fragment implements IView, View.OnClickListener {

    private EditText etProducer, etConsumer, etCapacity;
    private Button btnStop, btnClear;
    private Button btnStartBQ, btnStartWN, btnStartLC, btnStartBetterLC;
    private TextView tvInfo;


    private ThreadPoolExecutor producerThreadPool;
    private ThreadPoolExecutor consumerThreadPool;


    private LinkedBlockingQueue mLinkedBlockingQueue;

    int producers = 1;
    int consumers = 1;
    int capacity = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consumer, container, false);

        etProducer = (EditText) view.findViewById(R.id.etProducer);
        etConsumer = (EditText) view.findViewById(R.id.etConsumer);
        etCapacity = (EditText) view.findViewById(R.id.etCapacity);
        btnStartBQ = (Button) view.findViewById(R.id.btnStartBQ);
        btnStartWN = (Button) view.findViewById(R.id.btnStartWN);
        btnStartLC = (Button) view.findViewById(R.id.btnStartLC);
        btnStartBetterLC = (Button) view.findViewById(R.id.btnStartBetterLC);

        btnStop = (Button) view.findViewById(R.id.btnStop);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        tvInfo = (TextView) view.findViewById(R.id.tvInfo);

        initData();
        return view;
    }

    private void initData() {

        btnStartBQ.setOnClickListener(this);
        btnStartWN.setOnClickListener(this);
        btnStartLC.setOnClickListener(this);
        btnStartBetterLC.setOnClickListener(this);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearProcessInfo();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProducerConsumerManager.getInstance().stop();
            }
        });

        mLinkedBlockingQueue = new LinkedBlockingQueue();

    }

    private void updateConfig() {
        try {
            producers = Integer.valueOf(etProducer.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            consumers = Integer.valueOf(etConsumer.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            capacity = Integer.valueOf(etCapacity.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("producers=" + producers + ", consumers=" + consumers + ", capacity=" + capacity);
    }

    @Override
    public void clearProcessInfo() {
        if (tvInfo != null) {
            tvInfo.setText("");
        }
    }

    @Override
    public void addProcessInfo(String text) {
        if (tvInfo != null) {
            tvInfo.append(text);
        }
    }

    @Override
    public void onClick(View v) {
        updateConfig();
        switch (v.getId()) {
            case R.id.btnStartBQ:
                ProducerConsumerManager.getInstance().startBlockingQueueModel(producers, consumers, capacity, this);
                break;
            case R.id.btnStartWN:
                ProducerConsumerManager.getInstance().startWaitNotifyModel(producers, consumers, capacity, this);
                break;
            case R.id.btnStartLC:
                break;
            case R.id.btnStartBetterLC:
                break;
        }
    }

    private static class CustomNameThreadFactory implements ThreadFactory {

        private String mThreadName;
        private AtomicInteger mCount;

        public CustomNameThreadFactory(String threadName) {
            mThreadName = threadName;
            mCount = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(mThreadName + "#" + mCount.getAndIncrement());

        }
    }


}
