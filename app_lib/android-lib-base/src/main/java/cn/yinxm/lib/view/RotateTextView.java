package cn.yinxm.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 功能：可以旋转的TextView
 * Created by yinxm on 2017/12/12.
 */

public class RotateTextView extends TextView {
    public RotateTextView(Context context) {
        super(context);
    }

    public RotateTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.save();
//        canvas.rotate(90);
//        super.onDraw(canvas);
//        canvas.restore();

        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());//
        canvas.rotate(90, this.getWidth() / 2f, this.getHeight() / 2f);//根据控件中心旋转
        super.onDraw(canvas);
        canvas.restore();
    }
}
