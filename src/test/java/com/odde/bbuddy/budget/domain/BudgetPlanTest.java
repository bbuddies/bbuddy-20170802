package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BudgetPlanTest {
    BudgetRepo repo = mock(BudgetRepo.class);
    BudgetPlan budgetPlan = new BudgetPlan(repo);
    @Test
    public void no_budget() throws Exception {
        givenBudgets();

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 3), LocalDate.of(2017, 8, 15))))
        .isEqualTo(0);
    }

    @Test
    public void one_day_budget() throws Exception {
        givenBudgets(new Budget("2017-08", 31));

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 3), LocalDate.of(2017, 8, 3))))
        .isEqualTo(1);
    }

    @Test
    public void one_day_after_budget() throws Exception {
        givenBudgets(new Budget("2017-07", 31));

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 3), LocalDate.of(2017, 8, 3))))
        .isEqualTo(0);
    }

    @Test
    public void one_day_before_budget() throws Exception {
        givenBudgets(new Budget("2017-09", 30));

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 3), LocalDate.of(2017, 8, 3))))
        .isEqualTo(0);
    }

    @Test
    public void two_day_in_budget() throws Exception {
        givenBudgets(new Budget("2017-08", 31));

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 3), LocalDate.of(2017, 8, 4))))
        .isEqualTo(2);
    }

    @Test
    public void two_day_partially_before_budget() throws Exception {
        givenBudgets(new Budget("2017-08", 31));

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 7, 31), LocalDate.of(2017, 8, 1))))
        .isEqualTo(1);
    }

    @Test
    public void two_day_partially_after_budget() throws Exception {
        givenBudgets(new Budget("2017-08", 31));

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 31), LocalDate.of(2017, 9, 1))))
        .isEqualTo(1);
    }

    @Test
    public void period_across_two_budgets() throws Exception {
        givenBudgets(
                new Budget("2017-08", 31),
                new Budget("2017-09", 30)
                );

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 31), LocalDate.of(2017, 9, 1))))
        .isEqualTo(2);
    }

    @Test
    public void various_budget_amount() throws Exception {
        givenBudgets(
                new Budget("2017-08", 31),
                new Budget("2017-09", 300)
                );

        assertThat(budgetPlan.query(
                new Period(LocalDate.of(2017, 8, 31), LocalDate.of(2017, 9, 1))))
        .isEqualTo(11);
    }

    private void givenBudgets(Budget... budgets) {
        when(repo.findAll()).thenReturn(Arrays.asList(budgets));
    }
}
