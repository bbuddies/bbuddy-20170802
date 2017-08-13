package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
        List<Budget> budgetList = givenBudgets(
                budget("2017-10", 1000),
                budget("2017-11", 2000));

        List<Budget> allBudgets = budgets.getAll();

        assertEquals(budgetList, allBudgets);
    }

    @Test
    public void update_by_repo() {
        int newAmount = 4000;
        String month = "2017-10";
        Long id = 1L;
        when(repo.findByMonth(month)).thenReturn(
                existedBudget(id, month, 10000));

        budgets.save(budget(month, newAmount));

        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getAmount()).isEqualTo(newAmount);
    }

    @Test
    public void query_with_one_day() {
        givenBudgets(budget("2017-04", 3000));

        Long total = budgets.summarize("2017-04-01", "2017-04-01");

        assertThat(total).isEqualTo(100);
    }

    @Test
    public void query_budget_with_two_day_in_one_month() {
        givenBudgets(budget("2017-04", 3000));

        Long total = budgets.summarize("2017-04-01", "2017-04-02");

        assertThat(total).isEqualTo(200);
    }

    @Test
    public void query_budget_across_2_month() {
        givenBudgets(budget("2017-04", 3000),
                budget("2017-05", 3100));

        Long total = budgets.summarize("2017-04-01", "2017-05-31");

        assertThat(total).isEqualTo(6100);
    }

    private Budget existedBudget(Long id, String month, int oldAmount) {
        Budget budget = budget(month, oldAmount);
        budget.setId(id);
        return budget;
    }

    private List<Budget> givenBudgets(Budget... budget) {
        List<Budget> budgetList = Arrays.asList(budget);
        when(repo.findAll()).thenReturn(budgetList);
        return budgetList;
    }

    private Budget budget(String month, int amount) {
        Budget budget = new Budget();
        budget.setMonth(month);
        budget.setAmount(amount);
        return budget;
    }
}
