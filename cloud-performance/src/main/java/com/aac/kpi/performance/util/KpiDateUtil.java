package com.aac.kpi.performance.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Date;

public class KpiDateUtil {

    public static String formatToTheMonth(Date date){
        //获得年的部分
        int year = DateUtil.year(date);
        //获得月份，从0开始计数
        int month = DateUtil.month(date) + 1;

        String yearStr = String.valueOf(year);
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);

        return yearStr + "-" + monthStr;
    }

    public static boolean isBetweenMonth(String month, Date begin, Date end){
        String beginStr = KpiDateUtil.formatToTheMonth(begin);
        String endStr = KpiDateUtil.formatToTheMonth(end);
        return StrUtil.compare(month, beginStr, true) >= 0 && StrUtil.compare(endStr, month, true) >= 0;
    }
}
