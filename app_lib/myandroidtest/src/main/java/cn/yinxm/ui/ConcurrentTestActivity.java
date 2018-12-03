package cn.yinxm.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.yinxm.test.R;


/**
 * 并发队列测试
 */
public class ConcurrentTestActivity extends AppCompatActivity {

    Fragment frequentClicksFragment, consumerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concurrent_test);

        frequentClicksFragment = new FrequentClicksFragment();
        consumerFragment = new ConsumerFragment();
    }

    public void clickFrequent(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, frequentClicksFragment).commit();
    }

    public void clickConsumer(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, consumerFragment).commit();
    }
}
