package cn.yinxm.design.object;

import java.util.ArrayList;
import java.util.List;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * 对象池测试
 * <p>
 * Created by yinxuming on 2018/7/19.
 */
public class MyPooledClassTest {

    /**
     * 只消费：每次都是new出来的
     */
    public static void test1() {
        for (int i=0; i<15; i++) {
            MyPooledClass object = MyPooledClass.obtain();
            LogUtil.d("i="+i+", object="+object);
        }
    }

    /**
     * 边消费边释放：下一次会复用上一次的
     */
    public static void test2() {
        for (int i=0; i<15; i++) {
            MyPooledClass object = MyPooledClass.obtain();
            LogUtil.d("i="+i+", object="+object);
            object.recycle();
        }
//        i=0, object=cn.yinxm.design.object.MyPooledClass@41be63a0
//        i=1, object=cn.yinxm.design.object.MyPooledClass@41be63a0
    }

    /**
     *
     * 边消费边释放，对象池容量测试：下一次会复用上一次的，超过容量的new
     */
    public static void test3() {
        List<MyPooledClass> tempList = new ArrayList<>();
        for (int i=0; i<15; i++) {
            MyPooledClass object = MyPooledClass.obtain();
            tempList.add(object);
            LogUtil.d("i="+i+", object="+object);
//            object.recycle();
        }

        //开始回收对象
        for (MyPooledClass obj : tempList) {
            obj.recycle();
//            obj.recycle(); // 不能重复调用
        }


        LogUtil.d("对象池对象全部构建完成，下面只消费，可以看到从10以后的对象就没法复用了");
        for (int i=0; i<15; i++) {
            MyPooledClass object = MyPooledClass.obtain();
            LogUtil.d("i="+i+", object="+object);
//            object.recycle();
        }
//        i=0, object=cn.yinxm.design.object.MyPooledClass@41be63a0
//        i=1, object=cn.yinxm.design.object.MyPooledClass@41be63a0
    }
}
