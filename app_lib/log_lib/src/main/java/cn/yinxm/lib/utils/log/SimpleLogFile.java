package cn.yinxm.lib.utils.log;


import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ThreadPoolExecutor.*;

/**
 * 将日志记录到文件，不依赖任何第三方库
 * <p>
 *
 * @author yinxuming
 * @date 2019-12-09
 */
public class SimpleLogFile {
    FileConfigure mFileConfigure;
    private Executor mExecutor;

    public static void main(String[] args) {
        SimpleLogFile simpleLogFile = new SimpleLogFile(new FileConfigure() {
            @Override
            public String getFilePath() {
                return "LogFile.log";
            }   // 当前project 根目录

            @Override
            public int getFileMaxSize() {
                return 1024;
            }
        });
        for (int i = 0; i < 100; i++) {
            simpleLogFile.log2File("test=" + i);
        }
        simpleLogFile.log2File("test");
    }

    public SimpleLogFile(FileConfigure fileConfigure) {
        if (fileConfigure == null) {
            throw new RuntimeException("new SimpleLogFile, FileConfigure can not be null...");
        }
        mFileConfigure = fileConfigure;

        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "SimpleLogFile-Thread");
            }
        };

        mExecutor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory,
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void log2File(final String content) {

        if (content == null || content.isEmpty()) {
            return;
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                appendLog(content);
                checkMaxFile();
            }
        });
    }


    private File getLogFile() {
        File logFile = null;
        try {
            logFile = new File(mFileConfigure.getFilePath());

//            if (!logFile.getParentFile().exists()) {
//                logFile.mkdirs();
//            }

            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logFile;
    }

    private void appendLog(String text) {
        try {
            File logFile = getLogFile();
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkMaxFile() {
        File sourceFile = getLogFile();
        if (sourceFile != null && sourceFile.length() > mFileConfigure.getFileMaxSize()) {
            File tempFile = new File("temp.log");
            copyFileByChannel(sourceFile, tempFile, mFileConfigure.delOldFileRatio());
            boolean flag = tempFile.renameTo(sourceFile);
            System.out.println("renameTo=" + flag);
        }
    }

    private void copyFileByChannel(File sourceFile, File destFile, float startRatio) {
        FileInputStream fi = null;
        FileOutputStream fo = null;

        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(sourceFile);
            fo = new FileOutputStream(destFile);
            in = fi.getChannel();
            out = fo.getChannel();

            in.transferTo((long) (in.size() * startRatio), in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fi);
            closeStream(in);
            closeStream(fo);
            closeStream(out);
        }
    }

    private void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static abstract class FileConfigure {
        public abstract String getFilePath();

        /**
         * 文件大小，默认10M
         *
         * @return
         */
        public int getFileMaxSize() {
            return 10 * 1024 * 1024;
        }

        /**
         * 文件满了后，删除老文件的比例
         *
         * @return
         */
        public float delOldFileRatio() {
            return 0.5f;
        }
    }
}
