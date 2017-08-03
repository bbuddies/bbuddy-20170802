package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Budgets {
    private final BudgetRepo repo;

    @Autowired
    public Budgets(BudgetRepo repo) {
        this.repo = repo;
    }

    public void save(Budget budget) {
        Budget oldBudget = repo.findByMonth(budget.getMonth());
        if (oldBudget != null) {
            oldBudget.setAmount(budget.getAmount());
            repo.save(oldBudget);
        } else {
            repo.save(budget);
        }
    }

    public List<Budget> getAll() {
        return repo.findAll();
    }

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

        Map<String, Map<String, Integer>> getDetailsOfEachMonth() {
            Map<String, Map<String, Integer>> rtn = new HashMap<>();
            long monthRange = ChronoUnit.MONTHS.between(startDate, endDate);

            if (isTheSameMonth) {
                Map<String, Integer> detail = new HashMap<>();
                detail.put("actual", getDaysBetween(startDate, endDate) + 1);
                detail.put("length", startDate.lengthOfMonth());
                rtn.put(TO_MONTH_FORMATTER.format(startDate), detail);
            } else {
                for (int i = 0; i <= monthRange; i++) {
                    YearMonth inYearMonth = YearMonth.from(startDate.plusMonths(i));
                    Map<String, Integer> detail = new HashMap<>();
                    detail.put("length", inYearMonth.lengthOfMonth());
                    detail.put("actual", getActualDays(inYearMonth));
                    rtn.put(TO_MONTH_FORMATTER.format(inYearMonth), detail);
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

    public Integer getTotal(String startDate, String endDate) throws ParseException {
        Dates dates = new Dates(startDate, endDate);

        List<Budget> budgets = repo.findByMonthIn(dates.getYearMonthListBetween().stream()
                .map(dates.TO_MONTH_FORMATTER::format).collect(Collectors.toList()));

        Map<String, Map<String, Integer>> detailOfMonth = dates.getDetailsOfEachMonth();
        int rtn = 0;
        for (Budget budget : budgets) {
            Map<String, Integer> budgetMonthDetail = detailOfMonth.get(budget.getMonth());
            rtn = rtn + budget.getAmount() * budgetMonthDetail.get("actual") / budgetMonthDetail.get("length");
        }
        return rtn;
    }

    class MonthAndDays {

        private String month;
        private int days;

        public MonthAndDays(String month, int days) {
            this.month = month;
            this.days = days;
        }

        public String getMonth() {
            return month;
        }

        public int getDays() {
            return this.days;
        }
    }

    private List<MonthAndDays> getMonthAndDays(String startDate, String endDate) throws ParseException {
        List<MonthAndDays> months = new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start = df.parse(startDate);
        Date end = df.parse(endDate);
        LocalDate startD = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endD = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate firstDayOfStartMonth = startD.withDayOfMonth(1);
        LocalDate firstDayOfEndMonth = endD.withDayOfMonth(1);

        if (firstDayOfStartMonth.equals(firstDayOfEndMonth)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            months.add(new MonthAndDays(formatter.format(startD), Period.between(startD, endD).plusDays(1).getDays()));
        } else {

        }

        return months;
    }
}
