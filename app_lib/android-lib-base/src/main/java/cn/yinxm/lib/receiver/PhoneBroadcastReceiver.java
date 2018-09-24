package cn.yinxm.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import cn.yinxm.lib.utils.log.LogUtil;


/**
 * 监听拨打电话状态
 * 要在AndroidManifest.xml注册广播接收器:
 * <receiver android:name=".PhoneReceiver">
 * <intent-filter>
 * <action android:name="android.intent.action.PHONE_STATE"/>
 * <action android:name="android.intent.action.NEW_OUTGOING_CALL" />
 * </intent-filter>
 * </receiver>
 * <p>
 * 还要添加权限:
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
 * <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"></uses-permission>
 */
public class PhoneBroadcastReceiver extends BroadcastReceiver {

    public Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        try {
            String action = intent.getAction();
            LogUtil.d("action=" + action);
            //如果是去电
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
                final String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                LogUtil.d("phoneNum: " + phoneNum);
                LogUtil.i("[通信]去电");
            } else {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            LogUtil.e("[通信]广播异常:", e);
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            LogUtil.d("PhoneBroadcastReceiver.listener state=" + state + ", incomingNumber=" + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    LogUtil.i("[通信]挂断");

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    LogUtil.i("[通信]接听");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    LogUtil.d("[通信]响铃:来电号码" + incomingNumber);

                    break;
            }
        }
    };
}
