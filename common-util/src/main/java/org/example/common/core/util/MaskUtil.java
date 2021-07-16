package org.example.common.core.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: edward
 * @date: 2020/2/13 19:21
 * @description:
 */
public class MaskUtil {
    public final static String MASK = "*";

    /**
     * 姓名脱敏，保留姓，比如王大大，脱敏后王**
     *
     * @param name 姓名
     * @return
     */
    public static String formatName(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }

        String value = StringUtils.left(name, 1);
        return StringUtils.rightPad(value, StringUtils.length(name), MASK);
    }

    /**
     * 身份证号码脱敏，保留前六后四
     *
     * @param id 身份证号码
     * @return
     */
    public static String formatIdNum(String id) {
        if (StringUtils.isEmpty(id)) {
            return id;
        }

        return replaceBetween(id, 6, id.length() - 4);
    }

    /**
     * 手机号脱敏，保留前三后四
     *
     * @param mobile
     * @return
     */
    public static String formatMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return mobile;
        }
        return replaceBetween(mobile, 3, mobile.length() - 4);
    }

    /**
     * 卡号脱敏，保留前六后四
     *
     * @param card
     * @return
     */
    public static String formatCard(String card) {
        if (StringUtils.isEmpty(card)) {
            return card;
        }
        return replaceBetween(card, 6, card.length() - 4);
    }

    /**
     * 生日脱敏，格式为19*-*-20
     *
     * @param birthday
     * @return
     */
    public static String formatBirthday(String birthday) {
        if (StringUtils.isEmpty(birthday)) {
            return birthday;
        }
        String separator = "";
        for (char var : birthday.toCharArray()) {
            if (var < '0' || var > '9') {
                separator = var + "";
                break;
            }
        }
        return formatDate(separator,birthday);
    }

    public static String formatDate(String separator,String birthday){
        String str = "yyyy" + separator + "MM" + separator + "dd";
        SimpleDateFormat sf = new SimpleDateFormat(str);
        /* 没有分隔符时，脱敏后给它一个默认的分隔符 */
        if (StringUtils.isEmpty(separator)){
            separator = "-";
        }
        try {
            Date date = sf.parse(birthday);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String formatYear = String.valueOf(calendar.get(Calendar.YEAR)).substring(0,2);
            return formatYear + MASK + separator + MASK + separator +calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return birthday;
    }

    /**
     * 格式化邮箱地址
     *
     * @param email
     * @return
     */
    public static String formatEmailAddress(String email) {
        if (StringUtils.isEmpty(email)) {
            return email;
        }
        return replaceBetween(email, 2, email.indexOf("@"));
    }

    public static String replaceBetween(String oriValue, int start, int end) {
        return replaceBetween(oriValue, start, end, MASK);
    }

    public static String replaceBetween(String oriValue, int start, int end, String replacement) {
        if (StringUtils.isEmpty(oriValue)) {
            return oriValue;
        }
        int length = end - start;
        if (length > 0) {
            StringBuilder sb = new StringBuilder(oriValue);
            sb.replace(start, end, StringUtils.repeat(replacement, length));
            return sb.toString();
        } else {
            return oriValue;
        }
    }
}
