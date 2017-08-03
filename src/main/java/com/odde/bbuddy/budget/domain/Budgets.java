package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public Integer getTotal(String startDate, String endDate) throws ParseException {
        List<MonthAndDays> monthAndDays = getMonthAndDays(startDate, endDate);

        List<Budget> budgets = repo.findByMonthIn(monthAndDays.stream().map(MonthAndDays::getMonth).collect(Collectors.toList()));

        return budgets.stream().mapToInt(budget -> {
            if (budget.getMonth().equals(monthAndDays.get(0).getMonth())) {
                return BigInteger.valueOf(budget.getAmount()).divide(BigInteger.valueOf(30))
                        .multiply(BigInteger.valueOf(monthAndDays.get(0).getDays())).intValue();
            }
            return 0;
                }
        ).sum();
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
