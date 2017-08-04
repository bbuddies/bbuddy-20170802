package com.odde.bbuddy.budget.domain;

import org.assertj.core.api.ComparatorFactory;
import org.junit.Test;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class DatesTest {

    @Test
    public void getMonthYearListInTheSameMonthRange() {
        Dates dates = dates("2017-04-01", "2017-04-30");

        List<YearMonth> ymList = dates.getYearMonthListBetween();

        assertThat(ymList.size()).isEqualTo(1);
        assertThat(ymList.get(0).getYear()).isEqualTo(2017);
        assertThat(ymList.get(0).getMonthValue()).isEqualTo(4);
    }

    @Test
    public void getMonthYearListCrossMonths() {
        Dates dates = dates("2017-04-01", "2017-05-30");

        List<YearMonth> ymList = dates.getYearMonthListBetween();

        assertThat(ymList.size()).isEqualTo(2);
        assertThat(ymList).contains(
                YearMonth.of(2017, 4), YearMonth.of(2017, 5));
    }

    private Dates dates(String start, String end) {
        return new Dates(start, end);
    }

    @Test
    public void getDetails() {
        Dates dates = dates("2017-04-01", "2017-07-10");

        Map<String, BudgetDetail> allDetails = new HashMap<>();
        allDetails.put("2017-04", aDetail(30, 30));
        allDetails.put("2017-05", aDetail(31, 31));
        allDetails.put("2017-06", aDetail(30, 30));
        allDetails.put("2017-07", aDetail(10, 31));

        Map<String, BudgetDetail> details = dates.getDetailsOfEachMonth();

        assertThat(details.values()).usingFieldByFieldElementComparator().isEqualTo(allDetails.values());
    }

    private BudgetDetail aDetail(int actual, int length) {
        return new BudgetDetail(actual, length);
    }
}
