package com.msj.order.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 处理此类，出现无法转换的问题
 *
 * @author gsma
 * @date 2015-6-29
 */
public class NumberUtil {

    /**
     * 判定，如果转换成功，返回成功值，失败返回默认值
     *
     * @param defaultValue 默认值
     * @param value 值
     * @return 转换后的值
     */
    public static int parseInt(String value, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double parseDouble(String value, double defaultValue) {
        double result = defaultValue;
        try {
            result = new DecimalFormat().parse(value).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static long parseLong(String value, long defaultValue) {
        long result = defaultValue;
        try {
            result = Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double sub(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double multiply(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double multiply(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    public static double divide(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        //进行除法时当不整除，出现无限循环小数时，就会抛异常的
        //设置小数点和指定标度
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
}
