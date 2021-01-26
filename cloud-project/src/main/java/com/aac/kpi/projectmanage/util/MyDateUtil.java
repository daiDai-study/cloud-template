package com.aac.kpi.projectmanage.util;


import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * @author jinke
 * @date 2020/06/11/15:34
 **/


public class MyDateUtil {

    /**
     * 获取当前是今年的第几周
     *
     * @return
     */
    public static int getWeekNumOfYear(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        return date.get(weekFields.weekOfWeekBasedYear());
    }

    /**
     * 返回年周；例如：202039
     *
     * @param date
     * @return
     */
    public static String getYearWeek(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        int week = date.get(weekFields.weekOfWeekBasedYear());
        return "" + date.get(weekFields.weekBasedYear()) + (week < 10 ? ("0" + week) : week);
    }

    /**
     * 获取当前是当周的第几天
     *
     * @return
     */
    public static int getDayNumOfWeek(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        ;
        return date.get(weekFields.dayOfWeek());
    }

    /**
     * 获取前/后几周的周字符串，类型string： ["202001", ""202002]
     *
     * @param num
     * @param type: 1表示往后，-1表示忘前
     * @return
     */
    public static List<String> getLastWeeksString(int num, int type) {
        if (type != -1 && type != 1) {
            return null;
        }
        List<String> weeks = new ArrayList<>();
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        int year = now.getYear();

        /**
         * 当前周的上一周的年次必定是和周次对应的
         * 如果当前周的周次比上一周小，说明跨年了，那么今天的年次应该和周次对应，需要+1
         */
        if (now.get(weekFields.weekOfWeekBasedYear()) < now.minusWeeks(1).get(weekFields.weekOfWeekBasedYear())) {
            year++;
        }

        // 计算前几个星期的字符串
        for (int i = 0; i < num; i++) {
            if (type == 1) {
                String weekNum = String.format("%02d", now.plusWeeks(i).get(weekFields.weekOfWeekBasedYear()));
                // 这里的year必须自己维护
                if (i >= 1 && (year + weekNum).compareTo(weeks.get(i - 1)) < 0) {
                    year++;
                }
                weeks.add(year + weekNum);
            } else {
                String weekNum = String.format("%02d", now.minusWeeks(i).get(weekFields.weekOfWeekBasedYear()));
                // 这里的year必须自己维护
                if (i >= 1 && (year + weekNum).compareTo(weeks.get(i - 1)) > 0) {
                    year--;
                }
                weeks.add(year + weekNum);
            }
        }

        return weeks;
    }

    /**
     * 解析时间，获取当周的第一天
     *
     * @param weekString： 202024
     * @return
     */
    public static LocalDate getWeekFirstDate(String weekString) {
        if (StringUtils.isEmpty(weekString)){
            return null;
        }

        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(weekFields.weekBasedYear(), 4)
                .appendValue(weekFields.weekOfWeekBasedYear(), 2)
                .parseDefaulting(ChronoField.DAY_OF_WEEK, 1)
                .toFormatter();
        LocalDate date = LocalDate.parse(weekString, formatter);
        return date;
    }

    /**
     * 将给定的日期变成当年的哪一周
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @return
     */
    public static String getWeekByGivenDate(int year, int month, int dayOfMonth) {
        LocalDate ld = LocalDate.of(year, month, dayOfMonth);
        int weekNumOfYear = getWeekNumOfYear(ld);
        String retValue = "";
        if (String.valueOf(weekNumOfYear).length() == 1) {
            retValue = year + "0" + weekNumOfYear;
        } else {
            retValue = String.valueOf(year) + weekNumOfYear;
        }
        return retValue;
    }

    /**
     * 根据公历来判断节日
     *
     * @param date
     */
    public static String getHolidayStringByDate(LocalDate date) {
        // 转为农历
        ChineseDate date1 = new ChineseDate(DateUtil.parseDate(date.toString().substring(0, 10)));

        // 清明节公式： (Y*D+C)-L
        // Y=年数后2位，D=0.2422，L=闰年数，21世纪C=4.81，20世纪C=5.59
        // 这里只考虑21世纪！！
        int lastTwo = date.getYear() % 1000;
        int qingMingDay = (int) ((lastTwo * 0.2422f + 4.81f) - (lastTwo / 4));
        String date2 = date.getMonthValue() + "-" + date.getDayOfMonth();
        if (date2.equals("4-" + qingMingDay)){
            return "清明节";
        }

        String des;
        switch (date1.getMonth() + "-" + date1.getDay()) {
            case "12-30":
                des = "除夕";
                break;
            case "1-1":
                des = "春节";
                break;
            case "5-5":
                des = "端午节";
                break;
            case "8-15":
                des = "中秋节";
                break;
            default:
                des = null;
        }
        return des;
    }

    /**
     * 列举指定年月的前 n 个月的所有周次
     * 如：
     * 列举前5个月的所有周次
     * 2020年8月份： 2020年7月、2020年6月、2020年5月、2020年4月、2020年3月的所有周次
     * @param year 年份
     * @param month 月份
     * @param beforeMonth 前 n 个月中的 n
     * @return 周次列表
     */
    public static List<String> listYearWeekOfMonth(int year, int month, int beforeMonth){
        List<String> allWeeks = new ArrayList<>();

        int firstYear = year;
        int secondYear = year;
        int firstMonth = month - beforeMonth;
        int secondMonth = month - 1;
        while(firstMonth < 1){
            firstYear--;
            firstMonth += 12;
        }
        while(secondMonth < 1){
            secondYear--;
            secondMonth += 12;
        }
        LocalDate dayOne = LocalDate.of(firstYear, firstMonth, 1);
        LocalDate dayTwo = LocalDate.of(secondYear, secondMonth, 1);
        dayTwo = LocalDate.of(secondYear, secondMonth, dayTwo.lengthOfMonth());
        while(dayOne.compareTo(dayTwo) < 0){
            String yearWeek = MyDateUtil.getYearWeek(dayOne);
            if (!allWeeks.contains(yearWeek)) {
                allWeeks.add(yearWeek);
            }
            dayOne = dayOne.plusDays(7);
        }
        String yearWeek = MyDateUtil.getYearWeek(dayTwo);
        if (!allWeeks.contains(yearWeek)) {
            allWeeks.add(yearWeek);
        }

        return allWeeks;
    }

    /**
     * 使用 Map按value进行排序
     * @param oriMap LinkedHashMap
     * @return
     */
    public static <T extends Number> Map<String, T> sortMapByValue(Map<String, T> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, T> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<String, T>> entryList = new ArrayList<>(
                oriMap.entrySet());
        entryList.sort(Comparator.comparing(v -> -Math.abs(v.getValue().floatValue())));

        Iterator<Map.Entry<String, T>> iter = entryList.iterator();
        Map.Entry<String, T> tmpEntry;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
}
