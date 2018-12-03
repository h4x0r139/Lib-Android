package cn.yinxm.mylog;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import cn.yinxm.lib.utils.log.LogUtil;
import cn.yinxm.lib.utils.log.log4j.ConfigureLog4J;
import de.mindpipe.android.logging.log4j.LogConfigurator;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLog4j();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("yinxm", "getExternalStorageDirectory="+ Environment.getExternalStorageDirectory());
        LogUtil.d("yinxm", "getExternalStoragePublicDirectory="+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }

    private void initLog4j() {
        //加载配置
        ConfigureLog4J configureLog4J=new ConfigureLog4J(getApplicationContext());

        configureLog4J.setFileName("testlog.log");
        configureLog4J.setLogEnabled(true);
        LogConfigurator logConfigurator = configureLog4J.getDefaultLogConfig();
        if (logConfigurator != null) {
            logConfigurator.setUseLogCatAppender(false);

            configureLog4J.init(logConfigurator);
        }

        for (int i=0; i<1000; i++) {
            LogUtil.d("yinxm", "不知道呀就是测试一下啊");
        }
    }

}
