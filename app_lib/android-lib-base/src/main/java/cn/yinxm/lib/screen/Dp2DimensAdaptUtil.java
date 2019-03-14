package cn.yinxm.lib.screen;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dp值 转 dimens适配
 * <p>
 *
 * @author yinxuming
 * @date 2019/3/13
 */
public class Dp2DimensAdaptUtil {
    public static final String regex = "\"(\\d*(\\.\\d*)?)(dp|dip|sp)\"";

    public static void main(String[] args) {
        // 竖屏
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/ui-portrait-media/src/main/res", 0.6667f);
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/bt-music/bt-music-port/src/main/res", 0.6667f);
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/radio/radio-port/src/main/res", 0.6667f);
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/usb/usb-port/src/main/res", 0.6667f);

        // 横屏
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/ui-landscape-media/src/main/res", 0.6667f);
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/bt-music/bt-music-land/src/main/res", 0.6667f);
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/radio/radio-land/src/main/res", 0.6667f);
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/usb/usb-land/src/main/res", 0.6667f);

        // 公共ui
//        findFile2Relace("/Users/yinxuming/work/code/media/carradio/ui-base/src/main/res", 0.6667f);

        mdpi2AnyDpiMapping(1.0f / 1.5f);

    }

    public static void mdpi2AnyDpiMapping(float scale) {
        String numStr = null;
        for (float numF = 0; numF <= 1920; ) {
            if (numF < 5) {
                // 比较小的数，使用float，保留一位小数
                float num = (numF * scale);
                numStr = String.format("%.1f", num);
                if (num == 0) {
                    numStr = "0";
                }
                System.out.println(String.format("%.1f", numF) + ", " + numStr);
                numF += 0.1;
            } else {

                numF = (int) (numF + 0.5);
                if (numF < 6) {
                    numF += 1.0f;
                    continue;
                }
                int num = (int) (numF * scale);
                numStr = "" + num;
                System.out.println(((int) numF) + ", " + numStr);
                numF += 1.0f;
            }
        }

    }

    public static void findFile2Relace(String sourcePath, float scale) {
        File file = new File((sourcePath));

        List<File> fileList = new ArrayList<>();
        getFileList(file, fileList);

        System.out.println("fileList=" + fileList.size());

        for (File tempFile : fileList) {
            String content = modifyFileDp(tempFile, scale);
            if (content == null || content.length() == 0) {
                System.out.println("tempFile=" + tempFile);
                continue;
            } else {
                System.err.println("do write tempFile=" + tempFile);
                writeFile(tempFile, content);
            }
        }
    }

    public static List<File> getFileList(File file, List<File> fileList) {
        if (file.exists()) {
            if (file.isFile()) {
                fileList.add(file);
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File temp : files) {
                    getFileList(temp, fileList);
                }
            }
        }
        return fileList;
    }


    /**
     * @param file
     * @param scale
     * @return 需要更新返回文件内容，不需要更新返回空
     */
    public static String modifyFileDp(File file, float scale) {
        boolean isModify = false;
        StringBuilder sbr = new StringBuilder();

        BufferedReader bufferedReader = null;
        String line = null;

        Pattern pattern = Pattern.compile(regex);
        String str = null;
        try {
            str = readFile(file);
            if (str != null && str.length() > 0) {
                Matcher matcher = pattern.matcher(str);
                Map<String, String> replaceMap = new HashMap<>();
                while (matcher.find()) {
                    isModify = true;
                    System.out.println("groupCount=" + matcher.groupCount() + ", " + matcher.group() + ", " + matcher.group(0) + ", " + matcher.group(1) + ", " + matcher.group(2) + ", " + matcher.group(3));
                    float numF = Float.parseFloat(matcher.group(1));
                    String numStr = null;
                    if (numF < 5) {
                        // 比较小的数，使用float，保留一位小数
                        float num = (numF * scale);
                        numStr = String.format("%.1f", num);
                    } else {
                        int num = (int) (numF * scale);
                        numStr = "" + num;
                    }

                    System.err.println(numF + "——》" + numStr);
                    String key = matcher.group();
                    String value = "\"" + numStr + matcher.group(3) + "\"";
                    replaceMap.put(key, value);
                }

                if (isModify) {
                    Set<Map.Entry<String, String>> set = replaceMap.entrySet();
                    for (Map.Entry<String, String> entry : set) {
                        str = str.replaceAll(entry.getKey(), entry.getValue());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isModify) {
            return str;
        } else {
            return null;
        }
    }


    public static String readFile(File file) {
        StringBuilder sbr = new StringBuilder();

        BufferedReader bufferedReader = null;
        String line = null;

        try {
            String br = System.getProperty("line.separator");
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                if (sbr.length() != 0) {
                    sbr.append(br);
                }
                sbr.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbr.toString();
    }

    public static void writeFile(File file, String content) {
        BufferedWriter bw = null;
        try {
            // 根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new FileWriter(file));
            // 将内容写入文件中
            bw.write(content);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }

}
