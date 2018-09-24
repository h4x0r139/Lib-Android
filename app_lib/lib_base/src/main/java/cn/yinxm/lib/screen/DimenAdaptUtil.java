package cn.yinxm.lib.screen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by yinxm on 2017/3/17.
 * 功能: 自动计算dimens中的值大小
 */

public class DimenAdaptUtil {

    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.close();
    }

    /**
     * 计算缩放倍数
     *
     * @param srcSwdp
     * @param destSwdp
     * @return 目的swdp/源swdp
     */
    public static BigDecimal getScale(int srcSwdp, int destSwdp) {
        System.out.print("srcSwdp=" + srcSwdp + ", destSwdp=" + destSwdp);
        BigDecimal scale = new BigDecimal(destSwdp).divide(new BigDecimal(srcSwdp), 5, BigDecimal.ROUND_HALF_DOWN);
        System.out.print("scale=" + scale);
        return scale;
    }

    /**
     * @param srcDimensFile  dimens 模板文件路径
     * @param srcSwdp        dimens 模板的最小宽度限定符
     * @param destDimensFile 需要生成的dimens文件路径
     * @param destSwdp       需要生成的dimens 最小宽度限定符
     */
    public static void genDimens(String srcDimensFile, int srcSwdp, String destDimensFile, int destSwdp) {

        File srcFile = new File(srcDimensFile);
//       File destFile = new File(destDimensFile);
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();

        DecimalFormat decimalFormat = new DecimalFormat("#.#####");

        BigDecimal scale = getScale(srcSwdp, destSwdp);

        try {
            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(srcFile));
            String tempString;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束

            while ((tempString = reader.readLine()) != null) {

                if (tempString.contains("</dimen>")) {
                    //tempString = tempString.replaceAll(" ", "");
                    String start = tempString.substring(0, tempString.indexOf(">") + 1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);
                    double num = Double.valueOf(tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</dimen>") - 2));
                    String count = decimalFormat.format(num * scale.doubleValue());
                    System.err.println("num=" + num + ", count=" + count);
                    stringBuilder.append(start).append(count).append(end).append("\n");

                } else {
                    stringBuilder.append(tempString).append("\n");
                }
                line++;
            }
            reader.close();
            System.out.println("<!--  sw" + destSwdp + "dp -->");
            System.out.println(stringBuilder);

            writeFile(destDimensFile, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 改变原有dimens的值, value = (value+plusValue)*scaleValue
     *
     * @param srcDimensFilePath dimens 源文件路径
     * @param destDimensFile    dimens 新文件路径
     * @param changeType        0:dp&&sp, 1:dp, 2:sp
     * @param plusValue         原有值增加量
     * @param scaleValue        原有值放大量
     */
    public static void changeDimens(String srcDimensFilePath, String destDimensFile, int changeType, double plusValue, double scaleValue) throws Exception {
        File srcFile = new File(srcDimensFilePath);
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");

        reader = new BufferedReader(new FileReader(srcFile));
        String tempStr = null;
        while ((tempStr = reader.readLine()) != null) {
            if (tempStr.contains("</dimen>")) {
//                 <dimen name="dip_1257">670.3581dp</dimen>
//                <dimen name="sp_197">105.0601sp</dimen>
                String startStr = tempStr.substring(0, tempStr.indexOf(">") + 1);
                String endStr = tempStr.substring(tempStr.lastIndexOf("<") - 2);//   dp</dimen>
                int thisType = 0;
                //处理dp、sp
                if (endStr.toLowerCase().startsWith("sp")) {
                    thisType = 2;
                } else {
                    thisType = 1;
                }


                String valueStr = tempStr.substring(tempStr.indexOf(">") + 1, tempStr.lastIndexOf("<") - 2);//670.3581
                String newValue = valueStr;
                if (changeType == 0 || changeType == thisType) {//需要改变的范围一致
                    double num = Double.valueOf(valueStr) + plusValue;
                    if (num < 0) {
                        num = 0;
                    }
                    newValue = decimalFormat.format(num * scaleValue);
                    System.err.println("oldValue=" + valueStr + ", newValue=" + newValue);
                }
                stringBuilder.append(startStr).append(newValue).append(endStr).append("\n");

            } else {
                stringBuilder.append(tempStr).append("\n");
            }
        }

        reader.close();
        System.out.println(stringBuilder);

        writeFile(destDimensFile, stringBuilder.toString());
    }


    public static void main(String[] args) throws Exception {
//        genDimens(
//                "D:\\yinxm\\Android_Workspace\\MyAndroid\\my_senior_app\\myapp\\myapp_01\\my_demo\\src\\main\\res\\values-sw720dp-land\\dimens.xml", 720,
//                "D:\\dimens.xml", 811
//        );

        //所有字体值-2
        changeDimens("D:\\androidCode\\BanTing2.0_Git\\BanTing\\huiting\\src\\main\\res\\values-sw720dp-land\\dimens.xml"
                , "D:\\dimens.xml"
                , 2, -2.0, 1);
    }
}
