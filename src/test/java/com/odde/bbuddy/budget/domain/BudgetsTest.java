package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BudgetsTest {
    BudgetRepo repo = mock(BudgetRepo.class);
    Budgets budgets = new Budgets(repo);

    @Test
    public void save_by_repo() throws Exception {
        Budget budget = budget("2017-10", 1000);

        budgets.save(budget);

        verify(repo).save(budget);
    }

    @Test
    public void get_all_budgets_from_repo() throws Exception {
        List<Budget> budgetList = givenBudgets(budget("2017-10", 1000), budget("2017-11", 2000));

        List<Budget> allBudgets = budgets.getAll();

        assertEquals(budgetList, allBudgets);
    }

    @Test
    public void update_by_repo() {
        int newAmount = 4000;
        String month = "2017-10";
        Long id = 1L;
        when(repo.findByMonth(month)).thenReturn(existedBudget(id, month, 10000));

        budgets.save(budget(month, newAmount));

        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue()
                         .getId()).isEqualTo(id);
        assertThat(captor.getValue()
                         .getAmount()).isEqualTo(newAmount);
    }

    private Budget existedBudget(Long id,
                                 String month,
                                 int oldAmount) {
        Budget budget = budget(month, oldAmount);
        budget.setId(id);
        return budget;
    }

    private List<Budget> givenBudgets(Budget... budget) {
        List<Budget> budgetList = Arrays.asList(budget);
        when(repo.findAll()).thenReturn(budgetList);
        return budgetList;
    }

    private Budget budget(String month,
                          int amount) {
        Budget budget = new Budget();
        budget.setMonth(month);
        budget.setAmount(amount);

        return budget;
    }

    @Test
    public void _12月有預算資料找12月1號到12月10號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-12", 3100));
        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-12-01", "2017-12-10");
        assertEquals(BigDecimal.valueOf(1000), total);
    }

    @Test
    public void _11月資料_找10月1號到11月10號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-11", 3000));
        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-10-01", "2017-11-10");
        assertEquals(BigDecimal.valueOf(1000), total);
    }

    @Test
    public void _11and12月資料_找10月1號到隔年1月1號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-11", 3100), new Budget("2017-12", 3100));
        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-10-01", "2018-01-01");
        assertEquals(BigDecimal.valueOf(6200), total);
    }

    @Test
    public void _10and12月資料_找10月15號到12月15號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-10", 3100), new Budget("2017-12", 3100));
        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-10-15", "2017-12-15");
        assertEquals(BigDecimal.valueOf(3200), total);
    }

    @Test
    public void _11and12月資料_找11月20號到12月15號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-11", 3100), new Budget("2017-12", 3100));
        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-11-20", "2017-12-15");
        assertEquals(BigDecimal.valueOf(2637), total);
    }

    @Test
    public void _11and12月資料_找11月20號到隔年01月15號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-11", 3100), new Budget("2017-12", 3100));

        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-11-20", "2018-01-15");
        assertEquals(BigDecimal.valueOf(4237), total);
    }

    @Test
    public void _12月資料_找11月01號到11月10號() throws Exception {
        List<Budget> budgets = Arrays.asList(new Budget("2017-12", 3100));

        BigDecimal total = getTotalBudgetFromDateRange(budgets, "2017-11-01", "2017-11-10");
        assertEquals(BigDecimal.valueOf(0), total);
    }

    private BigDecimal getTotalBudgetFromDateRange(List<Budget> budgets,
                                                   String startDate,
                                                   String endDate) throws ParseException {
        return this.budgets.getBudgetInDate(budgets, startDate, endDate);
    }
}
