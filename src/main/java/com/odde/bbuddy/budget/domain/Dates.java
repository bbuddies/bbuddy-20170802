package com.odde.bbuddy.budget.domain;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Dates {

    final DateTimeFormatter TO_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean isTheSameMonth;

    Dates(String start, String end) {
        startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
        endDate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);
        isTheSameMonth = YearMonth.from(startDate).equals(YearMonth.from(endDate));
    }

    List<YearMonth> getYearMonthListBetween() {
        if (isTheSameMonth) {
            return Collections.singletonList(YearMonth.from(startDate));
        } else {
            List<YearMonth> rtn = new ArrayList<>();
            long monthRange = ChronoUnit.MONTHS.between(startDate, endDate);
            for (int i = 0; i <= monthRange; i++) {
                rtn.add(YearMonth.from(startDate).plusMonths(i));
            }
            return rtn;
        }
    }

    Map<String, BudgetDetail> getDetailsOfEachMonth() {
        Map<String, BudgetDetail> rtn = new HashMap<>();
        long monthRange = ChronoUnit.MONTHS.between(startDate, endDate);

        if (isTheSameMonth) {
            rtn.put(TO_MONTH_FORMATTER.format(startDate),
                    new BudgetDetail(getDaysBetween(startDate, endDate) + 1,
                            startDate.lengthOfMonth()));
        } else {
            for (int i = 0; i <= monthRange; i++) {
                YearMonth inYearMonth = YearMonth.from(startDate.plusMonths(i));
                rtn.put(TO_MONTH_FORMATTER.format(inYearMonth),
                        new BudgetDetail(getActualDays(inYearMonth), inYearMonth.lengthOfMonth()));
            }
        }

        return rtn;
    }

    private int getActualDays(YearMonth compareMonth) {
        if (compareMonth.equals(YearMonth.from(startDate))) {
            return getDaysBetween(startDate, compareMonth.atEndOfMonth()) + 1;
        }
        if (compareMonth.equals(YearMonth.from(endDate))) {
            return getDaysBetween(compareMonth.atDay(1), endDate) + 1;
        }
        return compareMonth.lengthOfMonth();
    }

    private int getDaysBetween(LocalDate start, LocalDate end) {
        return Math.toIntExact(ChronoUnit.DAYS.between(start, end));
    }

}
