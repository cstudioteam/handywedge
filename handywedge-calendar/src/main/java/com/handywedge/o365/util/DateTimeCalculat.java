package com.handywedge.o365.util;
import java.util.Date;

import org.joda.time.DateTime;

public class DateTimeCalculat {
    private static int TS_15MIN = 15;	// 15分
    /// <summary>
    /// カレント日時を取得（分で切り上げ）
    /// </summary>
    /// <returns>切り上げ後日時</returns>
    public static DateTime CurrentTimeWithRest(){
        return roundDown(DateTime.now(), 1);
    }

    /// <summary>
    /// 日時（０時）を取得
    /// </summary>
    /// <returns>日時（０時）</returns>
    public static DateTime getDayTime(DateTime input){
        DateTime generalTime = new DateTime(input.getYear(), input.getMonthOfYear(), input.getDayOfMonth(), 0, 0, 0, input.getZone());
        return generalTime;
    }

    public static Date plusMinutes(Date input, int interval){
    		DateTime dt = new DateTime(input);
    		return dt.plusMinutes(interval).toDate();
    }

    public static Date minusMinutes(Date input, int interval){
		DateTime dt = new DateTime(input);
		return dt.minusMinutes(interval).toDate();
}
    /// <summary>
    /// 日時を切り上げ
    /// </summary>
    /// <param name="input">日時</param>
    /// <param name="interval">時間間隔</param>
    /// <returns>切り上げ後日時</returns>
    public static DateTime roundUp(DateTime input, int interval){
        if (interval == 0)
        {
            interval = TS_15MIN;
        }

        int modeInterval = input.getMinuteOfHour() % interval;
        int upMinute = input.getMinuteOfHour();
        if((input.getMinuteOfHour() % interval) > 0) {
            upMinute = (input.getMinuteOfHour() / interval + 1) * interval;
        }
        return new DateTime(input.getYear(), input.getMonthOfYear(), input.getDayOfMonth(), input.getHourOfDay(), upMinute, 0, input.getZone());
    }

    /// <summary>
    /// 日時を切り下げ
    /// </summary>
    /// <param name="input">日時</param>
    /// <param name="interval">時間間隔</param>
    /// <returns>切り下げ後日時</returns>
    public static DateTime roundDown(DateTime input, int interval){
        if (interval == 0)
        {
            interval = TS_15MIN;
        }
        int modeInterval = input.getMinuteOfHour() % interval;
        int upMinute = input.getMinuteOfHour();
        if((input.getMinuteOfHour() % interval) > 0) {
            upMinute = (input.getMinuteOfHour() / interval ) * interval;
        }
        return new DateTime(input.getYear(), input.getMonthOfYear(), input.getDayOfMonth(), input.getHourOfDay(), upMinute, 0, input.getZone());
    }
}
