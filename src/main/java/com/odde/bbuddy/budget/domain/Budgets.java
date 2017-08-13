package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class Budgets {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
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
        String endMonth = end.substring(0, 7);
        Long startMonthTotal = getMonthBudgetFromAllBudget(getAll(), startMonth);
        if (sameDay(start, end)) {
            return startMonthTotal / getMonthDayCount(startMonth);
        }
        if (startMonth.equals(endMonth)) {
            Period between = Period.between(LocalDate.parse(start, DATE_FORMATTER), LocalDate.parse(end, DATE_FORMATTER));
            return startMonthTotal / getMonthDayCount(startMonth) * (between.getDays() + 1);
        }
        return getAll().stream()
                .mapToLong(budget -> {
                    if (isBudgetSameMonthAs(budget, startMonth)) {
                        return budgetFromStartDay(budget, start);
                    }
                    if (isBudgetSameMonthAs(budget, endMonth)) {
                        return budgetTillEndDay(budget, end);
                    }
                    if (isBudgetBetween(budget, startMonth, endMonth)) {
                        return budget.getAmount();
                    }
                    return 0L;
                }).sum();
    }

    private long budgetTillEndDay(Budget budget, String end) {
        YearMonth budgetYM = YearMonth.parse(budget.getMonth(), MONTH_FORMATTER);
        Period between = Period.between(budgetYM.atDay(1), LocalDate.parse(end, DATE_FORMATTER));
        return budget.getOneDayAmountOfBudget() * (between.getDays() + 1);
    }

    private long budgetFromStartDay(Budget budget, String start) {
        YearMonth budgetYM = YearMonth.parse(budget.getMonth(), MONTH_FORMATTER);
        Period between = Period.between(LocalDate.parse(start, DATE_FORMATTER), budgetYM.atDay(budgetYM.lengthOfMonth()));
        return budget.getOneDayAmountOfBudget() * (between.getDays() + 1);
    }

    private boolean isBudgetBetween(Budget budget, String startMonth, String endMonth) {
        YearMonth budgetYM = YearMonth.parse(budget.getMonth(), MONTH_FORMATTER);
        YearMonth startYM = YearMonth.parse(startMonth, MONTH_FORMATTER);
        YearMonth endYM = YearMonth.parse(endMonth, MONTH_FORMATTER);
        return budgetYM.isAfter(startYM) && budgetYM.isBefore(endYM);
    }

    private boolean isBudgetSameMonthAs(Budget budget, String month) {
        return YearMonth.parse(budget.getMonth(), MONTH_FORMATTER).equals(YearMonth.parse(month, MONTH_FORMATTER));
    }

    private boolean sameDay(String start, String end) {
        return start.equals(end);
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
