package com.purduecs.kiwi.oneup.helpers;

import java.util.Date;

/**
 * Created by Adam on 4/25/16.
 */
public class TimeFormatHelper {

    public static String timeSinceShort(Date d) {
        long time = (new Date()).getTime() - d.getTime();
        String tim = "s";
        time /= 1000; // At seconds
        if (time >= 60) {
            time /= 60;
            tim = "m";
            if (time >= 60) {
                time /= 60;
                tim = "h";
                if (time >= 24) {
                    time /= 24;
                    tim = "d";
                    if (time >= 365) {
                        time /= 365;
                        tim = "y";
                    }// At years
                    else if (time >= 12) {
                        time /= 12;
                        tim = "mo";
                    }// Else do months
                } // At days
            }// At hours
        }// At minutes

       return time + " " + tim;
    }

    public static String timeSince(Date d) {
        long time = (new Date()).getTime() - d.getTime();
        String tim = "seconds";
        time /= 1000; // At seconds
        if (time >= 60) {
            time /= 60;
            tim = "minutes";
            if (time >= 60) {
                time /= 60;
                tim = "hours";
                if (time >= 24) {
                    time /= 24;
                    tim = "days";
                    if (time >= 365) {
                        time /= 365;
                        tim = "years";
                    }// At years
                    else if (time >= 12) {
                        time /= 12;
                        tim = "months";
                    }// Else do months
                } // At days
            }// At hours
        }// At minutes

        return time + " " + tim;
    }

}
