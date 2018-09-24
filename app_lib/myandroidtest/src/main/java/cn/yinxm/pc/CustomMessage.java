package cn.yinxm.pc;

/**
 * Created by yinxuming on 2018/6/11.
 */
public class CustomMessage {
    public int index;

    public CustomMessage(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
//        return "\"CustomMessage\": {"
//                + "\"index\": \"" + index
//                + '}';
        return "in:"+index;
    }
}
