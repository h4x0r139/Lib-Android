package cn.yinxm.design;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.yinxm.design.object.MyPooledClassTest;
import cn.yinxm.test.R;

public class DesignPatternActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_pattern);
    }

    public void clickObjectPool(View view) {
//        MyPooledClassTest.test1();
//        MyPooledClassTest.test2();
        MyPooledClassTest.test3();
    }
}
