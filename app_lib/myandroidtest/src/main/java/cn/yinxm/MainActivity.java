package cn.yinxm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

import cn.yinxm.cache.AppCacheManager;
import cn.yinxm.cache.ICache;
import cn.yinxm.cache.iml.DiskLruCacheIml;
import cn.yinxm.design.DesignPatternActivity;
import cn.yinxm.lib.utils.log.LogUtil;
import cn.yinxm.test.R;
import cn.yinxm.ui.ConcurrentTestActivity;
import cn.yinxm.ui.QRCodeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickConcurrent(View view) {
        startActivity(new Intent(MainActivity.this, ConcurrentTestActivity.class));
    }

    public void clickQRcode(View view) {
        startActivity(new Intent(MainActivity.this, QRCodeActivity.class));
    }

    public void clickDesign(View view) {
        startActivity(new Intent(MainActivity.this, DesignPatternActivity.class));
    }

    public void clickCacheTest(View view) throws IOException {
        ICache cache = new DiskLruCacheIml(AppCacheManager.getDiskCacheDir(getApplicationContext(), "media"),
                1, 1, Integer.MAX_VALUE);
        AppCacheManager.getInstance().init(getApplicationContext(), cache);
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppCacheManager.getInstance().saveCache("test1", "test1");
                AppCacheManager.getInstance().saveCache("test2", "test2");
                AppCacheManager.getInstance().saveCache("test3", "test3");
                AppCacheManager.getInstance().saveCache("test4", "test4").flush();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.d("1="+AppCacheManager.getInstance().loadCache("test1"));
                LogUtil.d("2="+AppCacheManager.getInstance().loadCache("test2"));
                LogUtil.d("3="+AppCacheManager.getInstance().loadCache("test3"));
                LogUtil.d("4="+AppCacheManager.getInstance().loadCache("test4"));
            }
        }).start();

    }
}
