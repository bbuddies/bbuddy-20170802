package com.odde.bbuddy.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author neil
 * @since 2017/8/4
 */
public class DateRange {
    private Date start;
    private Date end;

    public DateRange(String startDate,
                     String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        this.start = sdf.parse(startDate);
        this.end = sdf.parse(endDate);
    }

    public boolean isDateInRange(Date date) {
        return start.equals(date) || end.equals(date) || (start.before(date) && date.before(end));
    }
}
