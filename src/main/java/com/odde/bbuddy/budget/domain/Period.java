package com.odde.bbuddy.budget.domain;

import java.time.LocalDate;

import static java.time.Period.between;

public class Period {
    private final LocalDate start;
    private final LocalDate end;

    public Period(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public int overlappingDayCount(Period another) {
        if(start.isAfter(another.end) || end.isBefore(another.start)){
            return 0;
        }
        LocalDate startOfOverlapping = start.isAfter(another.start) ? start : another.start;
        LocalDate endOfOverlapping = end.isBefore(another.end) ? end : another.end;
        return between(startOfOverlapping, endOfOverlapping.plusDays(1)).getDays();
    }
}
