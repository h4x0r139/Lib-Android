package cn.yinxm.lib.utils.log;

import android.content.ContentValues;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import cn.yinxm.util.log.BuildConfig;

public class HkLogUtils {
    public static final String BASE_LOG_PATH = File.separator + "Log" + File.separator;
    private static final String COMMON_LOG_PATH = BASE_LOG_PATH + "CommonLog" + File.separator;
    private static final String CRASH_LOG_PATH = BASE_LOG_PATH + "CrashLog" + File.separator;
    private static final String DATABASE_LOG_PATH = BASE_LOG_PATH + "DatabaseLog" + File.separator;

    private static File file = null;
    private static String createLogFileTime = null;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF2 = new SimpleDateFormat("MM-dd HH:mm:ss");
    private static BaseConfigure mBaseConfigure;
    private static final String TAG = "baiduhaokan";
    private static final String FILE_TYPE = ".java";
    public static boolean sDebug;

    /**
     * 日志持久化线程池
     */
    private static Executor mLogExecutor;

    /**
     * 初始化配置
     *
     * @param configure
     */
    public static void init(BaseConfigure configure) {
        mBaseConfigure = configure;
        sDebug =
                BuildConfig.DEBUG || sDebug || (mBaseConfigure != null && mBaseConfigure.getIsDebug());
    }

    private static void init() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                createLogFileTime = SDF.format(new Date());
//                file = new File(FileUtils.getCachePath() + COMMON_LOG_PATH + createLogFileTime +
//                        ".log");
                file = new File(mBaseConfigure.getFilePath());
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

                if (mLogExecutor == null) {
                    mLogExecutor = Executors.newSingleThreadExecutor();
                }
            } catch (Exception e) {
                Log.e(TAG, "LogUtils " + e.getMessage() + " don't have SDCard Permission");
                e.printStackTrace();
            }
        }
    }

    private static boolean checkStoreLog() {
        return file != null && file.exists() && !TextUtils.isEmpty(createLogFileTime)
                && createLogFileTime.equals(SDF.format(new Date()));
    }

    private static void appendLog(final File file, final String content, final int level) {
        if (file == null || !file.exists() || mLogExecutor == null) {
            return;
        }
        mLogExecutor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedWriter out = null;
                try {
                    out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(file, true), "UTF-8"), 8192);
                    StringBuffer sb = new StringBuffer();
                    sb.append(SDF2.format(new Date()));
                    sb.append("\t ");
                    sb.append(level == 1 ? "info" : level == 2 ? "warn" : level == 100 ? "crash"
                            : "error");
                    sb.append("\t");
                    sb.append(content);
                    sb.append("\r\n");
                    out.write(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void d(String msg) {
        if (sDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (sDebug) {
            Log.d(tag, msg);
        }
    }

    public static void v(String msg) {
        if (mBaseConfigure == null) {
            return;
        }

        if (mBaseConfigure.getIsDebug()) {
            StackTraceElement stackTrace = (new Throwable()).getStackTrace()[2];
            String filename = stackTrace.getFileName();
            String methodname = stackTrace.getMethodName();
            int linenumber = stackTrace.getLineNumber();
            // 当心！proguard混淆以后getFileName会是一个null值！
            if (filename != null && filename.contains(FILE_TYPE)) {
                filename = filename.replace(FILE_TYPE, "");
            }
            msg = String.format("[%s: %s: %d]%s", filename, methodname, linenumber, msg);
            Log.v(TAG, "Thread Id: " + Thread.currentThread().getId() + "  " + msg);
        }
        if (mBaseConfigure.getIsStorageLog()) {
            if (!checkStoreLog()) {
                init();
            }
            appendLog(file,
                    TAG + "\t" + "thread Id: " + Thread.currentThread().getId() + "  " + msg, 1);
        }
    }

    public static void info(String tag, String msg) {
        if (mBaseConfigure == null) {
            return;
        }

        if (mBaseConfigure.getIsDebug()) {
            Log.i(tag, "thread Id: " + Thread.currentThread().getId() + "  " + msg);
        }
        if (mBaseConfigure.getIsStorageLog()) {
            if (!checkStoreLog()) {
                init();
            }
            appendLog(file,
                    tag + "\t" + "thread Id: " + Thread.currentThread().getId() + "  " + msg, 1);
        }
    }

    public static void warn(String tag, String msg) {
        if (mBaseConfigure == null) {
            return;
        }

        if (mBaseConfigure.getIsDebug()) {
            Log.w(tag, "thread Id: " + Thread.currentThread().getId() + "  " + msg);
        }
        if (mBaseConfigure.getIsStorageLog()) {
            if (!checkStoreLog()) {
                init();
            }
            appendLog(file,
                    tag + "\t" + "thread Id: " + Thread.currentThread().getId() + "  " + msg, 2);
        }
    }

    public static void error(String tag, String msg) {

        if (mBaseConfigure == null) {
            return;
        }

        if (mBaseConfigure.getIsDebug()) {
            Log.e(tag, "thread Id: " + Thread.currentThread().getId() + "  " + msg);
        }
        if (mBaseConfigure.getIsStorageLog()) {
            if (!checkStoreLog()) {
                init();
            }
            appendLog(file,
                    tag + "\t" + "thread Id: " + Thread.currentThread().getId() + "  " + msg, 0);
        }
    }

    public static void error(String tag, Throwable e) {
        if (mBaseConfigure == null) {
            return;
        }

        String msg = " exception:" + getErrorInfo(e);
        if (mBaseConfigure.getIsDebug()) {
            Log.e(tag, "thread Id: " + Thread.currentThread().getId() + "  " + msg);
        }
        if (mBaseConfigure.getIsStorageLog()) {
            if (!checkStoreLog()) {
                init();
            }
            appendLog(file,
                    tag + "\t" + "thread Id: " + Thread.currentThread().getId() + "  " + msg, 0);
        }
    }

    public static String getErrorInfo(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        throwable.printStackTrace(pw);
        pw.close();
        return writer.toString();
    }

    public static String crash(String msg) {
        if (mBaseConfigure == null) {
            return "";
        }

        if (mBaseConfigure.getIsDebug()) {
            Log.e("baidu.haokan.crash", "crash occured : " + Thread.currentThread().getId() + "  "
                    + msg);
        }
//        String path = FileUtils.getCachePath() + CRASH_LOG_PATH + SDF.format(new Date()) + ".log";
        String path = mBaseConfigure.getFilePath();
        try {
            File desFile = new File(path);
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            if (!desFile.exists()) {
                desFile.createNewFile();
            }
            RandomAccessFile newFile = new RandomAccessFile(desFile, "rw");
            // If file size lager than 100K,do not write again
            if (desFile.length() > 1024 * 1024) {
                newFile.seek(0);
            } else {
                newFile.seek(desFile.length());
            }
            newFile.write((msg + "\n").getBytes());
            newFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void sqllog(String tablename, String action, String exec,
                              ContentValues initialValues,
                              String whereClause, String[] whereArgs) {
        if (mBaseConfigure == null) {
            return;
        }

        StringBuffer sbmsg = new StringBuffer();
        boolean isdebug = mBaseConfigure.getIsDebug();
        boolean isstorelog = mBaseConfigure.getIsStorageDBLog();
        if (isdebug || isstorelog) {
            sbmsg.append("table:").append(tablename).append("; action:").append(action)
                    .append("; exec:").append(exec).append("; values:");
            if (initialValues != null && initialValues.size() > 0) {
                Set<Map.Entry<String, Object>> entrySet = initialValues.valueSet();
                Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();
                boolean needSeparator = false;
                while (entriesIter.hasNext()) {
                    if (needSeparator) {
                        sbmsg.append(",");
                    }
                    needSeparator = true;
                    Map.Entry<String, Object> entry = entriesIter.next();
                    sbmsg.append("[").append(entry.getKey()).append("] ");
                    sbmsg.append(entry.getValue());
                }
            }
            sbmsg.append("; where:[").append(whereClause).append("] ");
            if (whereArgs != null && whereArgs.length > 0) {
                boolean needSeparator = false;
                for (int i = 0; i < whereArgs.length; i++) {
                    if (needSeparator) {
                        sbmsg.append(",");
                    }
                    needSeparator = true;
                    sbmsg.append(whereArgs[i]);
                }
            }
        }
        if (isdebug) {
            Log.i("baidu.haokan.database",
                    Thread.currentThread().getId() + "  " + sbmsg.toString());
        }
        if (isstorelog) {
//            String path =
//                    FileUtils.getCachePath() + DATABASE_LOG_PATH + SDF.format(new Date()) + "
//                    .log";
            String path = mBaseConfigure.getFilePath();
            try {
                File desFile = new File(path);
                if (!desFile.getParentFile().exists()) {
                    desFile.getParentFile().mkdirs();
                }
                if (!desFile.exists()) {
                    desFile.createNewFile();
                }
                RandomAccessFile newFile = new RandomAccessFile(desFile, "rw");
                // If file size lager than 100K,do not write again
                if (desFile.length() > 1024 * 1024) {
                    newFile.seek(0);
                } else {
                    newFile.seek(desFile.length());
                }
                newFile.write((sbmsg.toString() + "\n").getBytes());
                newFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class DefaultLogConfigure implements BaseConfigure {

        @Override
        public boolean getIsDebug() {
            return true;
        }

        @Override
        public boolean getIsStorageLog() {
            return true;
        }

        @Override
        public boolean getIsStorageDBLog() {
            return true;
        }

        @Override
        public String getFilePath() {
            return "/sdcard/myLog.log";
        }
    }


    public interface BaseConfigure {

        /**
         * 是否调试模式
         *
         * @return
         */
        boolean getIsDebug();

        /**
         * 是否存储日志
         *
         * @return
         */
        boolean getIsStorageLog();

        /**
         * 是否存储数据库日志
         *
         * @return
         */
        boolean getIsStorageDBLog();

        String getFilePath();
    }
}
