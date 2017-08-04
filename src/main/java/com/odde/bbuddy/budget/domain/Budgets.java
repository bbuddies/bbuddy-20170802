package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
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

    public BigDecimal getBudgetInDate(String startDate,
                                      String endDate) throws ParseException {
        BigDecimal total = BigDecimal.ZERO;
        List<Budget> budgets = getAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);

        Calendar c = Calendar.getInstance();

        for (Budget budget : budgets) {
            c.setTime(sdf.parse(budget.getMonth() + "-01"));

            int lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            BigDecimal avgAmount = BigDecimal.valueOf(budget.getAmount() * 1d / lastDate);

            // sum the avg budget in month when date in range
            for (int i = 0; i < lastDate; i++) {
                Date date = c.getTime();

                if (isDateInRange(date, start, end)) {
                    total = total.add(avgAmount);
                }
                c.add(Calendar.DATE, 1);
            }
        }

        return total.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isDateInRange(Date date,
                                  Date start,
                                  Date end) {
        return start.equals(date) || end.equals(date) || (start.before(date) && date.before(end));
    }
}
