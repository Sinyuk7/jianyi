package com.sinyuk.jianyimaterial.utils;

import java.text.DecimalFormat;

/**
 * Created by Sinyuk on 16.1.22.
 */
public class FormatUtils {
    public static String formatPrice(String unFormatted) {
        final String prefix = "";

        String formatted = null;
        try {

            int price;
            DecimalFormat formatter = new DecimalFormat("#,###");
            if (unFormatted.contains(".")) {
                String[] split = unFormatted.split("\\.", 2);
                try {
                    price = Integer.parseInt(split[0]);
                    formatted = prefix + formatter.format(price);

                } catch (ArrayIndexOutOfBoundsException exception) {
                    exception.printStackTrace();
                }

            } else {

                price = Integer.parseInt(unFormatted);
                formatted = prefix + formatter.format(price);

            }
            if (formatted == null)
                formatted = prefix + unFormatted;


        } catch (NumberFormatException exception) {
            exception.printStackTrace();

        }
        return "¥" + formatted;
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        LogUtils.simpleLog(FormatUtils.class, "length: " + phoneNumber.length());
        if (phoneNumber.length() < 11) {
            return false;
        } else if (phoneNumber.length() == 11) {
            for (int i = 0; i < phoneNumber.length(); i++) {
                char c = phoneNumber.charAt(i);
                if (c > '9' || c < '0') {
                    return false;
                }
            }
        } else return false;

        return true;
    }

    public static String formatPhoneNum(String num) {
        if (!isPhoneNumberValid(num))
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num.length(); i++) {
            if (i == 3 || i == 7)
                sb.append(" ");
            sb.append(num.charAt(i));
        }
        return sb.toString();
    }
}
