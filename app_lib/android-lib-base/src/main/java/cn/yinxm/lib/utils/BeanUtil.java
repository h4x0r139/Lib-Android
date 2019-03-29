package cn.yinxm.lib.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by yinxm on 2017/3/15.
 * 功能: Bean工具类
 * 两种方式
 * 1、字节数组写入写出，枚举是否会有问题？不会有问题
 * 2、实现Cloneable接口，clone方法，内部只有基本数据类型
 */

public class BeanUtil {

    /**
     * 对象深度复制
     *
     * @param src
     * @return
     */
    public static Serializable deepClone(Serializable src) {
        Serializable dest = null;
        try {
            if (src != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(src);
                oos.close();
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais);
                dest = (Serializable) ois.readObject();
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }
}
