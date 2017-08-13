package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class Budgets {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
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

    public Long summarize(String start, String end) {
        String startMonth = start.substring(0, 7);
        if (start.equals(end)) {
            Long monthTotal = getMonthBudgetFromAllBudget(getAll(), startMonth);
            return monthTotal / getMonthDayCount(startMonth);
        }
        return 0L;
    }

    private int getMonthDayCount(String month) {
        return YearMonth.parse(month, MONTH_FORMATTER).lengthOfMonth();
    }

    private Long getMonthBudgetFromAllBudget(List<Budget> budgets, String month) {
        return budgets.stream()
                        .filter(budget -> budget.getMonth().equals(month))
                        .mapToLong(Budget::getAmount)
                        .sum();
    }
}
