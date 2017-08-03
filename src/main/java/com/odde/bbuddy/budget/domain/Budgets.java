package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        List<Budget> budgets = repo.findByMonthIn(getMonths(startDate, endDate));
        return budgets.get(0).getAmount();
    }

    private List<String> getMonths(String startDate, String endDate) throws ParseException {
        List<String> months = new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start = df.parse(startDate);
        Date end = df.parse(endDate);
        LocalDate firstDayOfStartMonth = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1);
        LocalDate firstDayOfEndMonth = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1);
        if (firstDayOfStartMonth.equals(firstDayOfEndMonth)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            months.add(formatter.format(firstDayOfStartMonth));
        }

        return months;
    }
}
