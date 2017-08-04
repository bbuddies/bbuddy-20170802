package com.odde.bbuddy.budget.domain;

import org.junit.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DatesTest {

    @Test
    public void getMonthYearListInTheSameMonthRange() {
        Dates dates = new Dates("2017-04-01", "2017-04-30");

        List<YearMonth> ymList = dates.getYearMonthListBetween();

        assertThat(ymList.size()).isEqualTo(1);
        assertThat(ymList.get(0).getYear()).isEqualTo(2017);
        assertThat(ymList.get(0).getMonthValue()).isEqualTo(4);
    }

    @Test
    public void getMonthYearListCrossMonths() {
        Dates dates = new Dates("2017-04-01", "2017-05-30");

        List<YearMonth> ymList = dates.getYearMonthListBetween();

        assertThat(ymList.size()).isEqualTo(2);
        assertThat(ymList).contains(
                YearMonth.of(2017, 4), YearMonth.of(2017, 5));
    }
}
