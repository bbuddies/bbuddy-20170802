package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import com.odde.bbuddy.common.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        }
        else {
            repo.save(budget);
        }
    }

    public List<Budget> getAll() {
        return repo.findAll();
    }

    public BigDecimal getBudgetInDate(DateRange searchRange) throws ParseException {
        BigDecimal sumAmount = BigDecimal.ZERO;
        List<Budget> budgets = getAll();

        for (Budget budget : budgets) {
            BudgetDetail detail = new BudgetDetail(budget);

            for (int i = 0; i < detail.dayLength; i++) {
                if (searchRange.isDateInRange(detail.getDate())) {
                    sumAmount = sumAmount.add(detail.avgAmount);
                }
                detail.nextDate();
            }
        }

        return sumAmount.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    class BudgetDetail {
        private final int dayLength;
        private final BigDecimal avgAmount;
        private final Calendar c;

        public BudgetDetail(Budget budget) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            c = Calendar.getInstance();
            c.setTime(sdf.parse(budget.getMonth() + "-01"));
            dayLength = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            avgAmount = BigDecimal.valueOf(budget.getAmount() * 1d / dayLength);
        }

        public void nextDate() {
            c.add(Calendar.DATE, 1);
        }

        public Date getDate() {
            return c.getTime();
        }
    }

}
