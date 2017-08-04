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

        for (Budget budget : budgets) {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            format1.setLenient(false);
            Calendar c = Calendar.getInstance();

            String month = budget.getMonth() + "-01"; // 2017-12
            Integer amount = budget.getAmount();

            Date monthStart = format1.parse(month);
            c.setTime(monthStart);

            Date start = format1.parse(startDate);
            Date end = format1.parse(endDate);

            int lastDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            BigDecimal avgAmount = BigDecimal.valueOf(amount * 1d / lastDate);

            for (int i = 0; i < lastDate; i++) {
                Date date = c.getTime();

                if (start.equals(date) || end.equals(date) || (start.before(date) && date.before(end))) {
                    total = total.add(avgAmount);
                }
                c.add(Calendar.DATE, 1);
            }
        }

        return total.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
