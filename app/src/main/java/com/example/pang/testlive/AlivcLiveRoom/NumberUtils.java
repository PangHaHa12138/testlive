package com.example.pang.testlive.AlivcLiveRoom;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Akira on 2018/6/19.
 */

public class NumberUtils {
    private static final String TAG = "ShowNumberUtils";

    public static int calculateTieFenLeftDay(long time) {
        int day;
        if (time < 86400000L) {
            day = 1;
        } else {
            double days = (double) time / 8.64E7D;
            day = (int) Math.ceil(days);
        }

        return day;
    }

    public static String calculateGuardLeftTimeInUserCard(int ld, int lh) {
        String result;
        if (ld > 0) {
            result = ld + "天";
        } else if (lh > 0) {
            result = lh + "小时";
        } else {
            result = "不足1小时";
        }

        return result;
    }

    public static String calculateShowTimeByMilliseconds(long time) {
        String mShowTimeStr;
        if (time > 3600000L) {
            double hour = (double) time / 3600000.0D;
            hour = Math.ceil(hour * 10.0D) / 10.0D;
            DecimalFormat df = new DecimalFormat("#.0");
            mShowTimeStr = df.format(hour);
            mShowTimeStr = mShowTimeStr + "小时";
        } else {
            int minute = (int) (time / 60000L);
            if (minute == 0) {
                minute = 1;
            }

            mShowTimeStr = minute + "分钟";
        }

        return mShowTimeStr;
    }

    public static String calculateShowTimeBySeconds(long time) {
        String mShowTimeStr;
        if (time > 3600L) {
            double hour = (double) time / 3600.0D;
            hour = Math.ceil(hour * 10.0D) / 10.0D;
            DecimalFormat df = new DecimalFormat("#.0");
            mShowTimeStr = df.format(hour);
            mShowTimeStr = mShowTimeStr + "小时";
        } else {
            int minute = (int) (time / 60L);
            mShowTimeStr = minute + "分钟";
        }

        return mShowTimeStr;
    }

    public static String addComma(String str) {
        String reverseStr = (new StringBuilder(str)).reverse().toString();
        String strTemp = "";

        for (int i = 0; i < reverseStr.length(); ++i) {
            if (i * 3 + 3 > reverseStr.length()) {
                strTemp = strTemp + reverseStr.substring(i * 3, reverseStr.length());
                break;
            }

            strTemp = strTemp + reverseStr.substring(i * 3, i * 3 + 3) + ",";
        }

        if (strTemp.endsWith(",")) {
            strTemp = strTemp.substring(0, strTemp.length() - 1);
        }

        String resultStr = (new StringBuilder(strTemp)).reverse().toString();
        return resultStr;
    }

    public static String fixCoinsShow(String coins) {
        long c = 0L;

        try {
            c = Long.valueOf(coins);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        if (c > 100000000L) {
            return c % 100000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 亿", (double) c / 1.0E8D) : String.format(Locale.ENGLISH, "%d 亿", c / 100000000L);
        } else if (c >= 10000L) {
            if (c < 10000000L) {
                return c % 10000L != 0L ? String.format(Locale.ENGLISH, "%.1f 万", (double) c / 10000.0D) : String.format(Locale.ENGLISH, "%d 万", c / 10000L);
            } else {
                return c % 10000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 千万", (double) c / 1.0E7D) : String.format(Locale.ENGLISH, "%d 千万", c / 10000000L);
            }
        } else {
            return c >= 0L ? coins : "0";
        }
    }

    public static String fixCoinsShow(long coins) {
        if (coins > 100000000L) {
            return coins % 100000000L != 0L ? String.format(Locale.ENGLISH, "%.1f亿", (double) coins / 1.0E8D) : String.format(Locale.ENGLISH, "%d亿", coins / 100000000L);
        } else if (coins >= 10000L) {
            if (coins < 10000000L) {
                return coins % 10000L != 0L ? String.format(Locale.ENGLISH, "%.1f万", (double) coins / 10000.0D) : String.format(Locale.ENGLISH, "%d万", coins / 10000L);
            } else {
                return coins % 10000000L != 0L ? String.format(Locale.ENGLISH, "%.1f千万", (double) coins / 1.0E7D) : String.format(Locale.ENGLISH, "%d千万", coins / 10000000L);
            }
        } else {
            return coins >= 0L ? String.valueOf(coins) : "0";
        }
    }

    public static String fixCoinsShow2(String coins) {
        long c = 0L;

        try {
            c = Long.valueOf(coins);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        if (c > 100000000L) {
            return c % 100000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 亿", (double) c / 1.0E8D) : String.format(Locale.ENGLISH, "%d 亿", c / 100000000L);
        } else if (c >= 10000L) {
            return c % 10000L != 0L ? String.format(Locale.ENGLISH, "%.1f 万", (double) c / 10000.0D) : String.format(Locale.ENGLISH, "%d 万", c / 10000L);
        } else {
            return coins;
        }
    }

    public static String fixCoinsShow2(long coins) {
        if (coins > 100000000L) {
            return coins % 100000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 亿", (double) coins / 1.0E8D) : String.format(Locale.ENGLISH, "%d 亿", coins / 100000000L);
        } else if (coins >= 10000L) {
            return coins % 10000L != 0L ? String.format(Locale.ENGLISH, "%.1f 万", (double) coins / 10000.0D) : String.format(Locale.ENGLISH, "%d 万", coins / 10000L);
        } else {
            return coins >= 0L ? String.valueOf(coins) : "0";
        }
    }

    @SuppressLint({"DefaultLocale"})
    public static String getUsercountString(long nums) {
        String strres = "" + nums;
        if (nums > 10000L) {
            String numsfomat = "%.1f万";
            strres = String.format(numsfomat, (float) nums / 10000.0F);
        }

        return strres;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getUsercountString(String nums) {
        long num = Long.valueOf(nums);
        String strres = "" + num;
        if (num > 10000L) {
            String numsfomat = "%.1f万";
            strres = String.format(numsfomat, (float) num / 10000.0F);
        }

        return strres;
    }

    public static String getFansNumberFromLong(long number) {
        if (number > 100000000L) {
            return number % 100000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 亿人", (double) number / 1.0E8D) : String.format(Locale.ENGLISH, "%d 亿人", number / 100000000L);
        } else if (number >= 10000L) {
            if (number < 10000000L) {
                return number % 10000L != 0L ? String.format(Locale.ENGLISH, "%.1f 万人", (double) number / 10000.0D) : String.format(Locale.ENGLISH, "%d 万人", number / 10000L);
            } else {
                return number % 10000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 千万人", (double) number / 1.0E7D) : String.format(Locale.ENGLISH, "%d 千万人", number / 10000000L);
            }
        } else {
            return number + "人";
        }
    }

    public static String formatPeopleNum(Context context, int number) {
        if (LanguegeUtils.isZh(context)) {
            if (number > 100000000L) {
                return number % 100000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 亿人", (double) number / 1.0E8D) : String.format(Locale.ENGLISH, "%d 亿人", number / 100000000L);
            } else if (number >= 10000L) {
                if (number < 10000000L) {
                    return number % 10000L != 0L ? String.format(Locale.ENGLISH, "%.1f 万人", (double) number / 10000.0D) : String.format(Locale.ENGLISH, "%d 万人", number / 10000L);
                } else {
                    return number % 10000000L != 0L ? String.format(Locale.ENGLISH, "%.1f 千万人", (double) number / 1.0E7D) : String.format(Locale.ENGLISH, "%d 千万人", number / 10000000L);
                }
            } else {
                return number + "";
            }
        } else {
            if (number > 1000000000L) {
                return number % 1000000000L != 0L ? String.format(Locale.ENGLISH, "%.1f B", (double) number / 1.0E9D) : String.format(Locale.ENGLISH, "%d 亿人", number / 1000000000L);
            } else if (number >= 1000L) {
                if (number < 1000000L) {
                    return number % 1000L != 0L ? String.format(Locale.ENGLISH, "%.1f K", (double) number / 1000.0D) : String.format(Locale.ENGLISH, "%d 万人", number / 1000L);
                } else {
                    return number % 1000000L != 0L ? String.format(Locale.ENGLISH, "%.1f M", (double) number / 1.0E6D) : String.format(Locale.ENGLISH, "%d 千万人", number / 1000000L);
                }
            } else {
                return number + "";
            }
        }
    }

    public static long getFansNumberFromString(String countString) {
        long count = 0L;
        int length = countString.length();
        String numberString = countString.substring(0, length - 1);
        String middleString;
        double doubleCount;
        if (numberString.contains("万")) {
            middleString = numberString.substring(0, length - 2);
            if (middleString.contains("千")) {
                String realString = countString.substring(0, length - 3);
                doubleCount = Double.valueOf(realString);
                count = (long) (doubleCount * 1.0E7D);
                return count;
            } else {
                doubleCount = Double.valueOf(middleString);
                count = (long) (doubleCount * 10000.0D);
                return count;
            }
        } else if (numberString.contains("亿")) {
            middleString = numberString.substring(0, length - 2);
            doubleCount = Double.valueOf(middleString);
            count = (long) (doubleCount * 1.0E8D);
            return count;
        } else {
            return Long.valueOf(numberString);
        }
    }

    public static String getShowingTimeInMins(long timeMillis) {
        if (timeMillis < 60000L) {
            return "1分钟前开播";
        } else {
            double mins = (double) timeMillis / 60000.0D;
            int intMins = (int) Math.ceil(mins * 10.0D / 10.0D);
            return "开播" + intMins + "分钟";
        }
    }

    public static String getShowWatcherNumber(long number) {
        double kNumber;
        DecimalFormat df;
        String kNumberString;
        if (number > 99999L) {
            kNumber = (double) number / 10000.0D;
            kNumber = Math.ceil(kNumber * 10.0D) / 10.0D;
            df = new DecimalFormat("#.0");
            kNumberString = df.format(kNumber);
            return kNumberString + "w";
        } else if (number > 9999L) {
            kNumber = (double) number / 10000.0D;
            df = new DecimalFormat("#.00");
            kNumberString = df.format(kNumber);
            return kNumberString + "w";
        } else if (number > 999L) {
            kNumber = (double) number / 1000.0D;
            df = new DecimalFormat("#.00");
            kNumberString = df.format(kNumber);
            return kNumberString + "k";
        } else {
            return String.valueOf(number);
        }
    }

}
